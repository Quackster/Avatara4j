package net.h4bbo.avatara4j.figure.types;

public class FigurePart {
    private String id;
    private String type;
    private boolean colorable;
    private int index;
    private int orderId;

    public FigurePart(String id, String type, boolean colorable, int index) {
        this.id = id;
        this.type = type;
        this.colorable = colorable;
        this.index = index;
        this.orderId = getOrder();
    }

    private int getOrder() {
        switch (type) {
            case "sh": return 5;
            case "lg": return 6;
            case "ch": return 7;
            case "wa": return 8;
            case "ca": return 9;
            case "fa": return 27;
            case "ea": return 28;
            case "ha": return 29;
            case "he": return 29; // was 20 in comment
            case "cc": return 1;
            case "cp": return 6;
            case "hd": return 22;
            case "bd": return 1;
            case "fc": return 23;
            case "hr": return 24;
            case "lh": return 5;
            case "ls": return 7;
            case "rh": return 10;
            case "rs": return 11;
            case "ey": return 24;
            case "li": return 0;
            case "hrb": return 26;
            case "ri": return 9;
            case "lc": return 23;
            case "rc": return 24;
            case "fx": return 100;
            default: return -1;
        }
    }

    // ...your code...

    @Override
    public String toString() {
        return "FigurePart{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", colorable=" + colorable +
                ", index=" + index +
                ", orderId=" + orderId +
                '}';
    }

    // Getters
    public String getId() { return id; }
    public String getType() { return type; }
    public boolean isColorable() { return colorable; }
    public int getIndex() { return index; }
    public int getOrderId() { return orderId; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setColorable(boolean colorable) { this.colorable = colorable; }
    public void setIndex(int index) { this.index = index; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
}
