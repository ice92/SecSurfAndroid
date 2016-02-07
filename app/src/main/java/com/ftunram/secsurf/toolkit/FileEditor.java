package com.ftunram.secsurf.toolkit;

import java.io.File;

/**
 * Created by Ice on 2/7/2016.
 */
public class FileEditor {
    public static boolean delete(String filename){
        File file =new File(filename);
        return file.delete();
    }
}
