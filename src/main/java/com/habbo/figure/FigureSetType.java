package com.habbo.figure;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a set type (hd, hr, ch, lg, sh, etc.) with its associated palette and sets.
 */
public class FigureSetType {
    private final String type;
    private final int paletteId;
    private final boolean mandatory;
    private final Map<Integer, FigureSet> sets;

    public FigureSetType(String type, int paletteId, boolean mandatory) {
        this.type = type;
        this.paletteId = paletteId;
        this.mandatory = mandatory;
        this.sets = new HashMap<>();
    }

    public void addSet(FigureSet set) {
        sets.put(set.getId(), set);
    }

    public FigureSet getSet(int id) {
        return sets.get(id);
    }

    public String getType() {
        return type;
    }

    public int getPaletteId() {
        return paletteId;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public Map<Integer, FigureSet> getSets() {
        return sets;
    }

    @Override
    public String toString() {
        return "FigureSetType{type='" + type + "', paletteId=" + paletteId + ", sets=" + sets.size() + "}";
    }
}
