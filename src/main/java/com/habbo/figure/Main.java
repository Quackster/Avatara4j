package com.habbo.figure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Main entry point for the Habbo Figure Renderer.
 *
 * Usage: java com.habbo.figure.Main <figure-string> [output-file] [direction] [action]
 *
 * Example: java com.habbo.figure.Main "hd-180-1.hr-100-61.ch-210-66.lg-270-82.sh-290-80" figure.png
 */
public class Main {

    public static void main(String[] args) {
        // Default figure string for testing
        String figureString = "hd-180-1.hr-100-61.ch-210-66.lg-270-82.sh-290-80";
        String outputFile = "figure.png";
        int direction = 2;
        String action = "std";

        // Parse command line arguments
        if (args.length >= 1) {
            figureString = args[0];
        }
        if (args.length >= 2) {
            outputFile = args[1];
        }
        if (args.length >= 3) {
            direction = Integer.parseInt(args[2]);
        }
        if (args.length >= 4) {
            action = args[3];
        }

        System.out.println("Habbo Figure Renderer");
        System.out.println("=====================");
        System.out.println("Figure String: " + figureString);
        System.out.println("Output File: " + outputFile);
        System.out.println("Direction: " + direction);
        System.out.println("Action: " + action);
        System.out.println();

        try {
            // Determine base path (assume we're running from the figuredata directory)
            String basePath = System.getProperty("user.dir");

            // Check if figuredata.xml exists, if not try parent/figuredata
            File figureDataFile = new File(basePath, "figuredata.xml");
            if (!figureDataFile.exists()) {
                // Try the current directory structure
                figureDataFile = new File("figuredata.xml");
                if (figureDataFile.exists()) {
                    basePath = figureDataFile.getParent();
                    if (basePath == null) basePath = ".";
                }
            }

            System.out.println("Base path: " + new File(basePath).getAbsolutePath());

            // Load figure data
            System.out.println("Loading figure data...");
            FigureData figureData = new FigureData();
            figureData.load(basePath + "/figuredata.xml");

            // Load asset manifests
            System.out.println("Loading asset manifests...");
            AssetManager assetManager = new AssetManager(basePath);
            assetManager.loadManifests();

            // Create renderer
            FigureRenderer renderer = new FigureRenderer(figureData, assetManager);
            renderer.setDirection(direction);
            renderer.setAction(action);

            // Render the figure
            System.out.println("Rendering figure...");
            BufferedImage image = renderer.render(figureString);

            // Save the output
            File output = new File(outputFile);
            ImageIO.write(image, "PNG", output);
            System.out.println("Figure saved to: " + output.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
