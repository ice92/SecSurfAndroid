package com.ftunram.secsurf.toolkit;

import java.io.File;

public class Utils {
    public static final String avi = "avi";
    public static final String enc = "enc";
    public static final String flv = "flv";
    public static final String gif = "gif";
    public static final String jpeg = "jpeg";
    public static final String jpg = "jpg";
    public static final String mov = "mov";
    public static final String mp4 = "mp4";
    public static final String mpg = "mpg";
    public static final String png = "png";
    public static final String tif = "tif";
    public static final String tiff = "tiff";

    public static String getExtension(File f) {
        String s = f.getName();
        int i = s.lastIndexOf(46);
        if (i <= 0 || i >= s.length() - 1) {
            return null;
        }
        return s.substring(i + 1).toLowerCase();
    }
}
