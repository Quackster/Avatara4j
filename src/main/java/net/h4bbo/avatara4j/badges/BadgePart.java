package net.h4bbo.avatara4j.badges;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

/**
 * Represents a part of a badge.
 */
public class BadgePart {

    private final Badge badge;

    private int graphicResource;
    private int colorResource;

    private String symbol1;
    private String symbol2;
    private String color;

    private int location;
    private BadgePartType type;

    /**
     * Constructs a {@code BadgePart}.
     *
     * @param badge    the badge to which this part belongs
     * @param type     the type of the part
     * @param graphic  the graphic resource id
     * @param color    the color resource id
     * @param location the location identifier
     */
    public BadgePart(Badge badge, BadgePartType type, int graphic, int color, int location) {
        this.badge = badge;
        this.type = type;
        this.graphicResource = graphic;
        this.colorResource = color;
        this.location = location;

        /*
        if (!badge.getSettings().isShockwaveBadge()) {
            List<BadgeResource> resources = BadgeResourceManager.getBadgeResources();
            if (!resources.isEmpty()) {
                switch (type) {
                    case SHAPE:
                        Optional<BadgeResource> shapeRes = resources.stream()
                                .filter(x -> x.getId() == graphic && x.getType().equalsIgnoreCase("symbol"))
                                .findFirst();
                        shapeRes.ifPresent(res -> {
                            symbol1 = res.getExtraData1();
                            symbol2 = res.getExtraData2();
                        });

                        Optional<BadgeResource> colour1 = resources.stream()
                                .filter(x -> x.getId() == color && x.getType().equalsIgnoreCase("colour1"))
                                .findFirst();
                        colour1.ifPresent(res -> color = res.getExtraData1());
                        break;

                    case BASE:
                        Optional<BadgeResource> baseRes = resources.stream()
                                .filter(x -> x.getId() == graphic && x.getType().equalsIgnoreCase("base"))
                                .findFirst();
                        baseRes.ifPresent(res -> {
                            symbol1 = res.getExtraData1();
                            symbol2 = res.getExtraData2();
                        });

                        colour1 = resources.stream()
                                .filter(x -> x.getId() == color && x.getType().equalsIgnoreCase("colour1"))
                                .findFirst();
                        colour1.ifPresent(res -> color = res.getExtraData1());
                        break;
                }
            }
        }*/
    }

    /**
     * Calculates the position of this part on the given canvas using the template.
     *
     * @param canvas   the canvas image
     * @param template the template image
     * @return the top‑left point where the part should be drawn
     */
    public Badge.Point getPosition(BufferedImage canvas, BufferedImage template) {
        int x = 0;
        int y = 0;

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        int templateWidth = template.getWidth();
        int templateHeight = template.getHeight();

        switch (this.location) {
            case 1:
                x = (canvasWidth - templateWidth) / 2;
                break;
            case 2:
                x = canvasWidth - templateWidth;
                break;
            case 3:
                y = (canvasHeight / 2) - (templateHeight / 2);
                break;
            case 4:
                x = (canvasWidth - templateWidth) / 2;
                y = (canvasHeight / 2) - (templateHeight / 2);
                break;
            case 5:
                x = canvasWidth - templateWidth;
                y = (canvasHeight / 2) - (templateHeight / 2);
                break;
            case 6:
                y = canvasHeight - templateWidth; // Note: mirroring original logic
                break;
            case 7:
                x = (canvasWidth - templateWidth) / 2;
                y = canvasHeight - templateHeight;
                break;
            case 8:
                x = canvasWidth - templateWidth;
                y = canvasHeight - templateHeight;
                break;
        }

        return new Badge.Point(x, y);
    }

    // --------------------------------------------------------------------
    // Getters and setters
    // --------------------------------------------------------------------

    public Badge getBadge() {
        return badge;
    }

    public int getGraphicResource() {
        return graphicResource;
    }

    public void setGraphicResource(int graphicResource) {
        this.graphicResource = graphicResource;
    }

    public int getColorResource() {
        return colorResource;
    }

    public void setColorResource(int colorResource) {
        this.colorResource = colorResource;
    }

    public String getSymbol1() {
        return symbol1;
    }

    public void setSymbol1(String symbol1) {
        this.symbol1 = symbol1;
    }

    public String getSymbol2() {
        return symbol2;
    }

    public void setSymbol2(String symbol2) {
        this.symbol2 = symbol2;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public BadgePartType getType() {
        return type;
    }

    public void setType(BadgePartType type) {
        this.type = type;
    }
}