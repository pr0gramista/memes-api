package com.poprosturonin.utils;

public class ParsingUtils {
    /**
     * Parses (with trimming) given String to Integer. In case of NumberFormatException this
     * method should return 0
     *
     * @param string string to parse
     * @return number parsed int or 0 if parsing failed
     */
    public static int parseIntOrGetZero(String string) {
        try {
            return Integer.parseInt(string.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}