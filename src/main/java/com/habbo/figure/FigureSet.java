package com.habbo.figure;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a figure set (e.g., a specific hair style, shirt, etc.)
 */
public class FigureSet {
    private final int id;
    private final String gender;
    private final int club;
    private final boolean colorable;
    private final boolean selectable;
    private final List<FigurePart> parts;
    private final List<String> hiddenLayers;

    public FigureSet(int id, String gender, int club, boolean colorable, boolean selectable) {
        this.id = id;
        this.gender = gender;
        this.club = club;
        this.colorable = colorable;
        this.selectable = selectable;
        this.parts = new ArrayList<>();
        this.hiddenLayers = new ArrayList<>();
    }

    public void addPart(FigurePart part) {
        parts.add(part);
    }

    public void addHiddenLayer(String partType) {
        hiddenLayers.add(partType);
    }

    public int getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }

    public int getClub() {
        return club;
    }

    public boolean isColorable() {
        return colorable;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public List<FigurePart> getParts() {
        return parts;
    }

    public List<String> getHiddenLayers() {
        return hiddenLayers;
    }

    @Override
    public String toString() {
        return "FigureSet{id=" + id + ", gender='" + gender + "', parts=" + parts.size() + "}";
    }
}
