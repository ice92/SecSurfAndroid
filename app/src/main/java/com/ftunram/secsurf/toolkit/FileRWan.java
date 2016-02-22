package com.ftunram.secsurf.toolkit;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileRWan {
    public String read(String file) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while (true) {
                String line = br.readLine();
                if (line != null) {
                    text.append(line);
                    text.append('\n');
                } else {
                    br.close();
                    return text.toString();
                }
            }
        } catch (IOException e) {
            Log.e("filerw", e.getMessage());
            return null;
        }
    }

    public boolean write(Context context, String input, File file) {
        Log.e("filenyagan", file.getName());
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(file.getName(), 0));
            outputStreamWriter.write(input);
            outputStreamWriter.close();
            return true;
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            return false;
        }
    }
}
