package e.user.diarytoday;

import android.provider.BaseColumns;

public final class DiaryTodayDatabaseContract {
    private DiaryTodayDatabaseContract() {} // make non-creatable

    public static final class SubjectInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "subject_info";
        public static final String COLUMN_SUBJECT_ID = "subject_id";
        public static final String COLUMN_SUBJECT_TITLE = "subject_title";

        // CREATE INDEX subject_info_index1 ON subject_info (subject_title)
        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                        "(" + COLUMN_SUBJECT_TITLE + ")";

        public static final String getQName(String columnName) {
            return TABLE_NAME + "." + columnName;
        }

        // CREATE TABLE subject_info (subject_id, subject_title)
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_SUBJECT_ID + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_SUBJECT_TITLE + " TEXT NOT NULL)";
    }

    public static final class DiaryInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "diary_info";
        public static final String COLUMN_DIARY_TITLE = "diary_title";
        public static final String COLUMN_DIARY_TEXT = "diary_text";
        public static final String COLUMN_SUBJECT_ID = "subject_id";

        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME +
                        "(" + COLUMN_DIARY_TITLE + ")";


        public static final String getQName(String columnName) {
            return TABLE_NAME + "." + columnName;
        }

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_DIARY_TITLE + " TEXT NOT NULL, " +
                        COLUMN_DIARY_TEXT + " TEXT, " +
                        COLUMN_SUBJECT_ID + " TEXT NOT NULL)";
    }



}
