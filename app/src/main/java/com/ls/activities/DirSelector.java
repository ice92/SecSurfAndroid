package com.ls.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.ls.directoryselectordemo.R;
import org.opencv.BuildConfig;

public class DirSelector extends Activity {
    private static final int RQS_OPEN_DOCUMENT_TREE = 2;
    TextView textInfo;

    /* renamed from: com.ls.activities.DirSelector.1 */
    class C00841 implements OnClickListener {
        C00841() {
        }

        public void onClick(View v) {
            DirSelector.this.startActivityForResult(new Intent("android.intent.action.OPEN_DOCUMENT_TREE"), DirSelector.RQS_OPEN_DOCUMENT_TREE);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.textInfo = (TextView) findViewById(R.id.info);
        ((Button) findViewById(R.id.opendocument)).setOnClickListener(new C00841());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.textInfo.setText(BuildConfig.FLAVOR);
        if (resultCode == -1 && requestCode == RQS_OPEN_DOCUMENT_TREE) {
            Uri uriTree = data.getData();
            this.textInfo.append(uriTree.toString() + "\n");
            this.textInfo.append("=====================\n");
            for (DocumentFile file : DocumentFile.fromTreeUri(this, uriTree).listFiles()) {
                this.textInfo.append(file.getName() + "\n");
                if (file.isDirectory()) {
                    this.textInfo.append("is a Directory\n");
                } else {
                    this.textInfo.append(file.getType() + "\n");
                }
                this.textInfo.append("file.canRead(): " + file.canRead() + "\n");
                this.textInfo.append("file.canWrite(): " + file.canWrite() + "\n");
                this.textInfo.append(file.getUri() + "\n");
                this.textInfo.append("---------------------\n");
            }
        }
    }
}
