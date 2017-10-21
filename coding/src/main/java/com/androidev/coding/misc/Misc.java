package com.androidev.coding.misc;


public class Misc {

    public static boolean isImage(String name) {
        return name.endsWith(".png") ||
                name.endsWith(".jpg") ||
                name.endsWith(".webp") ||
                name.endsWith(".gif") ||
                name.endsWith(".svg");
    }

}
