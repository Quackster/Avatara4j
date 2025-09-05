package net.h4bbo.avatara4j.figure.readers;

import net.h4bbo.avatara4j.figure.util.FileUtil;
import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

public class ManifestReader {
    private static final ManifestReader INSTANCE = new ManifestReader();

    public static ManifestReader getInstance() {
        return INSTANCE;
    }

    // public static Map<String, FigureAssetEntry> parts;
    private Map<String, String> parts;

    public void load() {
        parts = new HashMap<String, String>();

        //File xmlDir = new File("figuredata/xml");
        // File convDir = new File("figuredata/converter");
        // File imgDir = new File("figuredata/images");

        //if (!xmlDir.exists()) xmlDir.mkdirs();
        // if (!convDir.exists()) convDir.mkdirs();
        // if (!imgDir.exists()) imgDir.mkdirs();

        /*
        File oldData = new File("figuredata/converter/oldfiguredata.json");
        if (!oldData.exists()) {
            writeStringToFile(oldData, FigureWriter.OLD_FIGUREDATA);
        }
        File newData = new File("figuredata/converter/newfiguredata.json");
        if (!newData.exists()) {
            writeStringToFile(newData, FigureWriter.NEW_FIGUREDATA);
        }*/

        // Parse all XML manifests
        //File[] xmlFiles = xmlDir.listFiles();
        //if (xmlFiles != null) {
        //    for (File manifest : xmlFiles) {
        //        try {
        //            parseXML(manifest.getAbsolutePath());
        //        } catch (Exception e) {
        //           e.printStackTrace();
        //           return;
        //        }
        //    }
        //}

        List<InputStream> xmlFiles = FileUtil.getInstance().getFigureXmlFiles();
        if (xmlFiles != null) {
            for (InputStream manifest : xmlFiles) {
                try {
                    parseXML(manifest);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void parseXML(InputStream fileName) throws Exception {
        Document xmlFile = readXmlFile(fileName);
        NodeList list = xmlFile.getElementsByTagName("asset");

        for (int i = 0; i < list.getLength(); i++) {
            Node asset = list.item(i);
            NamedNodeMap attrs = asset.getAttributes();
            Node nameNode = attrs.getNamedItem("name");
            if (nameNode == null) continue;

            String name = nameNode.getNodeValue();
            String[] nameParts = name.split("_");
            if (nameParts.length < 3) continue;

            String offsets = null;
            NodeList children = asset.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if (child.getNodeType() == Node.ELEMENT_NODE && "param".equals(child.getNodeName())) {
                    NamedNodeMap paramAttrs = child.getAttributes();
                    Node keyNode = paramAttrs.getNamedItem("key");
                    Node valueNode = paramAttrs.getNamedItem("value");
                    if (keyNode != null && "offset".equals(keyNode.getNodeValue()) && valueNode != null) {
                        offsets = valueNode.getNodeValue();
                        break;
                    }
                }
            }

            if (name != null && offsets != null && !parts.containsKey(name)) {
                parts.put(name, offsets);
            }
        }
    }

    private Document readXmlFile(InputStream fileName) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(fileName);
    }

    public Map<String, String> getParts() {
        return parts;
    }
}
