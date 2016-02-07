/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ftunram.secsurf.toolkit;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author I
 */
public class ImageCounter {
    
      
    public ArrayList<String> getNumFiles(String path){
            File f = new File(path);
            ArrayList<String> mylist = new ArrayList<>();
            
            int i=0;
            for (File file : f.listFiles()) {
                    if (file.isFile()) {
                        String extension = Utils.getExtension(file);
                        try {
                            if (extension.equals(Utils.tiff)    ||
                                extension.equals(Utils.tif) ||
                                extension.equals(Utils.gif) ||
                                extension.equals(Utils.jpeg)||
                                extension.equals(Utils.jpg) ||
                                extension.equals(Utils.png) ||
                                extension.equals(Utils.avi) ||           
                                extension.equals(Utils.flv) ||
                                extension.equals(Utils.mov) ||
                                extension.equals(Utils.mp4) ||
                                extension.equals(Utils.mpg)
                                )
                        {mylist.add(file.getName());
                        i++;}                        
                        } catch (Exception e) {
                        }
                        
                    }
            }
            
            return mylist;
    }

}
