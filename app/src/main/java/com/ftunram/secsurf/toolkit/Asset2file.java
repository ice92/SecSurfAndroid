package com.ftunram.secsurf.toolkit;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ice on 1/26/2016.
 */
public class Asset2file {
    public File createFileFromInputStream(InputStream inputStream) {

        try{

            String dir = Environment.getExternalStorageDirectory()+File.separator+"secsurf";
            File folder = new File(dir); //folder name
            folder.mkdirs();
            File f = new File(dir,"mySVM.xml");
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        }catch (IOException e) {
            //Logging exception
        }

        return null;
    }
}
