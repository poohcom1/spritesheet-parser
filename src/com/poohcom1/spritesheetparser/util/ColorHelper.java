package com.poohcom1.spritesheetparser.util;

public class ColorHelper {
    public ColorHelper() {}

    public static String colorIntToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static String colorIntToHexAlpha(int color) {
        return String.format("#%08X", (0xFFFFFF & color));
    }
}
