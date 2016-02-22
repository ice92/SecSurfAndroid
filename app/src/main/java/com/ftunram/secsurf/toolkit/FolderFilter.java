package com.ftunram.secsurf.toolkit;

import java.io.File;
import java.io.FileFilter;

public class FolderFilter implements FileFilter {
    public boolean accept(File file) {
        return file.isDirectory();
    }

    public String getDescription() {
        return "Just Directory";
    }
}
