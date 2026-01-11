package com.habbo.figure;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

/**
 * Parses and holds all figure data from figuredata.xml
 */
public class FigureData {
    private final Map<Integer, FigurePalette> palettes;
    private final Map<String, FigureSetType> setTypes;

    public FigureData() {
        this.palettes = new HashMap<>();
        this.setTypes = new HashMap<>();
    }

    public void load(String filePath) throws Exception {
        File file = new File(filePath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        doc.getDocumentElement().normalize();

        // Parse palettes
        NodeList paletteNodes = doc.getElementsByTagName("palette");
        for (int i = 0; i < paletteNodes.getLength(); i++) {
            Element paletteElement = (Element) paletteNodes.item(i);
            int paletteId = Integer.parseInt(paletteElement.getAttribute("id"));
            FigurePalette palette = new FigurePalette(paletteId);

            NodeList colorNodes = paletteElement.getElementsByTagName("color");
            for (int j = 0; j < colorNodes.getLength(); j++) {
                Element colorElement = (Element) colorNodes.item(j);
                int colorId = Integer.parseInt(colorElement.getAttribute("id"));
                int index = Integer.parseInt(colorElement.getAttribute("index"));
                int club = Integer.parseInt(colorElement.getAttribute("club"));
                boolean selectable = "1".equals(colorElement.getAttribute("selectable"));
                String hexColor = colorElement.getTextContent().trim();

                FigureColor color = new FigureColor(colorId, index, club, selectable, hexColor);
                palette.addColor(color);
            }

            palettes.put(paletteId, palette);
        }

        // Parse set types
        NodeList setTypeNodes = doc.getElementsByTagName("settype");
        for (int i = 0; i < setTypeNodes.getLength(); i++) {
            Element setTypeElement = (Element) setTypeNodes.item(i);
            String type = setTypeElement.getAttribute("type");
            int paletteId = Integer.parseInt(setTypeElement.getAttribute("paletteid"));
            boolean mandatory = "1".equals(setTypeElement.getAttribute("mandatory"));

            FigureSetType setType = new FigureSetType(type, paletteId, mandatory);

            NodeList setNodes = setTypeElement.getElementsByTagName("set");
            for (int j = 0; j < setNodes.getLength(); j++) {
                Element setElement = (Element) setNodes.item(j);
                int setId = Integer.parseInt(setElement.getAttribute("id"));
                String gender = setElement.getAttribute("gender");
                int club = Integer.parseInt(setElement.getAttribute("club"));
                boolean colorable = "1".equals(setElement.getAttribute("colorable"));
                boolean selectable = "1".equals(setElement.getAttribute("selectable"));

                FigureSet set = new FigureSet(setId, gender, club, colorable, selectable);

                // Parse parts
                NodeList partNodes = setElement.getElementsByTagName("part");
                for (int k = 0; k < partNodes.getLength(); k++) {
                    Element partElement = (Element) partNodes.item(k);
                    int partId = Integer.parseInt(partElement.getAttribute("id"));
                    String partType = partElement.getAttribute("type");
                    boolean partColorable = "1".equals(partElement.getAttribute("colorable"));
                    int partIndex = Integer.parseInt(partElement.getAttribute("index"));

                    FigurePart part = new FigurePart(partId, partType, partColorable, partIndex);
                    set.addPart(part);
                }

                // Parse hidden layers
                NodeList hiddenLayerNodes = setElement.getElementsByTagName("layer");
                for (int k = 0; k < hiddenLayerNodes.getLength(); k++) {
                    Element layerElement = (Element) hiddenLayerNodes.item(k);
                    String partType = layerElement.getAttribute("parttype");
                    set.addHiddenLayer(partType);
                }

                setType.addSet(set);
            }

            setTypes.put(type, setType);
        }

        System.out.println("Loaded " + palettes.size() + " palettes and " + setTypes.size() + " set types");
    }

    public FigurePalette getPalette(int id) {
        return palettes.get(id);
    }

    public FigureSetType getSetType(String type) {
        return setTypes.get(type);
    }

    public Map<Integer, FigurePalette> getPalettes() {
        return palettes;
    }

    public Map<String, FigureSetType> getSetTypes() {
        return setTypes;
    }

    /**
     * Get color for a specific set type and color ID.
     */
    public FigureColor getColorForSetType(String setTypeName, int colorId) {
        FigureSetType setType = setTypes.get(setTypeName);
        if (setType == null) return null;
        FigurePalette palette = palettes.get(setType.getPaletteId());
        if (palette == null) return null;
        return palette.getColor(colorId);
    }
}
