package com.ls.directoryselector.utils;

import java.io.File;
import java.io.FileFilter;

public class DirectoryFileFilter implements FileFilter {
    public boolean accept(File pathname) {
        return pathname.isDirectory();
    }
}
