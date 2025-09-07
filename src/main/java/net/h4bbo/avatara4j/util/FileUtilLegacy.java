package net.h4bbo.avatara4j.util;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for common file operations, particularly for XML file handling.
 */
public class FileUtilLegacy {

    /**
     * Finds and parses the first XML file in the output directory
     * whose name (without extension) contains the directory name.
     *
     * @param outputDirectory The directory to search for files.
     * @return The parsed XML Document, or null if not found or error occurs.
     */
    public static Document solveXmlFile(String outputDirectory) {
        return solveXmlFile(outputDirectory, null);
    }

    /**
     * Finds and parses the first XML file in the output directory
     * whose name (without extension) contains the given substring.
     * If fileNameContains is null, the directory name is used as the pattern.
     *
     * @param outputDirectory  The directory to search for files.
     * @param fileNameContains Substring to search for in file names.
     * @return The parsed XML Document, or null if not found or error occurs.
     */
    public static Document solveXmlFile(String outputDirectory, String fileNameContains) {
        if (fileNameContains == null) {
            fileNameContains = outputDirectory;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(outputDirectory))) {
            for (Path entry : stream) {
                String fileName = getFileNameWithoutExtension(entry.getFileName().toString());
                if (fileName.contains(fileNameContains)) {
                    return readXmlFile(entry.toAbsolutePath().toString());
                }
            }
        } catch (IOException e) {
            // Handle exception as needed
        }
        return null;
    }

    /**
     * Reads and parses an XML file, correcting known issues:
     * 1. Removes a newline before XML declaration.
     * 2. Removes <graphics> tags.
     *
     * @param file The path to the XML file.
     * @return The parsed XML Document, or null if an error occurs.
     */
    public static Document readXmlFile(String file) {
        try {
            String text = new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);

            // Remove newline before XML declaration if present
            if (text.contains("\n<?xml")) {
                text = text.replace("\n<?xml", "<?xml");
                Files.write(Paths.get(file), text.getBytes(StandardCharsets.UTF_8));
            }

            // Remove <graphics> tags if present
            if (text.contains("<graphics>")) {
                text = text.replace("<graphics>", "");
                text = text.replace("</graphics>", "            ");
                Files.write(Paths.get(file), text.getBytes(StandardCharsets.UTF_8));
            }

            // Parse XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(file));
            return doc;

        } catch (Exception e) {
            // Handle exception as needed
            return null;
        }
    }

    /**
     * Finds the first file in the directory whose name matches the given pattern.
     * Uses endsWith by default.
     *
     * @param outputDirectory  The directory to search for files.
     * @param fileNameContains The string to match in file names.
     * @return The full path of the found file, or null if not found.
     */
    public static String solveFile(String outputDirectory, String fileNameContains) {
        return solveFile(outputDirectory, fileNameContains, true, false);
    }

    /**
     * Finds the first file in the directory whose name matches the pattern according to the given flags.
     *
     * @param outputDirectory  The directory to search for files.
     * @param fileNameContains The string to match in file names.
     * @param endsWith         If true, matches files ending with fileNameContains.
     * @param equals           If true, matches files exactly equal to fileNameContains.
     * @return The full path of the found file, or null if not found.
     */
    public static String solveFile(String outputDirectory, String fileNameContains, boolean endsWith, boolean equals) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(outputDirectory))) {
            for (Path entry : stream) {
                String fileName = getFileNameWithoutExtension(entry.getFileName().toString());
                if (equals) {
                    if (fileName.equals(fileNameContains)) {
                        return entry.toAbsolutePath().toString();
                    }
                } else if (endsWith) {
                    if (fileName.endsWith(fileNameContains)) {
                        return entry.toAbsolutePath().toString();
                    }
                } else {
                    if (fileName.contains(fileNameContains)) {
                        return entry.toAbsolutePath().toString();
                    }
                }
            }
        } catch (IOException e) {
            // Handle exception as needed
        }
        return null;
    }

    /**
     * Returns the lowercase alphabet letter at the specified index (0 = 'a', 1 = 'b', ...).
     * If the index is out of range, returns an empty string.
     *
     * @param animationLayer The index of the letter.
     * @return The corresponding letter as a string, or empty string if out of bounds.
     */
    public static String numericLetter(int animationLayer) {
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        if (animationLayer >= 0 && animationLayer < alphabet.length) {
            return String.valueOf(alphabet[animationLayer]);
        }
        return "";
    }

    /**
     * Returns the file name without its extension.
     *
     * @param filename The file name with extension.
     * @return The file name without extension.
     */
    private static String getFileNameWithoutExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? filename : filename.substring(0, dotIndex);
    }
}