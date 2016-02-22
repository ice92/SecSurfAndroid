package com.ftunram.secsurf.toolkit;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video;

public class ASFUriHelper {
    public static String getPath(Context context, Uri uri) {
        boolean isKitKat;
        if (VERSION.SDK_INT >= 19) {
            isKitKat = true;
        } else {
            isKitKat = false;
        }
        if (VERSION.SDK_INT >= 19) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                String[] split;
                if (isExternalStorageDocument(uri)) {
                    split = DocumentsContract.getDocumentId(uri).split(":");
                    if ("primary".equalsIgnoreCase(split[0])) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {
                    return getDataColumn(context, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(uri)).longValue()), null, null);
                } else if (isMediaDocument(uri)) {
                    String type = DocumentsContract.getDocumentId(uri).split(":")[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = "_id=?";
                    return getDataColumn(context, contentUri, "_id=?", new String[]{split[1]});
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            } else {
                if ("file".equalsIgnoreCase(uri.getScheme())) {
                    return uri.getPath();
                }
            }
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        try {
            cursor = context.getContentResolver().query(uri, new String[]{"_data"}, selection, selectionArgs, null);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            }
            String string = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
            if (cursor == null) {
                return string;
            }
            cursor.close();
            return string;
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
