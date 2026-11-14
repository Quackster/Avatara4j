# Avatara4j

**Avatara4j** is a **Java port** of the original **[Avatara](https://github.com/Quackster/Avatara)** library written in C#. It brings the same figure & badge rendering engine to the JVM, allowing Java developers to generate high‑quality Habbo avatars and badges in PNG or GIF formats.

**Built and tested against Java 1.8**

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

## Features

| Feature | Description |
|---------|-------------|
| **Figure rendering** | Create avatars in various sizes (`b`, `l`, `s`) with custom body/head directions, actions, gestures, and frames. |
| **Badge rendering** | Render custom badges using the same template system, support shockwave, background options, and output formats. |
| **Badge code verification** | One call (`Badge.verifyBadgeCode`) validates a code before you render or store it, accepting both the modern and the legacy badge code layout. |
| **Legacy data support** | Load both current and legacy figuredata files (XML) and manifest offsets. |
| **Extensible** | Badge settings, render type, and other parameters are fully configurable. |
| **Zero external dependencies** | Only standard JDK 1.8+. |

---

## Installation

### Gradle

Add the [JitPack](https://jitpack.io/#Quackster/Avatara4j) repository:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

Then add the dependency:

```groovy
dependencies {
    implementation 'com.github.Quackster:Avatara4j:v1.0.2'
}
```

### Maven

Add the [JitPack](https://jitpack.io/#Quackster/Avatara4j) repository:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Then add the dependency:

```xml
<dependencies>
    <dependency>
        <groupId>com.github.Quackster</groupId>
        <artifactId>Avatara4j</artifactId>
        <version>v1.0.2</version>
    </dependency>
</dependencies>
```


***The library ships with the bundled figure data used by the original Avatara. If you need to use a custom set, override the data loading process (see below).***

### Quick Usage

```java
        // Load data (once at startup)
        FiguredataReader.getInstance().load();
        LegacyFiguredataReader.getInstance().load();
        ManifestReader.getInstance().load();

        // Render an avatar
        Avatar avatar = new Avatar(
            FiguredataReader.getInstance(),
            "hd-180-1.hr-100-61.ch-210-66.lg-270-82.sh-290-80", // figure string
            "b",                                            // size
            2, 2,                                            // bodyDir, headDir
            "wav", "std",                                     // action, gesture
            false, 1, -1, false                              // headOnly, frame, carryDrink, cropImage
        );
        Files.write(Paths.get("avatar.png"), avatar.run());

        // Render a badge
        BadgeSettings settings = new BadgeSettings()
            .setShockwaveBadge(true)
            .setRenderType(RenderType.PNG)
            .setForceWhiteBackground(false);

        Badge badge = Badge.parseBadgeData(settings, "b1605Xs44024s17171");
        Files.write(Paths.get("badge.png"), badge.render());

        // Verify a code first — accepts both the modern and the legacy layout
        if (Badge.verifyBadgeCode("b016051Xs044024")) {
            Badge verified = Badge.parseBadgeData(settings, "b016051Xs044024");
        }
```

***Tip: Keep the `load()` calls to a single place in your application (e.g., a static block or a Spring `@PostConstruct`). They populate caches and ensure fast rendering thereafter.***

### Verifying badge codes

```java
boolean ok = Badge.verifyBadgeCode("b1605Xs44024s17171");
```

`Badge.verifyBadgeCode` (backed by `BadgeCodeVerifier`) validates a code without knowing which badge
editor produced it — both layouts are accepted, and one code may even mix them:

| Layout | Part format | Example |
|--------|-------------|---------|
| Modern | `b`/`s` + 3 digit graphic id + 2 digit colour + 1 digit location | `b016051`, `s044024` |
| Legacy | `b`/`s` + 2 digit graphic id + 2 digit colour + optional location | `b1605`, `s44024` |

A code needs at least one part and no more than `BadgeCodeVerifier.MAX_PARTS` (12, the badge editor
limit). Parts may follow each other directly or be separated with `X`. Colour ids must be within
`1..Badge.MAX_COLOR_RESOURCE` (the palette is one based) and location ids `0..8`. Graphic ids are not
range checked: the id → template table (`BadgeResourceManager`) is not populated by this port, so an
unknown graphic id renders as nothing rather than failing verification.

The check is stricter than `Badge.parseBadgeData`, which picks parts out of anywhere in the input
string, so it is suitable for validating user supplied codes before storing them.

## API Overview

| Class | Responsibility |
|-------|----------------|
| `FiguredataReader` | Parses the main figuredata XML (and JSON for legacy data). |
| `LegacyFiguredataReader` | Handles legacy figuredata files used in older Avatara clients. |
| `ManifestReader` | Loads figure offsets for rendering. |
| `Avatar` | Main renderer for figures. Configure size, direction, action, etc. |
| `Badge` | Generates badges from badge data strings. |
| `BadgeSettings` | Configure badge rendering options (shockwave, background, format). |
| `BadgeCodeVerifier` | Validates badge codes of both the modern and the legacy layout, behind `Badge.verifyBadgeCode`. |
| `RenderType` | Enum (`PNG`, `GIF`) – output format. |

All core classes are in the `net.h4bbo.avatara4j` package and its sub‑packages.