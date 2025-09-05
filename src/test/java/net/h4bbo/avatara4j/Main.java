package net.h4bbo.avatara4j;

import net.h4bbo.avatara4j.figure.Avatar;
import net.h4bbo.avatara4j.figure.readers.FiguredataReader;
import net.h4bbo.avatara4j.figure.readers.LegacyFiguredataReader;
import net.h4bbo.avatara4j.figure.readers.ManifestReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        System.out.println("Loading figuredata for Avatara4j");
        FiguredataReader.getInstance().load();
        LegacyFiguredataReader.getInstance().load();

        System.out.println("Legacy figuredata entries: " + LegacyFiguredataReader.getInstance().getLegacyFiguredata().size());

        System.out.println("Loading figure offsets");
        ManifestReader.getInstance().load();
        System.out.println("Loaded " + ManifestReader.getInstance().getParts().size() + " figure offsets!");

        writeDefaultFigure();
        writeOldschoolFigure();
    }

    private static void writeOldschoolFigure() {
        // The imager arguments
        String figure = "1000118001270012900121001";
        String size = "l";
        int bodyDirection = 4;
        int headDirection = 4;
        String action = "std";
        String gesture = "std";
        boolean headOnly = false;
        int frame = 1;
        int carryDrink = -1;
        boolean cropImage = false;

        try {
            Avatar avatar = new Avatar(
                    FiguredataReader.getInstance(), figure, size, bodyDirection, headDirection,
                    action, gesture, headOnly, frame, carryDrink, cropImage
            );
            Files.write(Paths.get("figure_oldschool.png"), avatar.run());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeDefaultFigure() {
        // The imager arguments
        String figure = "hd-180-1.hr-100-61.ch-210-66.lg-270-82.sh-290-80";
        String size = "b";
        int bodyDirection = 2;
        int headDirection = 2;
        String action = "wav";
        String gesture = "std";
        boolean headOnly = false;
        int frame = 1;
        int carryDrink = -1;
        boolean cropImage = false;

        try {
            Avatar avatar = new Avatar(
                    FiguredataReader.getInstance(), figure, size, bodyDirection, headDirection,
                    action, gesture, headOnly, frame, carryDrink, cropImage
            );
            Files.write(Paths.get("figure_default.png"), avatar.run());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*        /*
        try {
            // Read JSON from the URL
            URL url = new URL("https://raw.githubusercontent.com/Alcosmos/habbo-old-figure-converter/refs/heads/main/imager/oldfiguredata.json");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder sb = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
            in.close();

            // Parse JSON
            JSONObject jsonObject = new JSONObject(sb.toString());

            // Convert to XML
            String xmlString = XML.toString(jsonObject, null,  XMLParserConfiguration.ORIGINAL, 2);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.xml"))) {
                writer.write(xmlString);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


// getOldschoolJsonData();

ObjectMapper mapper = new ObjectMapper();

// Read JSON as Map
Map<String, Object> originalData = null;
        try {
originalData = mapper.readValue(getOldschoolJsonData(), Map.class);
        } catch (
JsonProcessingException e) {
        throw new RuntimeException(e);
        }

// Mapping of old keys to new keys
Map<String, String> renameMap = new HashMap<>();
        renameMap.put("p", "parts");
        renameMap.put("c", "colors");
        renameMap.put("s","sprite");
        renameMap.put("M", "male");
        renameMap.put("F", "female");

// Renaming process
Map<String, Object> renamedData = (Map<String, Object>) renameKeysRecursive(originalData, renameMap);

// Output result as JSON
String resultJson = null;
        try {
resultJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(renamedData);
        } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
        }


                    String xmlString = XML.toString(jsonObject, null,  XMLParserConfiguration.ORIGINAL, 2);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.xml"))) {
                writer.write(xmlString);
            }


                try (
BufferedWriter writer = new BufferedWriter(new FileWriter("formatted_figuredata.json"))) {
        writer.write(resultJson);
        } catch (IOException e) {
        throw new RuntimeException(e);
        }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter("formatted_figuredata.xml"))) {
        writer.write(XML.toString(new JSONObject(resultJson), null,  XMLParserConfiguration.ORIGINAL, 2));
        } catch (IOException e) {
        throw new RuntimeException(e);
        }
                }

@SuppressWarnings("unchecked")
public static Object renameKeysRecursive(Object data, Map<String, String> renameMap) {
    if (data instanceof Map) {
        Map<String, Object> map = (Map<String, Object>) data;
        Map<String, Object> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // Flatten/remove "array" key, merge its map elements into parent
            if ("array".equals(key)) {
                Object arrValue = renameKeysRecursive(value, renameMap);
                if (arrValue instanceof List) {
                    for (Object item : (List<?>) arrValue) {
                        if (item instanceof Map) {
                            result.putAll((Map<? extends String, ?>) item);
                        }
                        // If not a Map, skip or handle as desired
                    }
                }
                // If not a list, skip
                continue;
            }

            // Convert "c" to colours:[{colour:...}]
            if ("c".equals(key)) {
                List<Object> coloursList = new ArrayList<>();
                Map<String, Object> colourObj = new HashMap<>();
                colourObj.put("colour", renameKeysRecursive(value, renameMap));
                coloursList.add(colourObj);

                Object existingColours = result.get("colours");
                if (existingColours instanceof List) {
                    ((List<Object>) existingColours).addAll(coloursList);
                } else {
                    result.put("colours", coloursList);
                }
                continue;
            }

            // Handle "parts" that is a List: merge to single map if each is a single-key map
            String newKey = renameMap.getOrDefault(key, key);
            Object newValue = renameKeysRecursive(value, renameMap);
            if ("parts".equals(newKey) && newValue instanceof List) {
                List<?> list = (List<?>) newValue;
                boolean allSingleKeyMaps = list.stream().allMatch(
                        item -> item instanceof Map && ((Map<?, ?>) item).size() == 1
                );
                if (allSingleKeyMaps && !list.isEmpty()) {
                    Map<String, Object> merged = new HashMap<>();
                    for (Object item : list) {
                        merged.putAll((Map<? extends String, ?>) item);
                    }
                    result.put("parts", merged);
                } else {
                    result.put(newKey, newValue);
                }
            } else {
                result.put(newKey, newValue);
            }
        }
        return result;
    } else if (data instanceof List) {
        List<Object> list = (List<Object>) data;
        List<Object> result = new ArrayList<>();
        for (Object item : list) {
            result.add(renameKeysRecursive(item, renameMap));
        }
        return result;
    } else {
        return data;
    }
}


private String getOldschoolJsonData() {
    StringBuilder sb = new StringBuilder();
    try {
        URL url = new URL("https://raw.githubusercontent.com/Alcosmos/habbo-old-figure-converter/refs/heads/main/imager/oldfiguredata.json");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }

        in.close();
    } catch (IOException e) {

    }

    return sb.toString();
}*/