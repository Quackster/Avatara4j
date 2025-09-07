# Avatara4j

**Avatara4j** is a **Java port** of the original **[Avatara](https://github.com/Quackster/Avatara)** library written in C#. It brings the same figure & badge rendering engine to the JVM, allowing Java developers to generate high‑quality Habbo avatars and badges in PNG or GIF formats.

**Built and tested against Java 1.8**

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

## Features

| Feature | Description |
|---------|-------------|
| **Figure rendering** | Create avatars in various sizes (`b`, `l`, `s`) with custom body/head directions, actions, gestures, and frames. |
| **Badge rendering** | Render custom badges using the same template system, support shockwave, background options, and output formats. |
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
```

***Tip: Keep the `load()` calls to a single place in your application (e.g., a static block or a Spring `@PostConstruct`). They populate caches and ensure fast rendering thereafter.***

## API Overview

| Class | Responsibility |
|-------|----------------|
| `FiguredataReader` | Parses the main figuredata XML (and JSON for legacy data). |
| `LegacyFiguredataReader` | Handles legacy figuredata files used in older Avatara clients. |
| `ManifestReader` | Loads figure offsets for rendering. |
| `Avatar` | Main renderer for figures. Configure size, direction, action, etc. |
| `Badge` | Generates badges from badge data strings. |
| `BadgeSettings` | Configure badge rendering options (shockwave, background, format). |
| `RenderType` | Enum (`PNG`, `GIF`) – output format. |

All core classes are in the `net.h4bbo.avatara4j` package and its sub‑packages.