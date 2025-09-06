package net.h4bbo.avatara4j.badges;

import net.h4bbo.avatara4j.badges.Extensions.ColorExtensions;
import net.h4bbo.avatara4j.badges.Extensions.RenderType;
import net.h4bbo.avatara4j.badges.Extensions.StringExtensions;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

/**
 * Badge rendering class.
 *
 * This class has been translated from C# to Java.
 * The functionality and structure have been preserved as closely as possible.
 * Some library‑specific features (e.g. OctreeQuantizer) have been omitted
 * due to lack of a direct Java equivalent.  The resulting GIF will be
 * produced using the standard ImageIO writer which may use a different
 * quantization algorithm.
 */
public class Badge {

    public static final int CANVAS_HEIGHT = 39;
    public static final int CANVAS_WIDTH = 39;

    public String[] colors = new String[]{
        "ffd601",
        "ec7600",
        "84de00",
        "589a00",
        "50c1fb",
        "006fcf",
        "ff98e3",
        "f334bf",
        "ff2d2d",
        "af0a0a",
        "ffffff",
        "c0c0c0",
        "373737",
        "fbe7ac",
        "977641",
        "c2eaff",
        "fff165",
        "aaff7d"
    };

    public int[] baseTemplateProxies = new int[]{
        10,
        11,
        12,
        17,
        18,
        19,
        21,
        23
    };

    public int[] templateProxies = new int[]{
        3, 4, 5, 7, 13, 14, 17, 18,
        21, 23, 28, 29, 30, 31, 34, 35,
        40, 41, 42, 50, 51, 52, 53, 55,
        61, 67
    };

    private List<BadgePart> badgeParts;
    private BadgeSettings badgeSettings;

    public List<BadgePart> getParts() {
        return badgeParts;
    }

    public BadgeSettings getSettings() {
        return badgeSettings;
    }

    public Badge(BadgeSettings settings) {
        // BadgeResourceManager.Load(); // placeholder – implementation omitted
        this.badgeSettings = settings;
        this.badgeParts = new ArrayList<>();
    }

    /**
     * Parse a badge code into a Badge instance.
     */
    public static Badge parseBadgeData(BadgeSettings settings, String badgeCode) {
        Badge badge = new Badge(settings);

        Pattern pattern = Pattern.compile("[bs][0-9]{4,6}");
        Matcher matcher = pattern.matcher(badgeCode);

        while (matcher.find()) {
            String partCode = matcher.group();
            boolean shortMethod = partCode.length() <= 6;

            char partType = partCode.charAt(0);
            int partId = 0;
            int partColor = 0;
            int partPosition = 0;

            if (settings.isShockwaveBadge()) {
                if (shortMethod) {
                    partId = Integer.parseInt(partCode.substring(1, 3));
                    partColor = Integer.parseInt(partCode.substring(3, 5));
                    partPosition = Integer.parseInt(partCode.length() > 5
                            ? partCode.substring(5)
                            : "-1");
                } else {
                    partId = Integer.parseInt(partCode.substring(1, 4));
                    partColor = Integer.parseInt(partCode.substring(4, 6));
                    partPosition = Integer.parseInt(partCode.substring(6));
                }
            } else {
                if (partType == 'b') {
                    partId = Integer.parseInt(partCode.substring(1, 4));
                    partColor = Integer.parseInt(partCode.substring(4));
                    partPosition = -1;
                } else {
                    partId = Integer.parseInt(partCode.substring(1, 4));
                    int colorStart = 4;
                    int colorEnd = partCode.length() - 1;
                    partColor = Integer.parseInt(partCode.substring(colorStart, colorEnd));
                    partPosition = Integer.parseInt(partCode.substring(partCode.length() - 1));
                }
            }

            badge.getParts().add(new BadgePart(
                    badge,
                    partType == 's' ? BadgePartType.SHAPE : BadgePartType.BASE,
                    partId, partColor, partPosition));
        }

        return badge;
    }


    /**
     * Render the badge to a byte array.
     *
     * @return the image bytes, or null if rendering failed
     */
    public byte[] render() {
        BufferedImage canvas = ColorExtensions.createTransparentImage(CANVAS_WIDTH, CANVAS_HEIGHT);
        Graphics2D gCanvas = canvas.createGraphics();

        for (BadgePart part : getParts()) {
            if (badgeSettings.isShockwaveBadge()) {
                try {
                    BufferedImage template = getShockwaveTemplate(part.getType(), part.getGraphicResource(), false);
                    if (template != null) {
                        ColorExtensions.tintImage(template, colors[part.getColorResource() - 1], 255);
                        Point pos = part.getPosition(canvas, template);
                        gCanvas.drawImage(template, pos.x, pos.y, null);
                    }

                    if (isTemplateProxied(part.getType(), part.getGraphicResource())) {
                        BufferedImage proxyTemplate = getShockwaveTemplate(part.getType(), part.getGraphicResource(), true);
                        if (proxyTemplate != null) {
                            Point pos = part.getPosition(canvas, proxyTemplate);
                            gCanvas.drawImage(proxyTemplate, pos.x, pos.y, null);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                    // ignore for now
                }
            } else {
                if (!StringExtensions.isNullOrEmpty(part.getSymbol1())) {
                    BufferedImage template1 = getTemplate(part.getSymbol1());
                    if (template1 != null) {
                        if (part.getColor() != null) {
                            ColorExtensions.tintImage(template1, part.getColor(), 255);
                        }
                        Point pos1 = part.getPosition(canvas, template1);
                        gCanvas.drawImage(template1, pos1.x, pos1.y, null);
                    }
                }

                if (!StringExtensions.isNullOrEmpty(part.getSymbol2())) {
                    BufferedImage template2 = getTemplate(part.getSymbol2());
                    if (template2 != null) {
                        Point pos2 = part.getPosition(canvas, template2);
                        gCanvas.drawImage(template2, pos2.x, pos2.y, null);
                    }
                }
            }
        }

        gCanvas.dispose();

        if (this.badgeSettings.isForceWhiteBackground()) {
            fixTransparency(canvas);
        }

        try (ByteArrayOutputStream ms = new ByteArrayOutputStream()) {
            if (this.badgeSettings.getRenderType() == RenderType.GIF) {
                ImageIO.write(canvas, "gif", ms);
            } else {
                ImageIO.write(canvas, "png", ms);
            }
            return ms.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Determines if a template is proxied.
     */
    public boolean isTemplateProxied(BadgePartType type, int templateId) {
        if (type == BadgePartType.BASE) {
            for (int proxy : baseTemplateProxies) {
                if (proxy == templateId) {
                    return true;
                }
            }
        } else {
            for (int proxy : templateProxies) {
                if (proxy == templateId) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Loads a template image from disk.
     */
    public BufferedImage getTemplate(String symbol) {
        String filePath = Paths.get("badges", "badgeparts", symbol).toString();

        if (badgeSettings.getBasePath() != null && !badgeSettings.getBasePath().isEmpty()) {
            filePath = Paths.get(badgeSettings.getBasePath(), "badges", "badgeparts", symbol).toString();
        }

        if (!Files.exists(Paths.get(filePath))) {
            return null;
        }

        try {
            return ImageIO.read(new File(filePath));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Loads a Shockwave template image from disk.
     */
    public BufferedImage getShockwaveTemplate(BadgePartType type, int templateId, boolean proxy) {
        String fileGraphic = templateId < 10 ? "0" + templateId : String.valueOf(templateId);

        if (proxy) {
            fileGraphic = fileGraphic + "_" + fileGraphic;
        }

        String baseDir = Paths.get("badges", "shockwave",
                type == BadgePartType.BASE ? "base" : "templates").toString();

        String fileName = (templateId == 0 ? "base" : fileGraphic) + ".gif";
        String filePath = Paths.get(baseDir, fileName).toString();

        if (badgeSettings.getBasePath() != null && !badgeSettings.getBasePath().isEmpty()) {
            filePath = Paths.get(badgeSettings.getBasePath(), "badges", "shockwave",
                    type == BadgePartType.BASE ? "base" : "templates",
                    fileName).toString();
        }

        if (!Files.exists(Paths.get(filePath))) {
            // Return null instead of throwing an exception
            return null;
        }

        try {
            return ImageIO.read(new File(filePath));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Replace transparent and pure‑white pixels to avoid rendering issues.
     */
    private void fixTransparency(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                Color c = new Color(rgb, true);

                if (c.equals(Color.WHITE)) {
                    Color newC = new Color(254, 254, 254, 255);
                    image.setRGB(x, y, newC.getRGB());
                } else if (c.getAlpha() == 0) {
                    Color newC = new Color(255, 255, 255, 255);
                    image.setRGB(x, y, newC.getRGB());
                }
            }
        }
    }
}