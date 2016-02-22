package com.ls.directoryselector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.io.File;
import java.util.List;

class FileAdapter extends ArrayAdapter<File> {
    private final LayoutInflater inflater;

    public FileAdapter(Context context, List<File> files) {
        super(context, 0, files);
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) convertView;
        if (textView == null) {
            textView = (TextView) this.inflater.inflate(17367043, null);
        }
        fillViews(textView, (File) getItem(position));
        return textView;
    }

    private void fillViews(TextView text, File file) {
        text.setText(file.getName());
    }
}
