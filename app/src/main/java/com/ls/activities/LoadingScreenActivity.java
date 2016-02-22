package com.ls.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import com.ls.directoryselectordemo.R;

public class LoadingScreenActivity extends Activity {
    private ProgressDialog progressDialog;

    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {
        private LoadViewTask() {
        }

        protected void onPreExecute() {
            LoadingScreenActivity.this.progressDialog = new ProgressDialog(LoadingScreenActivity.this);
            LoadingScreenActivity.this.progressDialog.setProgressStyle(1);
            LoadingScreenActivity.this.progressDialog.setTitle("Scanning...");
            LoadingScreenActivity.this.progressDialog.setMessage("Scanning porn file on folder, please wait...");
            LoadingScreenActivity.this.progressDialog.setCancelable(false);
            LoadingScreenActivity.this.progressDialog.setIndeterminate(false);
            LoadingScreenActivity.this.progressDialog.setMax(100);
            LoadingScreenActivity.this.progressDialog.setProgress(0);
            LoadingScreenActivity.this.progressDialog.show();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        protected java.lang.Void doInBackground(java.lang.Void... r6) {
            /*
            r5 = this;
            monitor-enter(r5);	 Catch:{ InterruptedException -> 0x001f }
            r0 = 0;
        L_0x0002:
            r2 = 4;
            if (r0 > r2) goto L_0x0025;
        L_0x0005:
            r2 = 1300; // 0x514 float:1.822E-42 double:6.423E-321;
            r5.wait(r2);	 Catch:{ all -> 0x001c }
            r0 = r0 + 1;
            r2 = 1;
            r2 = new java.lang.Integer[r2];	 Catch:{ all -> 0x001c }
            r3 = 0;
            r4 = r0 * 25;
            r4 = java.lang.Integer.valueOf(r4);	 Catch:{ all -> 0x001c }
            r2[r3] = r4;	 Catch:{ all -> 0x001c }
            r5.publishProgress(r2);	 Catch:{ all -> 0x001c }
            goto L_0x0002;
        L_0x001c:
            r2 = move-exception;
            monitor-exit(r5);	 Catch:{ all -> 0x001c }
            throw r2;	 Catch:{ InterruptedException -> 0x001f }
        L_0x001f:
            r1 = move-exception;
            r1.printStackTrace();
        L_0x0023:
            r2 = 0;
            return r2;
        L_0x0025:
            monitor-exit(r5);	 Catch:{ all -> 0x001c }
            goto L_0x0023;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.ls.activities.LoadingScreenActivity.LoadViewTask.doInBackground(java.lang.Void[]):java.lang.Void");
        }

        protected void onProgressUpdate(Integer... values) {
            LoadingScreenActivity.this.progressDialog.setProgress(values[0].intValue());
        }

        protected void onPostExecute(Void result) {
            LoadingScreenActivity.this.progressDialog.dismiss();
            LoadingScreenActivity.this.setContentView(R.layout.main);
            LoadingScreenActivity.this.finish();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new LoadViewTask().execute(new Void[0]);
    }
}
