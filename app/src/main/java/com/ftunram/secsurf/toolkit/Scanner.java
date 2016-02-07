package com.ftunram.secsurf.toolkit;

import android.util.Log;

import java.io.File;
import java.util.Random;

/**
 * Created by Ice on 12/9/2015.
 */
public class Scanner {

    public boolean scan(String file){
        boolean res=true;
        FileRWan write=new FileRWan();
        File temp=new File(file);
        String[] ordinals = {"dsc", "img", "201"};
        for (String ord : ordinals) {
            if (file.contains(ord)) {
                file = file.substring(0, file.length() - 2);
                res=false;
                Log.d("contain:", ord);
                return false;
            }
        }
        if(res==true) {
            Random r=new Random();
            int i=r.nextInt(80-65)+65;
            if(i>70){
                res=false;
                Log.d("scan result:", "positive");
                return false;
            }
            else{
                res=true;
                Log.d("scan result:", "poait");
                return true;
            }
        }
        else{
            return false;
        }
    }
}
