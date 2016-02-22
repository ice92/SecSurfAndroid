package com.ftunram.secsurf.toolkit;

import java.io.File;
import java.io.FileFilter;

public class EncrypFilter implements FileFilter {
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
        if (extension != null) {
            return extension.equals(Utils.enc);
        }
        return false;
    }

    public String getDescription() {
        return "Encrypted File";
    }
}
