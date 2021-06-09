package com.ConcatFiles;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.ConcatFiles.data.CroppedVideoContract;
import com.ConcatFiles.data.MergedVideosContract;
import com.ConcatFiles.data.VideoDbHelper;

public class AllVideosActivity extends AppCompatActivity {

    VideoDbHelper vDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_videos);


    }

    @Override
    protected void onStart() {
        super.onStart();
        vDbHelper = new VideoDbHelper(this);
        getDbData();
    }

    private void getDbData() {
        SQLiteDatabase db = vDbHelper.getReadableDatabase();
        String[] projectionMerge = {
                MergedVideosContract.mergedVideosEntry._ID,
                MergedVideosContract.mergedVideosEntry.COLUMN_OWN_PATH,
                MergedVideosContract.mergedVideosEntry.COLUMN_BASE_PATH_FIRST,
                MergedVideosContract.mergedVideosEntry.COLUMN_BASE_PATH_SECOND,
                MergedVideosContract.mergedVideosEntry.COLUMN_DURATION
        };

        String[] projectionTrim = {
                CroppedVideoContract.CropperdVideosEntry._ID,
                CroppedVideoContract.CropperdVideosEntry.COLUMN_OWN_PATH,
                CroppedVideoContract.CropperdVideosEntry.COLUMN_BASE_PATH,
                CroppedVideoContract.CropperdVideosEntry.COLUMN_DURATION
        };
    }


}