package net.h4bbo.avatara4j.figure.readers;

import net.h4bbo.avatara4j.figure.types.FigureColor;
import net.h4bbo.avatara4j.figure.types.FigurePart;
import net.h4bbo.avatara4j.figure.types.FigureSet;
import net.h4bbo.avatara4j.figure.types.FigureSetType;
import net.h4bbo.avatara4j.util.FileUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.*;

public class FiguredataReader {
    private static final FiguredataReader INSTANCE = new FiguredataReader();

    private Map<Integer, List<FigureColor>> figurePalettes;
    private Map<String, FigureSetType> figureSetTypes;
    private Map<String, FigureSet> figureSets;
    private final Boolean mandatoryGenderFlag;

    private FiguredataReader() {
        this.figurePalettes = new HashMap<>();
        this.figureSetTypes = new HashMap<>();
        this.figureSets = new HashMap<>();
        this.mandatoryGenderFlag = null;
    }

    public void load() {
        try {
            loadFigureSets();
            loadFigureSetTypes();
            loadFigurePalettes();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadFigureSets() throws Exception {
        InputStream xmlFile = Objects.requireNonNull(FileUtil.getInstance().getFile("figuredata", "figuredata.xml"), "figuredata/figuredata.xml file was not found");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        NodeList list = document.getElementsByTagName("set");

        for (int i = 0; i < list.getLength(); i++) {
            Node set = list.item(i);
            String setType = set.getParentNode().getAttributes().getNamedItem("type").getNodeValue();
            String id = set.getAttributes().getNamedItem("id").getNodeValue();
            String gender = set.getAttributes().getNamedItem("gender").getNodeValue();
            String clubValue = set.getAttributes().getNamedItem("club").getNodeValue();
            boolean club = "1".equals(clubValue) || "2".equals(clubValue);
            boolean colourable = "1".equals(set.getAttributes().getNamedItem("colorable").getNodeValue());
            boolean selectable = "1".equals(set.getAttributes().getNamedItem("selectable").getNodeValue());

            FigureSet figureSet = new FigureSet(setType, id, gender, club, colourable, selectable);
            NodeList partList = set.getChildNodes();

            for (int j = 0; j < partList.getLength(); j++) {
                Node part = partList.item(j);

                if ("hiddenlayers".equals(part.getNodeName()) || part.getAttributes() == null) {
                    continue;
                }

                if (part.getAttributes().getNamedItem("id") != null &&
                        part.getAttributes().getNamedItem("type") != null &&
                        part.getAttributes().getNamedItem("colorable") != null &&
                        part.getAttributes().getNamedItem("index") != null) {

                    figureSet.getFigureParts().add(new FigurePart(
                            part.getAttributes().getNamedItem("id").getNodeValue(),
                            part.getAttributes().getNamedItem("type").getNodeValue(),
                            "1".equals(part.getAttributes().getNamedItem("colorable").getNodeValue()),
                            Integer.parseInt(part.getAttributes().getNamedItem("index").getNodeValue())
                    ));
                }
            }

            for (int j = 0; j < partList.getLength(); j++) {
                Node part = partList.item(j);

                if (!"hiddenlayers".equals(part.getNodeName())) {
                    continue;
                }

                NodeList hiddenLayerList = part.getChildNodes();

                for (int k = 0; k < hiddenLayerList.getLength(); k++) {
                    Node hiddenLayer = hiddenLayerList.item(k);
                    if (hiddenLayer.getAttributes() != null &&
                            hiddenLayer.getAttributes().getNamedItem("parttype") != null) {
                        figureSet.getHiddenLayers().add(hiddenLayer.getAttributes().getNamedItem("parttype").getNodeValue());
                    }
                }
            }

            this.figureSets.put(id, figureSet);
        }
    }

    public void loadFigurePalettes() throws Exception {
        InputStream xmlFile =  FileUtil.getInstance().getFile("figuredata", "figuredata.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        NodeList list = document.getElementsByTagName("palette");

        for (int i = 0; i < list.getLength(); i++) {
            Node palette = list.item(i);
            NodeList colourList = palette.getChildNodes();

            Integer paletteId = Integer.valueOf(Integer.parseInt(palette.getAttributes().getNamedItem("id").getNodeValue()));
            this.figurePalettes.put(paletteId, new ArrayList<>());

            for (int k = 0; k < colourList.getLength(); k++) {
                Node colour = colourList.item(k);

                if (colour.getAttributes() != null &&
                        colour.getAttributes().getNamedItem("id") != null &&
                        colour.getAttributes().getNamedItem("index") != null &&
                        colour.getAttributes().getNamedItem("club") != null &&
                        colour.getAttributes().getNamedItem("selectable") != null) {

                    String colourId = colour.getAttributes().getNamedItem("id").getNodeValue();
                    String index = colour.getAttributes().getNamedItem("index").getNodeValue();
                    String clubValue = colour.getAttributes().getNamedItem("club").getNodeValue();
                    boolean isClubRequired = "1".equals(clubValue) || "2".equals(clubValue);
                    boolean isSelectable = "1".equals(colour.getAttributes().getNamedItem("selectable").getNodeValue());

                    this.figurePalettes.get(paletteId).add(new FigureColor(colourId, index, isClubRequired, isSelectable, colour.getTextContent()));
                }
            }
        }
    }

    public void loadFigureSetTypes() throws Exception {
        InputStream xmlFile =  FileUtil.getInstance().getFile("figuredata", "figuredata.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);

        NodeList list = document.getElementsByTagName("settype");

        for (int i = 0; i < list.getLength(); i++) {
            Node setType = list.item(i);
            String set = setType.getAttributes().getNamedItem("type").getNodeValue();
            Integer paletteId = Integer.valueOf(Integer.parseInt(setType.getAttributes().getNamedItem("paletteid").getNodeValue()));

            boolean isMandatoryFlag = setType.getAttributes().getNamedItem("mandatory") != null;

            Boolean isMandatory = null;
            Boolean isMaleMandatoryNonHC = null;
            Boolean isMaleMandatoryHC = null;
            Boolean isFemaleMandatoryNonHC = null;
            Boolean isFemaleMandatoryHC = null;

            if (isMandatoryFlag) {
                String mandatoryValue = setType.getAttributes().getNamedItem("mandatory").getNodeValue();
                isMandatory = (Boolean) "1".equals(mandatoryValue);

                if (isMandatory != null) {
                    isMaleMandatoryHC = isMandatory;
                    isMaleMandatoryNonHC = isMandatory;
                    isFemaleMandatoryHC = isMandatory;
                    isFemaleMandatoryNonHC = isMandatory;
                }
            } else {
                Node mandM0 = setType.getAttributes().getNamedItem("mand_m_0");
                isMaleMandatoryNonHC = (Boolean) (mandM0 != null && "1".equals(mandM0.getNodeValue()));

                Node mandM1 = setType.getAttributes().getNamedItem("mand_m_1");
                isMaleMandatoryHC = (Boolean) (mandM1 != null && "1".equals(mandM1.getNodeValue()));

                Node mandF0 = setType.getAttributes().getNamedItem("mand_f_0");
                isFemaleMandatoryNonHC = (Boolean) (mandF0 != null && "1".equals(mandF0.getNodeValue()));

                Node mandF1 = setType.getAttributes().getNamedItem("mand_f_1");
                isFemaleMandatoryHC = (Boolean) (mandF1 != null && "1".equals(mandF1.getNodeValue()));
            }

            this.figureSetTypes.put(set, new FigureSetType(set, paletteId, isMandatory, isMaleMandatoryNonHC,
                    isMaleMandatoryHC, isFemaleMandatoryNonHC, isFemaleMandatoryHC));
        }
    }

    public static FiguredataReader getInstance() {
        return INSTANCE;
    }

    // Getters
    public Map<Integer, List<FigureColor>> getFigurePalettes() { return figurePalettes; }
    public Map<String, FigureSetType> getFigureSetTypes() { return figureSetTypes; }
    public Map<String, FigureSet> getFigureSets() { return figureSets; }
    public Boolean getMandatoryGenderFlag() { return mandatoryGenderFlag; }


}
