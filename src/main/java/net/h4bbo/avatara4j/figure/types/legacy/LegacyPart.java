package net.h4bbo.avatara4j.figure.types.legacy;

public class LegacyPart {
    private String type;
    private int value;

    public LegacyPart(String type, int value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Part{" +
                "type='" + type + '\'' +
                ", value=" + value +
                '}';
    }
}