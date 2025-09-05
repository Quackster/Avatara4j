package net.h4bbo.avatara4j.figure.types;

public class FigureColor {
    private String colourId;
    private String index;
    private boolean isClubRequired;
    private boolean isSelectable;
    private String hexColor;

    public FigureColor(String colourId, String index, boolean isClubRequired, boolean isSelectable, String hexColor) {
        this.colourId = colourId;
        this.index = index;
        this.isClubRequired = isClubRequired;
        this.isSelectable = isSelectable;
        this.hexColor = hexColor;
    }

    // Getters
    public String getColourId() { return colourId; }
    public String getIndex() { return index; }
    public boolean isClubRequired() { return isClubRequired; }
    public boolean isSelectable() { return isSelectable; }
    public String getHexColor() { return hexColor; }

    // Setters
    public void setColourId(String colourId) { this.colourId = colourId; }
    public void setIndex(String index) { this.index = index; }
    public void setClubRequired(boolean clubRequired) { this.isClubRequired = clubRequired; }
    public void setSelectable(boolean selectable) { this.isSelectable = selectable; }
    public void setHexColor(String hexColor) { this.hexColor = hexColor; }

    @Override
    public String toString() {
        return "FigureColor{" +
                "colourId='" + colourId + '\'' +
                ", index='" + index + '\'' +
                ", isClubRequired=" + isClubRequired +
                ", isSelectable=" + isSelectable +
                ", hexColor='" + hexColor + '\'' +
                '}';
    }

}
