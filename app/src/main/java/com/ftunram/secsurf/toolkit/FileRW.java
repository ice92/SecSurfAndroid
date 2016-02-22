package com.ftunram.secsurf.toolkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileRW {
    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String[] read(java.lang.String r9) {
        /*
        r8 = this;
        r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r3 = new java.lang.String[r4];
        r0 = new java.io.BufferedReader;	 Catch:{ IOException -> 0x0028 }
        r4 = new java.io.FileReader;	 Catch:{ IOException -> 0x0028 }
        r4.<init>(r9);	 Catch:{ IOException -> 0x0028 }
        r0.<init>(r4);	 Catch:{ IOException -> 0x0028 }
        r5 = 0;
        r2 = 0;
    L_0x0010:
        r4 = r0.readLine();	 Catch:{ Throwable -> 0x0033, all -> 0x004a }
        r3[r2] = r4;	 Catch:{ Throwable -> 0x0033, all -> 0x004a }
        if (r4 == 0) goto L_0x001b;
    L_0x0018:
        r2 = r2 + 1;
        goto L_0x0010;
    L_0x001b:
        if (r0 == 0) goto L_0x0022;
    L_0x001d:
        if (r5 == 0) goto L_0x002f;
    L_0x001f:
        r0.close();	 Catch:{ Throwable -> 0x0023 }
    L_0x0022:
        return r3;
    L_0x0023:
        r4 = move-exception;
        r5.addSuppressed(r4);	 Catch:{ IOException -> 0x0028 }
        goto L_0x0022;
    L_0x0028:
        r1 = move-exception;
        r4 = 0;
        r5 = "null";
        r3[r4] = r5;
        goto L_0x0022;
    L_0x002f:
        r0.close();	 Catch:{ IOException -> 0x0028 }
        goto L_0x0022;
    L_0x0033:
        r4 = move-exception;
        throw r4;	 Catch:{ all -> 0x0035 }
    L_0x0035:
        r5 = move-exception;
        r7 = r5;
        r5 = r4;
        r4 = r7;
    L_0x0039:
        if (r0 == 0) goto L_0x0040;
    L_0x003b:
        if (r5 == 0) goto L_0x0046;
    L_0x003d:
        r0.close();	 Catch:{ Throwable -> 0x0041 }
    L_0x0040:
        throw r4;	 Catch:{ IOException -> 0x0028 }
    L_0x0041:
        r6 = move-exception;
        r5.addSuppressed(r6);	 Catch:{ IOException -> 0x0028 }
        goto L_0x0040;
    L_0x0046:
        r0.close();	 Catch:{ IOException -> 0x0028 }
        goto L_0x0040;
    L_0x004a:
        r4 = move-exception;
        goto L_0x0039;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ftunram.secsurf.toolkit.FileRW.read(java.lang.String):java.lang.String[]");
    }

    public boolean write(String input, File file) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
            bw.write(input);
            bw.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
