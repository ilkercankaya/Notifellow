package com.notifellow.su.notifellow;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;

import static com.notifellow.su.notifellow.NotePathUtils.isDownloadsDocument;
import static com.notifellow.su.notifellow.NotePathUtils.isExternalStorageDocument;

public class GalleryActivity extends AppCompatActivity {

    final int REQUEST_CODE_GALLERY = 999;

    String path;
    static NotesDBSchema schema;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(GalleryActivity.this,

                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_GALLERY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY
                && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            path = getRealPathFromURI_API19(this, uri);
            NoteAdapter.path = path;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
            NoteAdapter.imageView.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(),
                        "You don't have perms to access file location",
                        Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";

        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else {

                if (Build.VERSION.SDK_INT > 20) {
                    //getExternalMediaDirs() added in API 21
                    File extenal[] = context.getExternalMediaDirs();
                    if (extenal.length > 1) {
                        filePath = extenal[1].getAbsolutePath();
                        filePath = filePath.substring(0,
                                filePath.indexOf("Android")) + split[1];
                    }
                } else {
                    filePath = "/storage/" + type + "/" + split[1];
                }
                return filePath;
            }

        } else if (isDownloadsDocument(uri)) {
            // DownloadsProvider
            final String id = DocumentsContract.getDocumentId(uri);
            //final Uri contentUri = ContentUris.withAppendedId(
            // Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {column};

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    String result = cursor.getString(index);
                    cursor.close();
                    return result;
                }
            } finally {
                if (cursor != null) cursor.close();
            }
        } else if (DocumentsContract.isDocumentUri(context, uri)) {
            // MediaProvider
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String[] ids = wholeID.split(":");
            String id;
            String type;
            if (ids.length > 1) {
                id = ids[1];
                type = ids[0];
            } else {
                id = ids[0];
                type = ids[0];
            }

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{id};
            final String column = "_data";
            final String[] projection = {column};
            Cursor cursor = context.getContentResolver()
                    .query(contentUri, projection,
                            selection, selectionArgs, null);

            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(column);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
            return filePath;
        } else {
            String[] proj = {MediaStore.Audio.Media.DATA};
            Cursor cursor = context
                    .getContentResolver()
                    .query(uri, proj, null,
                            null, null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                if (cursor.moveToFirst()) filePath = cursor.getString(column_index);
                cursor.close();
            }


            return filePath;
        }
        return null;
    }

}
