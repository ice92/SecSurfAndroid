/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ftunram.secsurf.toolkit;

import java.io.File;
import java.io.FileFilter;


/**
 *
 * @author I
 */
public class EncrypFilter implements FileFilter {

    @Override
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
