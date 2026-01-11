package com.habbo.figure;

/**
 * Represents the offset for positioning an asset image.
 */
public class AssetOffset {
    private final String assetName;
    private final int x;
    private final int y;

    public AssetOffset(String assetName, int x, int y) {
        this.assetName = assetName;
        this.x = x;
        this.y = y;
    }

    public String getAssetName() {
        return assetName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "AssetOffset{name='" + assetName + "', x=" + x + ", y=" + y + "}";
    }
}
