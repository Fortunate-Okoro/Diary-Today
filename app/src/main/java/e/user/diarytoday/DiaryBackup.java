package e.user.diarytoday;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import e.user.diarytoday.DiaryTodayProviderContract.Diaries;

public class DiaryBackup {
    public static final String ALL_SUBJECTS = "ALLSUBJECTS";
    private static final String TAG = DiaryBackup.class.getSimpleName();

    public static void doBackup(Context context, String backupSubjectId) {
        String[] columns = {
                Diaries.COLUMN_SUBJECT_ID,
                Diaries.COLUMN_DIARY_TITLE,
                Diaries.COLUMN_DIARY_TEXT,
        };

        String selection = null;
        String[] selectionArgs = null;
        if(!backupSubjectId.equals(ALL_SUBJECTS)) {
            selection = Diaries.COLUMN_SUBJECT_ID + " = ?";
            selectionArgs = new String[] {backupSubjectId};
        }

        Cursor cursor = context.getContentResolver().query(Diaries.CONTENT_URI, columns, selection, selectionArgs, null);
        int subjectIdPos = cursor.getColumnIndex(Diaries.COLUMN_SUBJECT_ID);
        int diaryTitlePos = cursor.getColumnIndex(Diaries.COLUMN_DIARY_TITLE);
        int diaryTextPos = cursor.getColumnIndex(Diaries.COLUMN_DIARY_TEXT);

        Log.i(TAG, ">>>***   BACKUP START - Thread: " + Thread.currentThread().getId() + "   ***<<<");
        while(cursor.moveToNext()) {
            String subjectId = cursor.getString(subjectIdPos);
            String diaryTitle = cursor.getString(diaryTitlePos);
            String diaryText = cursor.getString(diaryTextPos);

            if(!diaryTitle.equals("")) {
                Log.i(TAG, ">>>Backing Up Diary<<< " + subjectId + "|" + diaryTitle + "|" + diaryText);
                simulateLongRunningWork();
            }
        }
        Log.i(TAG, ">>>***   BACKUP COMPLETE   ***<<<");
        cursor.close();
    }


    private static void simulateLongRunningWork() {
        try {
            Thread.sleep(1000);
        } catch(Exception ex) {}
    }

}
