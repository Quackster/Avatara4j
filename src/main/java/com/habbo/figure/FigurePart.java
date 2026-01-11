package com.habbo.figure;

/**
 * Represents a single part within a figure set.
 */
public class FigurePart {
    private final int id;
    private final String type;
    private final boolean colorable;
    private final int index;

    public FigurePart(int id, String type, boolean colorable, int index) {
        this.id = id;
        this.type = type;
        this.colorable = colorable;
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean isColorable() {
        return colorable;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "FigurePart{id=" + id + ", type='" + type + "', colorable=" + colorable + ", index=" + index + "}";
    }
}
