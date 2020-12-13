package e.user.diarytoday;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DiaryTodayProviderContract {
    private DiaryTodayProviderContract(){}
    public static final String AUTHORITY = "e.user.diarytoday.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    protected interface SubjectsIdColumns{
        public static final String COLUMN_SUBJECT_ID = "subject_id";
    }

    protected interface SubjectsColumns{
        public static final String COLUMN_SUBJECT_TITLE = "subject_title";
    }

    protected interface DiariesColumns {
        public static final String COLUMN_DIARY_TITLE = "diary_title";
        public static final String COLUMN_DIARY_TEXT = "diary_text";
    }

    public static final class Subjects implements BaseColumns, SubjectsColumns, SubjectsIdColumns{
        public static final String PATH = "subjects";
        //content://e.user.diarytoday.provider/subjects
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Diaries implements BaseColumns, DiariesColumns, SubjectsIdColumns, SubjectsColumns{
        public static final String PATH = "diaries";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
        public static final String PATH_EXPANDED = "diaries_expanded";
        public static final Uri CONTENT_EXPANDED_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH_EXPANDED);
    }
}
