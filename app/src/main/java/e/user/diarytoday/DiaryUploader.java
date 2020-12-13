package e.user.diarytoday;

import android.app.job.JobParameters;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import e.user.diarytoday.DiaryTodayProviderContract.Diaries;

public class DiaryUploader {
    private final String TAG = getClass().getSimpleName();

    private final Context mContext;
    private boolean mCanceled;

    public DiaryUploader(Context context) {
        mContext = context;
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public void cancel() {
        mCanceled = true;
    }

    public void doUpload(Uri dataUri) {
        String[] columns = {
                Diaries.COLUMN_SUBJECT_ID,
                Diaries.COLUMN_DIARY_TITLE,
                Diaries.COLUMN_DIARY_TEXT,
        };

        Cursor cursor = mContext.getContentResolver().query(dataUri, columns, null, null, null);
        int subjectIdPos = cursor.getColumnIndex(Diaries.COLUMN_SUBJECT_ID);
        int diaryTitlePos = cursor.getColumnIndex(Diaries.COLUMN_DIARY_TITLE);
        int diaryTextPos = cursor.getColumnIndex(Diaries.COLUMN_DIARY_TEXT);

        Log.i(TAG, ">>>*** UPLOAD START - " + dataUri + " ***<<<");
        mCanceled = false;
        while(!mCanceled && cursor.moveToNext()) {
            String subjectId = cursor.getString(subjectIdPos);
            String diaryTitle = cursor.getString(diaryTitlePos);
            String diaryText = cursor.getString(diaryTextPos);

            if(!diaryTitle.equals("")) {
                Log.i(TAG, ">>>Uploading Diary<<< " + subjectId + "|" + diaryTitle + "|" + diaryText);
                simulateLongRunningWork();
            }
        }
        if(mCanceled)
            Log.i(TAG, ">>>*** UPLOAD !!CANCELED!! - " + dataUri + " ***<<<");
        else
            Log.i(TAG, ">>>*** UPLOAD COMPLETE - " + dataUri + " ***<<<");
        cursor.close();
    }

    private static void simulateLongRunningWork() {
        try {
            Thread.sleep(3000);
        } catch(Exception ex) {}
    }
}
