package com.ConcatFiles.data;

import android.provider.BaseColumns;

public final class MergedVideosContract {
    MergedVideosContract(){
    }

    public static final class mergedVideosEntry implements BaseColumns {
        public final static String TABLE_NAME = "merged_video";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_OWN_PATH = "own_path";
        public final static String COLUMN_BASE_PATH_FIRST = "base_path_first";
        public final static String COLUMN_BASE_PATH_SECOND = "base_path_second";
        public final static String COLUMN_DURATION = "duration";
    }
}
