package com.Athena.FIX.Engine.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Wayne Sidney
 * Created on {09/07/2022}
 */
public class Field {

    private static final Pattern PATTERN = Pattern.compile("(?<tag>\\d{1,5})=(?<value>.+)");

    private final int    tag;
    private final String value;

    Field(int tag, String value) {
        this.tag   = tag;
        this.value = value;
    }

    static Field get(String s) {
        Matcher matcher = PATTERN.matcher(s);
        if (!matcher.matches())
            throw new IllegalArgumentException();

        int    tag   = Integer.parseInt(matcher.group("tag"));
        String value = matcher.group("value");

        return new Field(tag, value);
    }

    int getTag() {
        return tag;
    }

    String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%d=%s", tag, value);
    }

}
