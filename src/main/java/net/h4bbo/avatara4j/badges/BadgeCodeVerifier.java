package net.h4bbo.avatara4j.badges;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks whether a badge code is one this library can turn into a badge.
 *
 * <p>Both code layouts are accepted, so a caller never has to know which badge
 * editor produced the code:
 * <ul>
 *     <li><b>modern</b> — a three digit graphic id, a two digit colour and a
 *     location digit are always present: {@code b016051}, {@code s044024}.</li>
 *     <li><b>legacy</b> — a two digit graphic id, a two digit colour and an
 *     optional location digit: {@code b1605}, {@code s44024}.</li>
 * </ul>
 *
 * <p>Parts may follow each other directly or be separated with {@code X}, and a
 * single code may mix both layouts — exactly like
 * {@link Badge#parseBadgeData(BadgeSettings, String)} accepts them.
 *
 * <p>Graphic ids are deliberately not range checked. The table that maps an id to
 * a template is not populated by this port (see {@link BadgeResourceManager#Load()}),
 * so every id that fits the layout is accepted; an unknown id simply renders as
 * nothing.
 */
public final class BadgeCodeVerifier {

    /**
     * Parts per badge, matching the limit of the badge editor.
     */
    public static final int MAX_PARTS = 12;

    /**
     * Colour ids index into the palette of {@link Badge} and are one based.
     */
    public static final int MIN_COLOR_RESOURCE = 1;

    /**
     * Location slots known to {@link BadgePart#getPosition(java.awt.image.BufferedImage, java.awt.image.BufferedImage)}.
     * A part without a location digit defaults to the top left corner.
     */
    public static final int MIN_LOCATION = 0;
    public static final int MAX_LOCATION = 8;

    /**
     * Location of a legacy part that leaves the location digit out.
     */
    public static final int DEFAULT_LOCATION = -1;

    /**
     * A part is a base ({@code b}) or shape ({@code s}) marker followed by four to
     * six digits. Modern codes always carry six digits, legacy codes four or five.
     */
    private static final Pattern PART_CODE = Pattern.compile("[bs][0-9]{4,6}");

    /**
     * A code is a sequence of parts, with an optional separator between them.
     */
    private static final Pattern BADGE_CODE = Pattern.compile("[bs][0-9]{4,6}([Xx]?[bs][0-9]{4,6})*");

    /**
     * Beyond this length a part is a modern code, shorter is a legacy code.
     */
    private static final int SHORT_PART_LENGTH = 6;

    private BadgeCodeVerifier() {
        // Prevent instantiation
    }

    /**
     * Verifies a badge code of either layout.
     *
     * @param badgeCode the code to check, may be null
     * @return true when every part of the code can be parsed and rendered
     */
    public static boolean verify(String badgeCode) {
        if (badgeCode == null) {
            return false;
        }

        String code = badgeCode.trim();
        if (code.isEmpty() || !BADGE_CODE.matcher(code).matches()) {
            return false;
        }

        Matcher matcher = PART_CODE.matcher(code);
        int parts = 0;

        while (matcher.find()) {
            if (++parts > MAX_PARTS || !verifyPart(matcher.group())) {
                return false;
            }
        }

        return parts > 0;
    }

    /**
     * Splits a part the way {@link Badge#parseBadgeData(BadgeSettings, String)} does
     * and checks the fields it reads.
     */
    private static boolean verifyPart(String partCode) {
        boolean shortMethod = partCode.length() <= SHORT_PART_LENGTH;

        int color;
        int location;

        if (shortMethod) {
            color = Integer.parseInt(partCode.substring(3, 5));
            location = Integer.parseInt(partCode.length() > 5
                    ? partCode.substring(5)
                    : String.valueOf(DEFAULT_LOCATION));
        } else {
            color = Integer.parseInt(partCode.substring(4, 6));
            location = Integer.parseInt(partCode.substring(6));
        }

        if (color < MIN_COLOR_RESOURCE || color > Badge.MAX_COLOR_RESOURCE) {
            return false;
        }

        return location == DEFAULT_LOCATION
                || (location >= MIN_LOCATION && location <= MAX_LOCATION);
    }
}
