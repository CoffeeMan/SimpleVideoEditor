package com.ConcatFiles.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VideoDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = VideoDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "videos.db";
    private static final int DATABASE_VERSION = 1;

    public VideoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_CROPPED_VIDEOS_TABLE = "CREATE TABLE " + CroppedVideoContract.CropperdVideosEntry.TABLE_NAME + " ("
                + CroppedVideoContract.CropperdVideosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CroppedVideoContract.CropperdVideosEntry.COLUMN_OWN_PATH + " TEXT NOT NULL, "
                + CroppedVideoContract.CropperdVideosEntry.COLUMN_BASE_PATH + " TEXT NOT NULL, "
                + CroppedVideoContract.CropperdVideosEntry.COLUMN_DURATION + " INTEGER NOT NULL DEFAULT 0);";
        String SQL_CREATE_MERGED_VIDEOS_TABLE = "CREATE TABLE " + MergedVideosContract.mergedVideosEntry.TABLE_NAME + " ("
                + MergedVideosContract.mergedVideosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MergedVideosContract.mergedVideosEntry.COLUMN_OWN_PATH + " TEXT NOT NULL, "
                + MergedVideosContract.mergedVideosEntry.COLUMN_BASE_PATH_FIRST + " TEXT NOT NULL, "
                + MergedVideosContract.mergedVideosEntry.COLUMN_BASE_PATH_SECOND + " TEXT NOT NULL, "
                + MergedVideosContract.mergedVideosEntry.COLUMN_DURATION + " INTEGER NOT NULL DEFAULT 0);";
        String SQL_CREATE_COUNT_VIDEOS_TABLE = "CREATE TABLE " + CountVideosContract.CountVideosEntry.TABLE_NAME + " ("
                + CountVideosContract.CountVideosEntry.COUNT_TRIM + " INTEGER NOT NULL DEFAULT 0, "
                + CountVideosContract.CountVideosEntry.COUNT_MERGE + " INTEGER NOT NULL DEFAULT 0);";
        String SQL_START_DATA = "INSERT INTO " + CountVideosContract.CountVideosEntry.TABLE_NAME + " ("
                + CountVideosContract.CountVideosEntry.COUNT_TRIM + ", " + CountVideosContract.CountVideosEntry.COUNT_MERGE + ") VALUES (0, 0)";


        db.execSQL(SQL_CREATE_CROPPED_VIDEOS_TABLE);
        db.execSQL(SQL_CREATE_MERGED_VIDEOS_TABLE);
        db.execSQL(SQL_CREATE_COUNT_VIDEOS_TABLE);
        db.execSQL(SQL_START_DATA);
        String SQL_CREATE_TRIGGER_TRIM_INSERT = "CREATE TRIGGER trigger1 AFTER INSERT ON " + CroppedVideoContract.CropperdVideosEntry.TABLE_NAME
                + " BEGIN UPDATE " + CountVideosContract.CountVideosEntry.TABLE_NAME + " SET " + CountVideosContract.CountVideosEntry.COUNT_TRIM
                + " = " + CountVideosContract.CountVideosEntry.COUNT_TRIM + " + 1; end";
        String SQL_CREATE_TRIGGER_MERGE_INSERT = "CREATE TRIGGER trigger2 AFTER INSERT ON " + MergedVideosContract.mergedVideosEntry.TABLE_NAME
                + " BEGIN UPDATE " + CountVideosContract.CountVideosEntry.TABLE_NAME + " SET " + CountVideosContract.CountVideosEntry.COUNT_MERGE
                + " = " + CountVideosContract.CountVideosEntry.COUNT_MERGE + " + 1; end";
        String SQL_CREATE_TRIGGER_TRIM_DELETE = "CREATE TRIGGER trigger3 AFTER DELETE ON " + CroppedVideoContract.CropperdVideosEntry.TABLE_NAME
                + " BEGIN UPDATE " + CountVideosContract.CountVideosEntry.TABLE_NAME + " SET " + CountVideosContract.CountVideosEntry.COUNT_TRIM
                + " = " + CountVideosContract.CountVideosEntry.COUNT_TRIM + " - 1; end";
        String SQL_CREATE_TRIGGER_MERGE_DELETE = "CREATE TRIGGER trigger4 AFTER DELETE ON " + MergedVideosContract.mergedVideosEntry.TABLE_NAME
                + " BEGIN UPDATE " + CountVideosContract.CountVideosEntry.TABLE_NAME + " SET " + CountVideosContract.CountVideosEntry.COUNT_MERGE
                + " = " + CountVideosContract.CountVideosEntry.COUNT_MERGE + " - 1; end";
        db.execSQL(SQL_CREATE_TRIGGER_TRIM_INSERT);
        db.execSQL(SQL_CREATE_TRIGGER_MERGE_INSERT);
        db.execSQL(SQL_CREATE_TRIGGER_TRIM_DELETE);
        db.execSQL(SQL_CREATE_TRIGGER_MERGE_DELETE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
