package net.h4bbo.avatara4j.figure.types.legacy;

import java.util.List;

public class LegacyFigure {
    private int sprite;
    private List<LegacyPart> parts;
    private List<String> colours;
    private String genderType;

    public LegacyFigure(int sprite, List<LegacyPart> parts, List<String> colours, String genderType) {
        this.sprite = sprite;
        this.parts = parts;
        this.colours = colours;
        this.genderType = genderType;
    }

    public int getSprite() {
        return sprite;
    }

    public List<LegacyPart> getParts() {
        return parts;
    }

    public List<String> getColours() {
        return colours;
    }

    public String getGenderType() {
        return genderType;
    }

    @Override
    public String toString() {
        return "ChData{" +
                "sprite=" + sprite +
                ", parts=" + parts +
                ", colours=" + colours +
                ", genderType='" + genderType + '\'' +
                '}';
    }
}
