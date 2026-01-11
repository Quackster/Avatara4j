package com.habbo.figure;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a color palette used for figure parts.
 */
public class FigurePalette {
    private final int id;
    private final Map<Integer, FigureColor> colors;

    public FigurePalette(int id) {
        this.id = id;
        this.colors = new HashMap<>();
    }

    public void addColor(FigureColor color) {
        colors.put(color.getId(), color);
    }

    public FigureColor getColor(int id) {
        return colors.get(id);
    }

    public int getId() {
        return id;
    }

    public Map<Integer, FigureColor> getColors() {
        return colors;
    }

    @Override
    public String toString() {
        return "FigurePalette{id=" + id + ", colors=" + colors.size() + "}";
    }
}
