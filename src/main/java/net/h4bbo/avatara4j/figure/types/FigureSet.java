package net.h4bbo.avatara4j.figure.types;

import java.util.ArrayList;
import java.util.List;

public class FigureSet {
    private List<FigurePart> figureParts;
    private List<String> hiddenLayers;
    private String setType;
    private String id;
    private String gender;
    private boolean club;
    private boolean colourable;
    private boolean selectable;

    public FigureSet(String setType, String id, String gender, boolean club, boolean colourable, boolean selectable) {
        this.setType = setType;
        this.id = id;
        this.gender = gender;
        this.club = club;
        this.colourable = colourable;
        this.selectable = selectable;
        this.figureParts = new ArrayList<>();
        this.hiddenLayers = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "FigureSet{" +
                "setType='" + setType + '\'' +
                ", id='" + id + '\'' +
                ", gender='" + gender + '\'' +
                ", club=" + club +
                ", colourable=" + colourable +
                ", selectable=" + selectable +
                ", figureParts=" + figureParts +
                ", hiddenLayers=" + hiddenLayers +
                '}';
    }

    // Getters
    public List<FigurePart> getFigureParts() { return figureParts; }
    public List<String> getHiddenLayers() { return hiddenLayers; }
    public String getSetType() { return setType; }
    public String getId() { return id; }
    public String getGender() { return gender; }
    public boolean isClub() { return club; }
    public boolean isColourable() { return colourable; }
    public boolean isSelectable() { return selectable; }

    // Setters
    public void setFigureParts(List<FigurePart> figureParts) { this.figureParts = figureParts; }
    public void setHiddenLayers(List<String> hiddenLayers) { this.hiddenLayers = hiddenLayers; }
    public void setSetType(String setType) { this.setType = setType; }
    public void setId(String id) { this.id = id; }
    public void setGender(String gender) { this.gender = gender; }
    public void setClub(boolean club) { this.club = club; }
    public void setColourable(boolean colourable) { this.colourable = colourable; }
    public void setSelectable(boolean selectable) { this.selectable = selectable; }
}
