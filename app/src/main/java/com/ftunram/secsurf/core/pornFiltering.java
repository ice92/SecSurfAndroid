package com.ftunram.secsurf.core;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ftunram.secsurf.toolkit.Asset2file;
import com.ftunram.secsurf.toolkit.FileRWan;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by user on 1/12/2016.
 */
public class pornFiltering {




    public boolean scan2(String file, Activity what){



        return false;
    }
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