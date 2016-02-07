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
public class FolderFilter implements FileFilter {

    
        @Override
        public boolean accept(File file) {
            return file.isDirectory();
        }      


    public String getDescription() {
        return "Just Directory";
    }
    
}
