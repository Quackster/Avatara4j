package net.h4bbo.avatara4j.figure;

import net.h4bbo.avatara4j.extensions.ColorExtensions;
import net.h4bbo.avatara4j.figure.converter.FigureConverter;
import net.h4bbo.avatara4j.figure.readers.FiguredataReader;
import net.h4bbo.avatara4j.figure.readers.ManifestReader;
import net.h4bbo.avatara4j.figure.types.FigureColor;
import net.h4bbo.avatara4j.figure.types.FigurePart;
import net.h4bbo.avatara4j.figure.types.FigureSet;
import net.h4bbo.avatara4j.figure.types.FigureSetType;
import net.h4bbo.avatara4j.util.FileUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public class Avatar {
    public String figure;
    public String size;
    public boolean isSmall;
    public int bodyDirection;
    public int headDirection;
    public FiguredataReader figuredataReader;
    public String action;
    public String gesture;
    public int frame;
    public int carryDrink;

    public BufferedImage bodyCanvas;
    public BufferedImage faceCanvas;
    public BufferedImage drinkCanvas;

    public int CANVAS_HEIGHT = 110;
    public int CANVAS_WIDTH = 64;
    public boolean renderEntireFigure;
    public boolean cropImage = false;

    public Avatar(FiguredataReader figuredataReader, String figure, String size, int bodyDirection, int headDirection,
                  String action, String gesture, boolean headOnly, int frame, int carryDrink, boolean cropImage) {

        if (figure.matches("\\d+")) {
            figure = FigureConverter.getInstance().convertOldToNew(figure);
            // System.out.println("Converting old figure to new: " + figure);
        }

        this.figure = figure;
        this.size = size.toLowerCase();
        this.isSmall = !(size.equalsIgnoreCase("b") || size.equalsIgnoreCase("l"));
        this.bodyDirection = bodyDirection;
        this.headDirection = headDirection;
        this.figuredataReader = figuredataReader;
        this.renderEntireFigure = !headOnly;
        this.cropImage = cropImage;

        if (isSmall) {
            this.CANVAS_WIDTH /= 2;
            this.CANVAS_HEIGHT /= 2;
        }

        this.action = (action != null) ? action : "std";
        this.gesture = (gesture != null) ? gesture : "std";

        if (this.action.equals("lay")) {
            int temp = CANVAS_HEIGHT;
            CANVAS_HEIGHT = CANVAS_WIDTH;
            CANVAS_WIDTH = temp;
        }

        bodyCanvas = ColorExtensions.createTransparentImage(CANVAS_WIDTH, CANVAS_HEIGHT);
        faceCanvas = ColorExtensions.createTransparentImage(CANVAS_WIDTH, CANVAS_HEIGHT);
        drinkCanvas = ColorExtensions.createTransparentImage(CANVAS_WIDTH, CANVAS_HEIGHT);

        this.frame = frame - 1;
        this.carryDrink = carryDrink;

        if (action.equals("lay")) {
            this.gesture = "lay";
            this.action = "lay";
            this.headDirection = this.bodyDirection;

            if (this.bodyDirection != 2 && this.bodyDirection != 4)
                this.bodyDirection = 2;

            if (this.headDirection != 2 && this.headDirection != 4)
                this.headDirection = 2;

            this.carryDrink = 0;
        }
    }

    public byte[] run() {
        List<AvatarAsset> buildQueue = buildDrawQueue();

        if (buildQueue == null)
            return null;

        return drawImage(buildQueue);
    }

    private byte[] drawImage(List<AvatarAsset> buildQueue) {
        for (AvatarAsset asset : buildQueue) {
            if (!renderEntireFigure && !isHead(asset.getPart().getType()))
                continue;

            drawAsset(buildQueue, bodyCanvas, faceCanvas, drinkCanvas, asset);
        }

        if (headDirection == 4 || headDirection == 6 || headDirection == 5) {
            faceCanvas = flipImage(faceCanvas, true);
        }

        if (bodyDirection == 4 || bodyDirection == 6 || bodyDirection == 5) {
            bodyCanvas = flipImage(bodyCanvas, true);
            drinkCanvas = flipImage(drinkCanvas, true);
        }

        // Draw face and drink layers on body layer with proper graphics disposal
        Graphics2D g = bodyCanvas.createGraphics();
        try {
            g.drawImage(faceCanvas, 0, 0, null);
            g.drawImage(drinkCanvas, 0, 0, null);
        } finally {
            g.dispose();
        }

        BufferedImage finalCanvas = renderEntireFigure ? bodyCanvas : faceCanvas;
        return renderImage(finalCanvas);
    }

    private byte[] renderImage(BufferedImage croppedBitmap) {
        if (size.equals("l")) {
            return resizeAndToBytes(croppedBitmap);
        } else {
            return toBytes(croppedBitmap);
        }
    }

    // Function to resize image using NEAREST_NEIGHBOR
    public static byte[] resizeAndToBytes(BufferedImage image) {
        int newWidth = image.getWidth() * 2;
        int newHeight = image.getHeight() * 2;

        // Create new image with desired size
        BufferedImage resized = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D g2d = resized.createGraphics();
        try {
            // Use NEAREST_NEIGHBOR interpolation
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        } finally {
            g2d.dispose();
        }

        // Convert to bytes (PNG)
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            ImageIO.write(resized, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] toBytes(BufferedImage bitmap) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bitmap, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    // Flip image horizontally if needed
    private static BufferedImage flipImage(BufferedImage img, boolean horizontal) {
        AffineTransform tx;
        if (horizontal) {
            tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-img.getWidth(), 0);
        } else {
            tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -img.getHeight());
        }
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(img, null);
    }

    public void drawAsset(
            List<AvatarAsset> buildQueue,
            BufferedImage bodyCanvas,
            BufferedImage faceCanvas,
            BufferedImage drinkCanvas,
            AvatarAsset asset
    ) {try (InputStream stream = asset.getFileName()) {
        final BufferedImage image = ImageIO.read(stream);

            // Skip if the asset's type is hidden by any buildQueue item
            boolean isHidden = buildQueue.stream().anyMatch(
                    x -> x.getSet().getHiddenLayers().contains(asset.getPart().getType())
            );
            if (isHidden) return;

            if (!"ey".equals(asset.getPart().getType())) {
                if (asset.getPart().isColorable()) {
                    String[] parts = asset.getParts();
                    if (parts.length > 2 && parts[2] != null && !parts[2].isEmpty() && isNumeric(parts[2])) {
                        String paletteId = parts[2];
                        if (FiguredataReader.getInstance().getFigureSetTypes().containsKey(parts[0])) {
                            FigureSetType figureTypeSet = FiguredataReader.getInstance().getFigureSetTypes().get(parts[0]);

                            List<FigureColor> palette = FiguredataReader.getInstance().getFigurePalettes().get(figureTypeSet.getPaletteId());
                            Optional<FigureColor> colourData = palette.stream().filter(x -> x.getColourId().equals(paletteId)).findFirst();

                            colourData.ifPresent(figureColor -> {
                                ColorExtensions.tintImage(image, figureColor.getHexColor(), 255);
                            });
                        }
                    }
                }
            } else {
                ColorExtensions.tintImage(image, "FFFFFF", 255);
            }

            // Draw to the correct canvas
            if (isHead(asset.getPart().getType())) {
                Point point = new Point(
                        faceCanvas.getWidth() - asset.getImageX(),
                        faceCanvas.getHeight() - asset.getImageY()
                );
                point = mutatePoint(point, faceCanvas);

                drawOnCanvas(faceCanvas, image, point.x, point.y);
            } else {
                Point point = new Point(
                        bodyCanvas.getWidth() - asset.getImageX(),
                        bodyCanvas.getHeight() - asset.getImageY()
                );
                point = mutatePoint(point, bodyCanvas);

                if (!asset.isDrinkCanvas()) {
                    drawOnCanvas(bodyCanvas, image, point.x, point.y);
                } else {
                    drawOnCanvas(drinkCanvas, image, point.x, point.y);
                }
            }
        } catch (Exception e) {
            // handle as needed (swallowing for parity with C# catch)
        } finally {
            // Note: We don't dispose the loaded image here as BufferedImage from ImageIO.read()
            // doesn't need explicit disposal - it will be garbage collected
            // Only Graphics2D objects need explicit disposal
        }
    }

    private boolean isNumeric(String part) {
        try {
            Integer.parseInt(part);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    // Utility to draw image on canvas - Fixed to properly dispose graphics
    private static void drawOnCanvas(BufferedImage canvas, BufferedImage image, int x, int y) {
        Graphics2D g = canvas.createGraphics();
        try {
            g.drawImage(image, x, y, null);
        } finally {
            g.dispose();
        }
    }

    private Point mutatePoint(Point pt, BufferedImage canvas) {
        int x = pt.x + 1;
        int y = pt.y + 2;
        return new Point(x, y);
    }

    // Check if a figure part is head
    public boolean isHead(String figurePart) {
        return figurePart.contains("hr") || figurePart.contains("hd") || figurePart.contains("he")
                || figurePart.contains("ha") || figurePart.contains("ea") || figurePart.contains("fa")
                || figurePart.contains("ey") || figurePart.contains("fc");
    }

    private List<AvatarAsset> buildDrawQueue() {
        List<AvatarAsset> tempQueue = new ArrayList<>();
        Map<String, String> figureData = new HashMap<>();

        for (String data : figure.split("\\.")) {
            String[] parts = data.split("-");
            figureData.put(parts[0], String.join("-", parts));
        }

        for (String data : figureData.values()) {
            String[] parts = data.split("-");
            if (parts.length < 2) return null;

            // You will need to port this: setList = FiguredataReader.FigureSets.Values.Where(x => x.Id == parts[1]).ToList();
            List<FigureSet> setList = new ArrayList<>();
            for (FigureSet fs : figuredataReader.getFigureSets().values()) {
                if (fs.getId().equals(parts[1])) {
                    setList.add(fs);
                }
            }
            if (!setList.isEmpty()) {
                for (FigureSet set : setList) {
                    for (FigurePart part : set.getFigureParts()) {
                        AvatarAsset t = loadFigureAsset(parts, part, set);
                        if (t == null) continue;
                        tempQueue.add(t);
                    }
                }
            }
        }

        // Calculate head render order
        List<AvatarAsset> headRenderList = new ArrayList<>();
        for (AvatarAsset x : tempQueue) {
            if (isHead(x.part.getType())) {
                headRenderList.add(x);
            }
        }
        int headRenderOrder = headRenderList.size() > 0
                ? Collections.max(headRenderList, Comparator.comparingInt(a -> a.renderOrder)).renderOrder
                : 1;

        if (carryDrink > 0) {
            AvatarAsset carryItemAsset = this.loadCarryItemAsset(carryDrink);
            if (carryItemAsset != null) {
                if (bodyDirection == 1 || bodyDirection == 5 || bodyDirection == 6 || bodyDirection == 0) {
                    carryItemAsset.isDrinkCanvas = false;
                    carryItemAsset.renderOrder = 0;
                } else {
                    carryItemAsset.renderOrder = headRenderOrder + 1;
                    carryItemAsset.isDrinkCanvas = true;
                }
                tempQueue.add(carryItemAsset);

                if (action.equals("drk")) {
                    for (AvatarAsset asset : tempQueue) {
                        if (asset == carryItemAsset) continue;
                        if (asset.name.contains("_drk_")) {
                            asset.renderOrder = 100;
                            asset.isDrinkCanvas = true;
                        }
                    }
                }
            }
        }

        tempQueue.sort(Comparator.comparingInt(a -> a.renderOrder));
        return tempQueue;
    }

    private AvatarAsset loadCarryItemAsset(int carryId) {
        int direction = bodyDirection;
        if (bodyDirection == 4) direction = 2;
        if (bodyDirection == 6) direction = 0;
        if (bodyDirection == 5) direction = 1;

        FigurePart part = new FigurePart("0", "ri", false, 0);
        FigureSet set = new FigureSet("ri", "", "", false, false, false);

        String prefix = isSmall ? "sh" : "h";
        AvatarAsset asset = locateAsset(prefix + "_" + action + "_ri_" + carryId + "_" + direction + "_0", null, part, set);
        if (asset == null)
            asset = locateAsset(prefix + "_crr_ri_" + carryId + "_" + direction + "_0", null, part, set);
        if (asset == null)
            asset = locateAsset(prefix + "_std_ri_" + carryId + "_0_0", null, part, set);

        return asset;
    }

    private AvatarAsset loadFigureAsset(String[] parts, FigurePart part, FigureSet set) {
        int direction;
        String gesture;

        if (isHead(part.getType())) {
            direction = this.headDirection;
            gesture = this.gesture;
            if (headDirection == 4) direction = 2;
            if (headDirection == 6) direction = 0;
            if (headDirection == 5) direction = 1;
        } else {
            direction = bodyDirection;
            gesture = action;
            if (bodyDirection == 4) direction = 2;
            if (bodyDirection == 6) direction = 0;
            if (bodyDirection == 5) direction = 1;
        }

        if (direction == 1 && part.getType().equals("ls")) return null;

        if (action.equals("lay") && bodyDirection == 4) direction = 2;
        if (carryDrink > 0 && !action.equals("lay") && !action.equals("drk")) {
            if (part.getType().equals("ls") || part.getType().equals("lh")) gesture = "std";
        }

        AvatarAsset asset = null;
        String prefix = isSmall ? "sh" : "h";
        if (carryDrink > 0 && (part.getType().equals("rs") || part.getType().equals("rh"))
                && !action.equals("drk") && !action.equals("crr")) {
            asset = locateAsset(prefix + "_crr_" + part.getType() + "_" + part.getId() + "_" + direction + "_0", parts, part, set);
        }
        if (asset == null)
            asset = locateAsset(prefix + "_" + gesture + "_" + part.getType() + "_" + part.getId() + "_" + direction + "_" + frame, parts, part, set);

        if (asset == null)
            asset = locateAsset(prefix + "_std_" + part.getType() + "_" + part.getId() + "_" + direction + "_" + frame, parts, part, set);

        if (asset == null)
            asset = locateAsset(prefix + "_std_" + part.getType() + "_" + part.getId() + "_" + direction + "_0", parts, part, set);

        if (isSmall) {
            if (asset == null)
                asset = locateAsset(prefix + "_std_" + part.getType() + "_1_" + direction + "_" + frame, parts, part, set);
        }
        return asset;
    }

    private AvatarAsset locateAsset(String assetName, String[] parts, FigurePart part, FigureSet set) {
        String offsets = ManifestReader.getInstance().getParts().get(assetName);
        if (offsets == null) return null;

        List<InputStream> streams = FileUtil.getInstance().solveFile("figuredata/images/", assetName);
        Optional<InputStream> file = streams.stream().findFirst();

        if (!file.isPresent())
            return null;

        // if (file.get().isEmpty()) return null;

        String[] offsetParts = offsets.split(",");
        int offsetX = Integer.parseInt(offsetParts[0]);
        int offsetY = Integer.parseInt(offsetParts[1]);

        return new AvatarAsset(
                this.isSmall, this.action, assetName, file.orElse(null),
                offsetX, offsetY, part, set, CANVAS_HEIGHT, CANVAS_WIDTH, parts
        );
    }

    /**
     * Clean up resources. Call this when you're done with the Avatar instance
     * to ensure all BufferedImages are properly disposed.
     */
    public void dispose() {
        // BufferedImages don't need explicit disposal, but we can set them to null
        // to help with garbage collection
        bodyCanvas = null;
        faceCanvas = null;
        drinkCanvas = null;
    }
}