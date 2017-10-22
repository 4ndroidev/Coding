package com.androidev.coding.misc;


import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Misc {

    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static boolean isImage(String name) {
        return name.endsWith(".png") ||
                name.endsWith(".jpg") ||
                name.endsWith(".webp") ||
                name.endsWith(".gif") ||
                name.endsWith(".svg");
    }

    public static Date time2date(String timestamp) {
        return DATE_FORMAT.parse(timestamp, new ParsePosition(0));
    }

}
