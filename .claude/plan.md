# Habbo Figure Renderer - Implementation Plan

## Status: COMPLETE

## Overview
Create a Java application that renders Habbo avatar figures from a figure string like:
`hd-180-1.hr-100-61.ch-210-66.lg-270-82.sh-290-80`

## Figure String Format
Each part is separated by `.` and follows the pattern: `TYPE-SETID-COLORID`
- `hd` = head (includes body, eyes, face, hands)
- `hr` = hair
- `ch` = chest/shirt (includes sleeves)
- `lg` = legs
- `sh` = shoes
- `ha` = hat (optional)
- Other types: `he`, `ea`, `fa`, `ca`, `wa`, etc.

## Data Files
1. **figuredata.xml** - Main configuration:
   - Palettes with color definitions (hex colors)
   - Set types mapping set IDs to part definitions
   - Each set contains multiple parts with types (bd, hd, hr, ch, lg, sh, etc.)

2. **xml/*.xml** - Asset manifests:
   - Define image offsets for proper positioning
   - Pattern: `h_{action}_{parttype}_{partid}_{direction}_{frame}.png`

3. **images/*.png** - Sprite images for each part

## Image Naming Convention
`h_{action}_{type}_{id}_{direction}_{frame}.png`
- action: std (stand), wlk (walk), sit, crr (carry), etc.
- type: bd (body), hd (head), hr (hair), ch (chest), lg (legs), sh (shoes), etc.
- id: part ID from figuredata.xml
- direction: 0-7 (viewing angle)
- frame: animation frame (usually 0)

## Color Application
- Colors from palette are applied to colorable parts using tinting
- RGB values are multiplied with the palette color
- Each palette ID corresponds to a category (1=skin, 2=hair, 3=clothes)

## Implementation Steps

### Phase 1: Data Models [COMPLETE]
- [x] Create `FigureData` class to parse figuredata.xml
- [x] Create `FigureColor` class to hold palette colors
- [x] Create `FigureSetType` class to hold set definitions
- [x] Create `FigurePart` class to represent individual parts
- [x] Create `AssetManager` class to parse XML asset files and load images

### Phase 2: Figure Parser [COMPLETE]
- [x] Create `FigureStringParser` to parse "hd-180-1.hr-100-61..." format
- [x] Map set IDs to actual part definitions
- [x] Resolve color IDs to hex colors

### Phase 3: Asset Loading [COMPLETE]
- [x] Load and parse all XML manifests for offsets
- [x] Create image loader to load PNG sprites
- [x] Cache loaded images for performance

### Phase 4: Renderer [COMPLETE]
- [x] Create `FigureRenderer` class
- [x] Implement part layering/ordering (draw order matters)
- [x] Apply color tinting to colorable parts
- [x] Position parts using manifest offsets
- [x] Composite final image

### Phase 5: Main Entry Point [COMPLETE]
- [x] Create `Main` class with CLI interface
- [x] Accept figure string as argument
- [x] Output rendered PNG image

## Project Structure
```
figuredata/
├── src/main/java/com/habbo/figure/
│   ├── Main.java              - CLI entry point
│   ├── FigureRenderer.java    - Main rendering engine
│   ├── FigureData.java        - figuredata.xml parser
│   ├── FigureStringParser.java - Figure string parser
│   ├── FigureSetType.java     - Set type model
│   ├── FigureSet.java         - Figure set model
│   ├── FigurePart.java        - Part model
│   ├── FigurePalette.java     - Color palette model
│   ├── FigureColor.java       - Individual color model
│   ├── AssetManager.java      - Asset loading and caching
│   └── AssetOffset.java       - Asset positioning data
├── build.bat                  - Windows build script
├── build.sh                   - Unix build script
├── run.bat                    - Windows run script
├── run.sh                     - Unix run script
├── figuredata.xml             - Main figure data
├── xml/                       - Asset manifests
└── images/                    - Sprite images
```

## Usage
```bash
# Build
./build.bat  (Windows)
./build.sh   (Unix)

# Run
./run.bat "hd-180-1.hr-100-61.ch-210-66.lg-270-82.sh-290-80" output.png [direction] [action]

# Examples
./run.bat "hd-180-1.hr-100-61.ch-210-66.lg-270-82.sh-290-80" figure.png 2 std
./run.bat "hd-180-1.hr-100-61.ch-210-66.lg-270-82.sh-290-80" figure_back.png 4 std
```

## Draw Order (Bottom to Top)
1. Body (bd)
2. Shoes (sh)
3. Legs (lg)
4. Chest (ch)
5. Left/Right sleeves (ls, rs)
6. Left/Right hands (lh, rh)
7. Head (hd)
8. Face (fc)
9. Eyes (ey)
10. Hair back (hrb)
11. Hair front (hr)
12. Accessories (ha, he, ea, fa, ca, wa)

## Offset Calculation (Flash/Habbo Convention)
The offset values in XML manifest files represent the sprite's **registration point** position.
- `offsetX` is typically negative (e.g., -22), indicating horizontal offset from reg point
- `offsetY` is typically positive (e.g., 74), indicating vertical offset from reg point

To position sprites correctly, **subtract** the offset from the canvas origin:
```java
int x = ORIGIN_X - offsetX;  // For offsetX=-20: x = 2 - (-20) = 22
int y = ORIGIN_Y - offsetY;  // For offsetY=74: y = 100 - 74 = 26
```

Canvas configuration (matches official Habbo "m" size):
- Width: 64 pixels
- Height: 110 pixels
- ORIGIN_X: 2 (figure registration point X)
- ORIGIN_Y: 100 (figure registration point Y, near bottom for feet)

## Color Tinting Rules
- Body parts (bd, hd, lh, rh): Tinted with skin color from hd palette
- Eyes (ey) and Face (fc): NOT tinted (keep original colors)
- Hair (hr, hrb): Tinted with hair color (if colorable=1 in set)
- Clothes: Tinted with respective clothing colors

## Completion Date
January 11, 2026
