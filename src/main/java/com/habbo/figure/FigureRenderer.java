package com.habbo.figure;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Renders a Habbo figure from a figure string.
 */
public class FigureRenderer {
    private final FigureData figureData;
    private final AssetManager assetManager;

    // Canvas size for the figure - matches official Habbo "m" size with proper margins
    private static final int CANVAS_WIDTH = 64;
    private static final int CANVAS_HEIGHT = 110;
    private static final int ORIGIN_X = 0;   // Figure registration point X (sprites extend right from here)
    private static final int ORIGIN_Y = 100; // Figure registration point Y (near bottom for feet)

    // Default rendering settings
    private String action = "std";
    private int direction = 2;
    private int frame = 0;

    public FigureRenderer(FigureData figureData, AssetManager assetManager) {
        this.figureData = figureData;
        this.assetManager = assetManager;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    /**
     * Render a figure from a figure string.
     */
    public BufferedImage render(String figureString) {
        // Parse the figure string
        List<FigureStringParser.FigurePartInfo> figurePartInfos = FigureStringParser.parse(figureString);

        // Collect all parts to render
        List<RenderablePart> renderableParts = new ArrayList<>();

        // Track hidden layers
        Set<String> hiddenLayers = new HashSet<>();

        // First pass: collect hidden layers
        for (FigureStringParser.FigurePartInfo info : figurePartInfos) {
            FigureSetType setType = figureData.getSetType(info.getSetType());
            if (setType == null) continue;

            FigureSet set = setType.getSet(info.getSetId());
            if (set == null) continue;

            hiddenLayers.addAll(set.getHiddenLayers());
        }

        // Second pass: collect renderable parts
        for (FigureStringParser.FigurePartInfo info : figurePartInfos) {
            FigureSetType setType = figureData.getSetType(info.getSetType());
            if (setType == null) {
                System.err.println("Unknown set type: " + info.getSetType());
                continue;
            }

            FigureSet set = setType.getSet(info.getSetId());
            if (set == null) {
                System.err.println("Unknown set ID: " + info.getSetId() + " for type: " + info.getSetType());
                continue;
            }

            // Get color from palette
            FigureColor color = figureData.getColorForSetType(info.getSetType(), info.getColorId());
            FigureColor secondaryColor = null;
            if (info.getSecondaryColorId() > 0) {
                secondaryColor = figureData.getColorForSetType(info.getSetType(), info.getSecondaryColorId());
            }

            // Add all parts from this set
            for (FigurePart part : set.getParts()) {
                // Skip hidden layers
                if (hiddenLayers.contains(part.getType())) {
                    continue;
                }

                // Determine which color to use based on part index
                FigureColor partColor = (part.getIndex() == 1 && secondaryColor != null) ? secondaryColor : color;

                // Determine if color should be applied
                // Eyes (ey) and face (fc) should not be tinted with skin color
                boolean shouldApplyColor = part.isColorable() && set.isColorable();
                String partType = part.getType();
                if (info.getSetType().equals("hd") && (partType.equals("ey") || partType.equals("fc"))) {
                    shouldApplyColor = false;
                }

                renderableParts.add(new RenderablePart(
                        part,
                        partColor,
                        shouldApplyColor
                ));
            }
        }

        // Sort by draw order (direction-aware), with colored parts drawn on top of uncolored parts (for same draw order)
        renderableParts.sort(Comparator
                .comparingInt((RenderablePart p) -> FigureStringParser.getDrawOrder(p.part.getType(), direction))
                .thenComparingInt(p -> p.applyColor ? 1 : 0));

        // Create canvas
        BufferedImage canvas = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // Render each part
        for (RenderablePart rp : renderableParts) {
            renderPart(g, rp);
        }

        g.dispose();
        return canvas;
    }

    /**
     * Render a single part onto the canvas.
     */
    private void renderPart(Graphics2D g, RenderablePart rp) {
        String assetName = AssetManager.buildAssetName(action, rp.part.getType(), rp.part.getId(), direction, frame);
        BufferedImage image = assetManager.loadImage(assetName);
        int usedDirection = direction;

// 1. Try requested direction first
        assetName = AssetManager.buildAssetName(action, rp.part.getType(), rp.part.getId(), direction, frame);
        image = assetManager.loadImage(assetName);

        // 2. Fallback: try all rotations 0–7
        if (image == null) {
            for (int d = 0; d <= 7; d++) {
                if (d == direction) continue;

                assetName = AssetManager.buildAssetName(action, rp.part.getType(), rp.part.getId(), d, frame);
                image = assetManager.loadImage(assetName);
                if (image != null) {
                    usedDirection = d;
                    break;
                }
            }
        }

        // 3. Fallback actions + rotations
        if (image == null) {
            String[] fallbackActions = {"std", "spk"};
            for (String fallbackAction : fallbackActions) {
                if (fallbackAction.equals(action)) continue;

                for (int d = 0; d <= 7; d++) {
                    assetName = AssetManager.buildAssetName(
                            fallbackAction,
                            rp.part.getType(),
                            rp.part.getId(),
                            d,
                            frame
                    );
                    image = assetManager.loadImage(assetName);
                    if (image != null) {
                        usedDirection = d;
                        break;
                    }
                }
                if (image != null) break;
            }
        }

        if (image == null) {
            return; // Nothing found
        }

        // Get offset
        AssetOffset offset = assetManager.getOffset(assetName);
        int offsetX = offset != null ? offset.getX() : 0;
        int offsetY = offset != null ? offset.getY() : 0;

        // Apply color tinting if needed
        BufferedImage coloredImage = image;
        if (rp.applyColor && rp.color != null) {
            coloredImage = applyColorTint(image, rp.color.getColor());
            System.out.println("  Tinting " + rp.part.getType() + "_" + rp.part.getId() +
                " with color " + rp.color.getHexColor() + " applyColor=" + rp.applyColor);
        } else {
            System.out.println("  NOT tinting " + rp.part.getType() + "_" + rp.part.getId() +
                " applyColor=" + rp.applyColor + " color=" + (rp.color != null ? rp.color.getHexColor() : "null"));
        }

        // Calculate position using Flash/Habbo offset convention:
        // Offset represents the registration point position within the sprite
        // To draw sprite with reg point at (ORIGIN_X, ORIGIN_Y):
        // - Subtract negative offsetX to move sprite right
        // - Subtract positive offsetY to move sprite up
        int x = ORIGIN_X - offsetX;
        int y = ORIGIN_Y - offsetY;

        // Draw the image
        g.drawImage(coloredImage, x, y, null);
    }

    /**
     * Apply color tinting to an image using Habbo-style colorization.
     * Uses the luminance of the original pixel to shade the tint color.
     * White pixels become the tint color, black stays black, grays become shaded versions.
     */
    private BufferedImage applyColorTint(BufferedImage original, Color tintColor) {
        BufferedImage tinted = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);

        int tintR = tintColor.getRed();
        int tintG = tintColor.getGreen();
        int tintB = tintColor.getBlue();

        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                int argb = original.getRGB(x, y);
                int alpha = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                // Skip fully transparent pixels
                if (alpha == 0) {
                    tinted.setRGB(x, y, 0);
                    continue;
                }

                // Calculate luminance using the maximum channel (works well for grayscale sprites)
                // This treats the brightest channel as the "whiteness" level
                int luminance = Math.max(r, Math.max(g, b));

                // Apply tint: scale the tint color by the luminance
                int newR = (tintR * luminance) / 255;
                int newG = (tintG * luminance) / 255;
                int newB = (tintB * luminance) / 255;

                int newArgb = (alpha << 24) | (newR << 16) | (newG << 8) | newB;
                tinted.setRGB(x, y, newArgb);
            }
        }

        return tinted;
    }

    /**
     * Helper class to hold a part with its color info for rendering.
     */
    private static class RenderablePart {
        final FigurePart part;
        final FigureColor color;
        final boolean applyColor;

        RenderablePart(FigurePart part, FigureColor color, boolean applyColor) {
            this.part = part;
            this.color = color;
            this.applyColor = applyColor;
        }
    }
}
