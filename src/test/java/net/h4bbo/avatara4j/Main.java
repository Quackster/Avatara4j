package net.h4bbo.avatara4j;

import net.h4bbo.avatara4j.figure.Avatar;
import net.h4bbo.avatara4j.figure.readers.FiguredataReader;
import net.h4bbo.avatara4j.figure.readers.ManifestReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        System.out.println("Loading figuredata for Avatara4j");
        FiguredataReader.getInstance().load();

        System.out.println("Loading figure offsets");
        ManifestReader.getInstance().load();
        System.out.println("Loaded " + ManifestReader.getInstance().getParts().size() + " figure offsets!");

        writeDefaultFigure();
        writeOldschoolFigure();
    }

    private static void writeOldschoolFigure() {
        // The imager arguments
        String figure = "1000118001270012900121001";
        String size = "l";
        int bodyDirection = 4;
        int headDirection = 4;
        String action = "std";
        String gesture = "sml";
        boolean headOnly = false;
        int frame = 1;
        int carryDrink = -1;
        boolean cropImage = false;

        try {
            Avatar avatar = new Avatar(
                    FiguredataReader.getInstance(), figure, size, bodyDirection, headDirection,
                    action, gesture, headOnly, frame, carryDrink, cropImage
            );
            Files.write(Paths.get("figure_oldschool.png"), avatar.run());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeDefaultFigure() {
        // The imager arguments
        String figure = "hd-180-1.hr-100-61.ch-210-66.lg-270-82.sh-290-80";
        String size = "b";
        int bodyDirection = 2;
        int headDirection = 2;
        String action = "std";
        String gesture = "sml";
        boolean headOnly = false;
        int frame = 1;
        int carryDrink = -1;
        boolean cropImage = false;

        try {
            Avatar avatar = new Avatar(
                    FiguredataReader.getInstance(), figure, size, bodyDirection, headDirection,
                    action, gesture, headOnly, frame, carryDrink, cropImage
            );
            Files.write(Paths.get("figure_default.png"), avatar.run());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}