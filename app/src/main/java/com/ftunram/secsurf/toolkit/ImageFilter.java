package com.ftunram.secsurf.toolkit;

import java.io.File;
import java.io.FileFilter;

public class ImageFilter implements FileFilter {
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utils.getExtension(f);
        if (extension == null) {
            return false;
        }
        if (extension.equals(Utils.tiff) || extension.equals(Utils.tif) || extension.equals(Utils.gif) || extension.equals(Utils.jpeg) || extension.equals(Utils.jpg) || extension.equals(Utils.png) || extension.equals(Utils.avi) || extension.equals(Utils.flv) || extension.equals(Utils.mov) || extension.equals(Utils.mp4) || extension.equals(Utils.mpg)) {
            return true;
        }
        return false;
    }

    public String getDescription() {
        return "Images and Videos";
    }
}
