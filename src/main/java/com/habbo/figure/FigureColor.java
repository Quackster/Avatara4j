package com.habbo.figure;

import java.awt.Color;

/**
 * Represents a color from a figure palette.
 */
public class FigureColor {
    private final int id;
    private final int index;
    private final int club;
    private final boolean selectable;
    private final String hexColor;
    private final Color color;

    public FigureColor(int id, int index, int club, boolean selectable, String hexColor) {
        this.id = id;
        this.index = index;
        this.club = club;
        this.selectable = selectable;
        this.hexColor = hexColor;
        this.color = parseHexColor(hexColor);
    }

    private Color parseHexColor(String hex) {
        try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return new Color(r, g, b);
        } catch (Exception e) {
            return Color.WHITE;
        }
    }

    public int getId() {
        return id;
    }

    public int getIndex() {
        return index;
    }

    public int getClub() {
        return club;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public String getHexColor() {
        return hexColor;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "FigureColor{id=" + id + ", hex='" + hexColor + "'}";
    }
}
