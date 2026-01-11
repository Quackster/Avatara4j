package com.habbo.figure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Debug {
    public static void main(String[] args) throws Exception {
        String[] files = {"h_std_rh_1_2_0", "h_std_lh_1_2_0", "h_std_bd_1_2_0"};

        for (String name : files) {
            File f = new File("images/" + name + ".png");
            if (!f.exists()) {
                System.out.println(name + " not found");
                continue;
            }

            BufferedImage img = ImageIO.read(f);
            System.out.println("\n" + name + ":");
            System.out.println("  Type: " + img.getType() + " (10=TYPE_INT_ARGB, 0=TYPE_CUSTOM)");
            System.out.println("  Size: " + img.getWidth() + "x" + img.getHeight());

            // Find some non-transparent pixels
            int count = 0;
            for (int y = 0; y < img.getHeight() && count < 10; y++) {
                for (int x = 0; x < img.getWidth() && count < 10; x++) {
                    int argb = img.getRGB(x, y);
                    int a = (argb >> 24) & 0xFF;
                    if (a > 0) {
                        int r = (argb >> 16) & 0xFF;
                        int g = (argb >> 8) & 0xFF;
                        int b = argb & 0xFF;
                        System.out.printf("  Pixel(%d,%d): A=%d R=%d G=%d B=%d%n", x, y, a, r, g, b);
                        count++;
                    }
                }
            }
        }
    }
}
