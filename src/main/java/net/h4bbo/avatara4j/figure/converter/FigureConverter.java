package net.h4bbo.avatara4j.figure.converter;

import net.h4bbo.avatara4j.figure.readers.FiguredataReader;
import net.h4bbo.avatara4j.figure.readers.LegacyFiguredataReader;
import net.h4bbo.avatara4j.figure.types.FigureColor;
import net.h4bbo.avatara4j.figure.types.legacy.LegacyFigure;

import java.util.*;

/**
 * Figuredata converter originally written by Alcosmos.
 */
public class FigureConverter {
    private static volatile FigureConverter instance;
    private static final Object lock = new Object();

    public static FigureConverter getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new FigureConverter();
                }
            }
        }
        return instance;
    }

    // Private constructor for singleton
    private FigureConverter() {

    }

    /**
     * Converts an old figure format string to the new avatarimage format.
     */
    public String convertOldToNew(String oldFigure) {
        if (oldFigure == null || oldFigure.length() < 22) {
            throw new IllegalArgumentException("Invalid figure string");
        }

        String[] partsString = new String[10];
        int start = 0;
        for (int i = 0; i < 10; i++) {
            int length = (i == 0 || i == 2 || i == 4 || i == 6 || i == 8) ? 3 : 2;
            partsString[i] = oldFigure.substring(start, start + length);
            start += length;
        }

        int[] parts = new int[10];
        for (int i = 0; i < 10; i++) {
            parts[i] = Integer.parseInt(partsString[i]);
        }

        String hrColor = convertOldColorToNew("hr", parts[0], parts[1]);
        StringBuilder result = new StringBuilder();

        result.append("hr-").append(parts[0]).append("-").append(hrColor);
        result.append(".hd-").append(parts[2]).append("-").append(convertOldColorToNew("hd", parts[2], parts[3]));
        result.append(".ch-").append(parts[8]).append("-").append(convertOldColorToNew("ch", parts[8], parts[9]));
        result.append(".lg-").append(parts[4]).append("-").append(convertOldColorToNew("lg", parts[4], parts[5]));
        result.append(".sh-").append(parts[6] == 730 ? 3206 : parts[6]).append("-").append(convertOldColorToNew("sh", parts[6], parts[7]));
        result.append(takeCareOfHats(parts[0], Integer.parseInt(hrColor)));

        return result.toString();
    }

    /*
    private JsonNode getOldFigureData() {
        if (oldFigureData == null) {
            synchronized (oldLock) {
                if (oldFigureData == null) {
                    try {
                        List<InputStream> streams = FileUtil.getInstance().solveFile("figuredata/converter/", "oldfiguredata");
                        Optional<InputStream> stream = streams.stream().findFirst();

                        if (stream.isPresent()) {
                            oldFigureData = mapper.readTree(stream.get());
                        } else {
                            throw new FileNotFoundException("Could not find figuredata/converter/oldfiguredata.json");
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return oldFigureData;
    }*/

    private String getOldColorFromFigureList(String part, int sprite, int colorIndex) {
        Optional<LegacyFigure> oldColor = LegacyFiguredataReader.getInstance().getLegacyFiguredata()
                .stream().filter(x ->
                        x.getSprite() == sprite &&
                        x.getParts().stream().anyMatch(partType -> partType.getType().equals(part) &&
                        x.getColours().size() >= colorIndex)
                )
                .findFirst();

        return oldColor.map(legacyFigure -> legacyFigure.getColours().get(colorIndex - 1)).orElse(null);

        /*
        JsonNode colorsJson = getOldFigureData();
        JsonNode genders = colorsJson.get("genders");
        if (genders == null) return null;

        for (JsonNode gender : genders) {
            for (JsonNode partType : gender) {
                if (partType.has(part)) {
                    JsonNode partArray = partType.get(part);
                    for (JsonNode dataArray : partArray) {
                        for (JsonNode dataObj : dataArray) {
                            if (dataObj != null && dataObj.has("s") && dataObj.has("c")) {
                                if (dataObj.get("s").asInt() == sprite) {
                                    JsonNode spriteColorsArray = mapper.createArrayNode();
                                    try {
                                        spriteColorsArray = mapper.readTree(dataObj.get("c").toString());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    if (colorIndex - 1 < spriteColorsArray.size()) {
                                        return spriteColorsArray.get(colorIndex - 1).asText();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }*/


    }

    private String convertOldColorToNew(String part, int sprite, int colorIndex) {
        String oldColor = getOldColorFromFigureList(part, sprite, colorIndex);
        if (oldColor == null) return null;

       for (List<FigureColor> colourPalettes : FiguredataReader.getInstance().getFigurePalettes().values()) {
            Optional<FigureColor> colour = colourPalettes.stream()
                    .filter(x -> x.getHexColor().equalsIgnoreCase(oldColor))
                    .findFirst();

            if (colour.isPresent()) {
                return colour.get().getColourId();
            }
        }

       /*
        JsonNode paletteJson = getNewFigureData();
        JsonNode palette = paletteJson.get("palette");
        if (palette == null) return null;

        for (JsonNode pal : palette) {
            if (pal.isObject()) {
                for (java.util.Iterator<String> it = pal.fieldNames(); it.hasNext(); ) {
                    String colorName = it.next();
                    JsonNode colorObj = pal.get(colorName);
                    if (colorObj != null && colorObj.has("color")) {
                        if (colorObj.get("color").asText().equals(oldColor)) {
                            return colorName;
                        }
                    }
                }
            }
        }*/

        return null;
    }

    private String takeCareOfHats(int spriteId, int colorId) {
        switch (spriteId) {
            case 120: return ".ha-1001-0";
            case 525:
            case 140: return ".ha-1002-" + colorId;
            case 150:
            case 535: return ".ha-1003-" + colorId;
            case 160:
            case 565: return ".ha-1004-" + colorId;
            case 570: return ".ha-1005-" + colorId;
            case 585:
            case 175: return ".ha-1006-0";
            case 580:
            case 176: return ".ha-1007-0.fa-1202-70";
            case 590:
            case 177: return ".ha-1008-0";
            case 595:
            case 178: return ".ha-1009-1321";
            case 130: return ".ha-1010-" + colorId;
            case 801: return ".hr-829-" + colorId + ".fa-1201-62.ha-1011-" + colorId;
            case 800:
            case 810: return ".ha-1012-" + colorId;
            case 802:
            case 811: return ".ha-1013-" + colorId;
            default: return ""; // this is the same as below but takes up less memory and is official Habbo behaviour :^) - Quackster
            // default: return ".ha-0-" + colorId;
        }
    }
}