package net.h4bbo.avatara4j.figure;

import net.h4bbo.avatara4j.figure.types.FigurePart;
import net.h4bbo.avatara4j.figure.types.FigureSet;

public class AvatarAsset {
    public String name;
    public int x;
    public int y;
    public int imageX;
    public int imageY;
    public String fileName;
    public FigurePart part;
    public FigureSet set;
    public String[] parts;
    public int renderOrder;
    public boolean isDrinkCanvas;

    public AvatarAsset(
            boolean isSmall,
            String action,
            String name,
            String fileName,
            int x,
            int y,
            FigurePart part,
            FigureSet set,
            int canvasW,
            int canvasH,
            String[] parts
    ) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.fileName = fileName;
        this.part = part;
        this.set = set;
        this.parts = parts;
        this.renderOrder = this.part.getOrderId(); // Assuming getOrderId() is the Java getter
        this.isDrinkCanvas = false;

        if ("lay".equals(action)) {
            this.imageY = y + (canvasW / 2) + (isSmall ? -5 : -20);
            this.imageX = x + (canvasH / 2) - (isSmall ? -11 : -10);
        } else {
            this.imageX = x + (canvasW / 2) + (isSmall ? 5 : 10);
            this.imageY = y + (canvasH / 2) - (isSmall ? 11 : 20);
        }
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getImageX() {
        return imageX;
    }

    public int getImageY() {
        return imageY;
    }

    public String getFileName() {
        return fileName;
    }

    public FigurePart getPart() {
        return part;
    }

    public FigureSet getSet() {
        return set;
    }

    public String[] getParts() {
        return parts;
    }

    public int getRenderOrder() {
        return renderOrder;
    }

    public boolean isDrinkCanvas() {
        return isDrinkCanvas;
    }
}
