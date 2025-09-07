package net.h4bbo.avatara4j.extensions;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorExtensions {
    // Tint image by hex color
    public static void tintImage(BufferedImage image, String hexColourCode, int alpha) {
        Color rgb = hexToColor(hexColourCode);
        int w = image.getWidth();
        int h = image.getHeight();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int argb = image.getRGB(x, y);
                int a = (argb >> 24) & 0xff;
                if (a > 0) {
                    int r = ((argb >> 16) & 0xff) * rgb.getRed() / 255;
                    int g = ((argb >> 8) & 0xff) * rgb.getGreen() / 255;
                    int b = (argb & 0xff) * rgb.getBlue() / 255;
                    int rgba = (alpha << 24) | (r << 16) | (g << 8) | b;
                    image.setRGB(x, y, rgba);
                }
            }
        }
    }

    // Convert hex to Color
    public static Color hexToColor(String hex) {
        if (hex.equalsIgnoreCase("transparent"))
            return new Color(0, 0, 0, 0);
        hex = hex.replace("#", "");
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new Color(r, g, b);
    }

    // Create a transparent BufferedImage
    public static BufferedImage createTransparentImage(int width, int height) {
        BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = b.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return b;
    }
}