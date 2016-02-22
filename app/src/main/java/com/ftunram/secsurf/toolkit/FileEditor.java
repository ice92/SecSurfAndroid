package com.ftunram.secsurf.toolkit;

import java.io.File;

public class FileEditor {
    public static boolean delete(String filename) {
        return new File(filename).renameTo(new File(filename + "Delete"));
    }
}
