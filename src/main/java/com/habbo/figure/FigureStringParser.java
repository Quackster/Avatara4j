package com.habbo.figure;

import java.util.*;

/**
 * Parses a figure string like "hd-180-1.hr-100-61.ch-210-66.lg-270-82.sh-290-80"
 * into a list of FigurePartInfo objects.
 */
public class FigureStringParser {

    /**
     * Represents a parsed part from the figure string.
     */
    public static class FigurePartInfo {
        private final String setType;
        private final int setId;
        private final int colorId;
        private final int secondaryColorId;

        public FigurePartInfo(String setType, int setId, int colorId, int secondaryColorId) {
            this.setType = setType;
            this.setId = setId;
            this.colorId = colorId;
            this.secondaryColorId = secondaryColorId;
        }

        public String getSetType() {
            return setType;
        }

        public int getSetId() {
            return setId;
        }

        public int getColorId() {
            return colorId;
        }

        public int getSecondaryColorId() {
            return secondaryColorId;
        }

        @Override
        public String toString() {
            return "FigurePartInfo{type='" + setType + "', setId=" + setId + ", colorId=" + colorId + "}";
        }
    }

    /**
     * Parse a figure string into a list of FigurePartInfo objects.
     * Format: "type-setId-colorId" separated by dots, optionally with secondary color "type-setId-color1-color2"
     */
    public static List<FigurePartInfo> parse(String figureString) {
        List<FigurePartInfo> parts = new ArrayList<>();

        if (figureString == null || figureString.isEmpty()) {
            return parts;
        }

        String[] segments = figureString.split("\\.");
        for (String segment : segments) {
            String[] components = segment.split("-");
            if (components.length >= 3) {
                String setType = components[0];
                int setId = Integer.parseInt(components[1]);
                int colorId = Integer.parseInt(components[2]);
                int secondaryColorId = -1;

                if (components.length >= 4) {
                    secondaryColorId = Integer.parseInt(components[3]);
                }

                parts.add(new FigurePartInfo(setType, setId, colorId, secondaryColorId));
            }
        }

        return parts;
    }

    /**
     * Get the draw order for part types (lower index = drawn first/behind).
     * Direction affects left/right part ordering:
     * - Directions 0, 1, 7 (facing right): right parts in front
     * - Directions 3, 4, 5 (facing left): left parts in front
     * - Directions 2, 6 (front/back): standard order
     */
    public static int getDrawOrder(String partType, int direction) {
        // Determine if we should swap left/right based on direction
        // Directions 3, 4, 5 = facing left, so left parts should be in front
        boolean leftInFront = (direction >= 3 && direction <= 5);

        switch (partType) {
            // Body parts - drawn first
            case "bd": return 0;    // Body
            case "sh": return 1;    // Shoes
            case "lg": return 2;    // Legs
            case "ch": return 3;    // Chest

            // Arms - order depends on direction
            case "lh": return leftInFront ? 5 : 4;    // Left hand
            case "rh": return leftInFront ? 4 : 5;    // Right hand
            case "ls": return leftInFront ? 7 : 6;    // Left sleeve
            case "rs": return leftInFront ? 6 : 7;    // Right sleeve

            // Head parts
            case "hd": return 10;   // Head
            case "fc": return 11;   // Face
            case "ey": return 12;   // Eyes
            case "hrb": return 13;  // Hair back
            case "hr": return 14;   // Hair front

            // Accessories
            case "ha": return 20;   // Hat
            case "he": return 21;   // Head accessory
            case "ea": return 22;   // Ear accessory
            case "fa": return 23;   // Face accessory
            case "ca": return 24;   // Chest accessory
            case "wa": return 25;   // Waist accessory
            case "cc": return 26;   // Coat
            case "cp": return 27;   // Coat front

            // Items - order depends on direction
            case "ri": return leftInFront ? 30 : 31;   // Right hand item
            case "li": return leftInFront ? 31 : 30;   // Left hand item

            default: return 50;
        }
    }
}
