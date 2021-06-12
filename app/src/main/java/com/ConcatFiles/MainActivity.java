package com.ConcatFiles;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.ConcatFiles.data.CroppedVideoContract;
import com.ConcatFiles.data.VideoDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    Uri selectedUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
    }


    public void buttonCutListener(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("video/*");
        startActivityForResult(i, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            selectedUri = data.getData();

            Intent i = new Intent(MainActivity.this, TrimActivity.class);
            i.putExtra("uri", selectedUri.toString());

            startActivity(i);
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                100);
    }

    public void buttonMergeListener(View view) {
        Intent i = new Intent(MainActivity.this, MergeVideosActivity.class);
        startActivity(i);
    }

    public void buttonAllVideosListener(View view) {
        Intent i = new Intent(MainActivity.this, AllVideosActivity.class);
        startActivity(i);
    }

}