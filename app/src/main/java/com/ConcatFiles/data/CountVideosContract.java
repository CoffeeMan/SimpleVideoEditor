package com.ConcatFiles.data;

import android.provider.BaseColumns;

public final class CountVideosContract {
    CountVideosContract(){
    }

    public final class CountVideosEntry implements BaseColumns {
        public final static String TABLE_NAME = "count_videos";

        public final static String COUNT_MERGE = "count_merge";
        public final static String COUNT_TRIM = "count_trim";

    }
}
