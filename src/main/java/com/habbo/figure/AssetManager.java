package com.habbo.figure;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * Manages loading and caching of figure assets (images and their offsets).
 */
public class AssetManager {
    private final String basePath;
    private final Map<String, AssetOffset> offsets;
    private final Map<String, BufferedImage> imageCache;

    public AssetManager(String basePath) {
        this.basePath = basePath;
        this.offsets = new HashMap<>();
        this.imageCache = new HashMap<>();
    }

    public void loadManifests() throws Exception {
        File xmlDir = new File(basePath, "xml");
        File[] xmlFiles = xmlDir.listFiles((dir, name) -> name.endsWith(".xml"));

        if (xmlFiles == null) {
            System.err.println("No XML files found in: " + xmlDir.getPath());
            return;
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        for (File xmlFile : xmlFiles) {
            try {
                Document doc = builder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                NodeList assetNodes = doc.getElementsByTagName("asset");
                for (int i = 0; i < assetNodes.getLength(); i++) {
                    Element assetElement = (Element) assetNodes.item(i);
                    String name = assetElement.getAttribute("name");

                    NodeList paramNodes = assetElement.getElementsByTagName("param");
                    for (int j = 0; j < paramNodes.getLength(); j++) {
                        Element paramElement = (Element) paramNodes.item(j);
                        if ("offset".equals(paramElement.getAttribute("key"))) {
                            String offsetValue = paramElement.getAttribute("value");
                            String[] parts = offsetValue.split(",");
                            if (parts.length == 2) {
                                int x = Integer.parseInt(parts[0].trim());
                                int y = Integer.parseInt(parts[1].trim());
                                offsets.put(name, new AssetOffset(name, x, y));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading manifest: " + xmlFile.getName() + " - " + e.getMessage());
            }
        }

        System.out.println("Loaded " + offsets.size() + " asset offsets");
    }

    public AssetOffset getOffset(String assetName) {
        return offsets.get(assetName);
    }

    public BufferedImage loadImage(String assetName) {
        if (imageCache.containsKey(assetName)) {
            return imageCache.get(assetName);
        }

        File imageFile = new File(basePath, "images/" + assetName + ".png");
        if (!imageFile.exists()) {
            return null;
        }

        try {
            BufferedImage original = ImageIO.read(imageFile);
            // Convert to ARGB format for consistent color handling
            BufferedImage image = new BufferedImage(
                original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
            java.awt.Graphics2D g = image.createGraphics();
            g.drawImage(original, 0, 0, null);
            g.dispose();
            imageCache.put(assetName, image);
            return image;
        } catch (IOException e) {
            System.err.println("Error loading image: " + assetName + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Build the asset name from components.
     * Format: h_{action}_{partType}_{partId}_{direction}_{frame}
     */
    public static String buildAssetName(String action, String partType, int partId, int direction, int frame) {
        return String.format("h_%s_%s_%d_%d_%d", action, partType, partId, direction, frame);
    }

    public Map<String, AssetOffset> getOffsets() {
        return offsets;
    }
}
