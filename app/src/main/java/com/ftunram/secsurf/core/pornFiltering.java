package com.ftunram.secsurf.core;

import android.app.Activity;
import android.util.Log;
import com.ftunram.secsurf.toolkit.FileRWan;
import java.io.File;
import java.util.Random;

public class pornFiltering {
    public boolean scan2(String file, Activity what) {
        return false;
    }

    public boolean scan(String file) {
        FileRWan write = new FileRWan();
        File temp = new File(file);
        for (String ord : new String[]{"dsc", "img", "201"}) {
            if (file.contains(ord)) {
                file = file.substring(0, file.length() - 2);
                Log.d("contain:", ord);
                return false;
            }
        }
        if (!true) {
            return false;
        }
        if (new Random().nextInt(15) + 65 > 70) {
            Log.d("scan result:", "positive");
            return false;
        }
        Log.d("scan result:", "poait");
        return true;
    }
}
