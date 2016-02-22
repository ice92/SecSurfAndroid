package com.ftunram.secsurf.toolkit;

import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.opencv.imgproc.Imgproc;

public class Asset2file {
    public File createFileFromInputStream(InputStream inputStream) {
        try {
            String dir = Environment.getExternalStorageDirectory() + File.separator + "secsurf";
            new File(dir).mkdirs();
            File file = new File(dir, "mySVM.xml");
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[Imgproc.INTER_TAB_SIZE2];
            while (true) {
                int length = inputStream.read(buffer);
                if (length > 0) {
                    outputStream.write(buffer, 0, length);
                } else {
                    outputStream.close();
                    inputStream.close();
                    return file;
                }
            }
        } catch (IOException e) {
            return null;
        }
    }
}
