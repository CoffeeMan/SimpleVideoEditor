package com.ConcatFiles.data;

import android.provider.BaseColumns;

public final class CroppedVideoContract {
    CroppedVideoContract(){
    }

    public static final class CropperdVideosEntry implements BaseColumns{
        public final static String TABLE_NAME = "cropped_video";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_OWN_PATH = "own_path";
        public final static String COLUMN_BASE_PATH = "base_path";
        public final static String COLUMN_DURATION = "duration";
    }
}
