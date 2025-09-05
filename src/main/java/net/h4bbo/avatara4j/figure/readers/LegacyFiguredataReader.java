package net.h4bbo.avatara4j.figure.readers;

import net.h4bbo.avatara4j.figure.types.legacy.LegacyFigure;
import net.h4bbo.avatara4j.figure.types.legacy.LegacyPart;
import net.h4bbo.avatara4j.figure.util.FileUtil;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to read legacy figure data from an XML file and parse it into {@link LegacyFigure} objects.
 */
public class LegacyFiguredataReader {
    // Private static instance of the class for singleton pattern
    private static volatile LegacyFiguredataReader instance = null;
    private List<LegacyFigure> legacyFiguredata;

    // Private constructor for singleton pattern
    private LegacyFiguredataReader() {}

    /**
     * Retrieves the single instance of {@link LegacyFiguredataReader}.
     * @return The single instance of this class.
     */
    public static LegacyFiguredataReader getInstance() {
        if (instance == null) {
            synchronized (LegacyFiguredataReader.class) {
                if (instance == null) {
                    instance = new LegacyFiguredataReader();
                }
            }
        }
        return instance;
    }

    /**
     * Retrieves the legacy figure data from the XML file.
     * @return List of {@link LegacyFigure} objects parsed from the XML.
     */
    public void load() {
        this.legacyFiguredata = this.loadLegacyFiguredata();
    }

    private List<LegacyFigure> loadLegacyFiguredata() {
        List<LegacyFigure> figuredata = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(FileUtil.getInstance().getFile("figuredata", "figuredata_oldschool"));
            document.getDocumentElement().normalize();

            NodeList genderNodes = document.getElementsByTagName("genders");
            for (int i = 0; i < genderNodes.getLength(); i++) {
                Node genderNode = genderNodes.item(i);
                NodeList childNodes = genderNode.getChildNodes();

                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node childNode = childNodes.item(j);

                    if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                        parseGenderNode((Element) childNode, figuredata);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return figuredata;
    }

    private void parseGenderNode(Element genderElement, List<LegacyFigure> figureList) {
        NodeList childNodes = genderElement.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node childNode = childNodes.item(j);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                String genderType = childNode.getNodeName().equalsIgnoreCase("female") ? "female" : "male";
                parseFigureData((Element) childNode, genderType, figureList);
            }
        }
    }

    private void parseFigureData(Element figureElement, String genderType, List<LegacyFigure> figureList) {
        int sprite = Integer.parseInt(figureElement.getElementsByTagName("sprite").item(0).getTextContent());

        List<LegacyPart> partsList = new ArrayList<>();
        Node partsNode = figureElement.getElementsByTagName("parts").item(0);
        if (partsNode != null) {
            NodeList partNodes = partsNode.getChildNodes();
            for (int i = 0; i < partNodes.getLength(); i++) {
                Node partNode = partNodes.item(i);
                if (partNode.getNodeType() == Node.ELEMENT_NODE) {
                    String partName = partNode.getNodeName();
                    int partValue = Integer.parseInt(partNode.getTextContent());
                    partsList.add(new LegacyPart(partName, partValue));
                }
            }
        }

        List<String> colours = new ArrayList<>();
        NodeList colourNodes = figureElement.getElementsByTagName("colour");
        for (int l = 0; l < colourNodes.getLength(); l++) {
            Element colourElement = (Element) colourNodes.item(l);
            colours.add(colourElement.getTextContent());
        }

        LegacyFigure figure = new LegacyFigure(sprite, partsList, colours, genderType);
        figureList.add(figure);
    }

    public List<LegacyFigure> getLegacyFiguredata() {
        return legacyFiguredata;
    }

}
