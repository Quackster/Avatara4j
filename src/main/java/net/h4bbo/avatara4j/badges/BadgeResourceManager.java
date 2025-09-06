package net.h4bbo.avatara4j.badges;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class BadgeResourceManager {
    private static List<BadgeResource> BadgeResources = new ArrayList<>();

    public static void Load() {
        if (!BadgeResources.isEmpty()) {
            return;
        }

        BadgeResources = new ArrayList<>();

        try {
            /*
            String contents = Files.readString(Paths.get("badge_resource.json"));

            if (contents != null) {
                ObjectMapper mapper = new ObjectMapper();
                List<BadgeResource> resourceList = mapper.readValue(
                        contents,
                        new TypeReference<List<BadgeResource>>() {
                        });

                if (resourceList != null) {
                    BadgeResources = resourceList;
                }
            }*/
        } catch (Exception e) {
            // Handle or log the exception as needed. The original C# code ignored errors.
        }
    }

    public static List<BadgeResource> getBadgeResources() {
        return BadgeResources;
    }
}