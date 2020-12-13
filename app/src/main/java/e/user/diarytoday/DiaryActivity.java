package e.user.diarytoday;

import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.List;

import e.user.diarytoday.DiaryTodayDatabaseContract.DiaryInfoEntry;
import e.user.diarytoday.DiaryTodayDatabaseContract.SubjectInfoEntry;
import e.user.diarytoday.DiaryTodayProviderContract.Diaries;
import e.user.diarytoday.DiaryTodayProviderContract.Subjects;

public class DiaryActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_DIARIES = 0;
    public static final int LOADER_SUBJECTS = 1;
    private final String TAG = getClass().getSimpleName();
    public static final String DIARY_URI = "e.user.diarytoday.NOTE_URI";
    public static final String DIARY_ID = "e.user.diarytoday.DIARY_ID";
    public static final String ORIGINAL_DIARY_SUBJECT_ID = "e.user.diarytoday.ORIGINAL_DIARY_SUBJECT_ID";
    public static final String ORIGINAL_DIARY_TITLE = "e.user.diarytoday.ORIGINAL_DIARY_TITLE";
    public static final String ORIGINAL_DIARY_TEXT = "e.user.diarytoday.ORIGINAL_DIARY_TEXT";
    public static final int ID_NOT_SET = -1;
    private DiaryInfo mDiary = new DiaryInfo(DataManager.getInstance().getSubjects().get(0), "", "");
    private boolean mIsNewDiary;
    private Spinner mSpinnerSubjects;
    private EditText mTextDiaryTitle;
    private EditText mTextDiaryText;
    private int mDiaryId;
    private boolean mIsCancelling;
    private String mOriginalDiarySubjectId;
    private String mOriginalDiaryTitle;
    private String mOriginalDiaryText;
    private DiaryTodayOpenHelper mDbOpenHelper;
    private Cursor mDiaryCursor;
    private int mSubjectIdPos;
    private int mDiaryTitlePos;
    private int mDiaryTextPos;
    private SimpleCursorAdapter mAdapterSubjects;
    private boolean mSubjectsQueryFinished;
    private boolean mDiariesQueryFinished;
    private Uri mDiaryUri;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotifyManager;
    private ModuleStatusView mViewModuleStatus;

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new DiaryTodayOpenHelper(this);
        mSpinnerSubjects = (Spinner) findViewById(R.id.spinner_subjects);
        mAdapterSubjects = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[] {SubjectInfoEntry.COLUMN_SUBJECT_TITLE},
                new int[] {android.R.id.text1}, 0);
        mAdapterSubjects.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerSubjects.setAdapter(mAdapterSubjects);
        getLoaderManager().initLoader(LOADER_SUBJECTS, null, this);

        readDisplayStateValues();
        if (savedInstanceState == null) {
            saveOriginalDiaryValues();
        } else {
            restoreOriginalDiaryValues(savedInstanceState);
            String stringDiaryUri = savedInstanceState.getString(DIARY_URI);
            mDiaryUri = Uri.parse(stringDiaryUri);
        }

        mTextDiaryTitle = (EditText) findViewById(R.id.text_diary_title);
        mTextDiaryText = (EditText) findViewById(R.id.text_diary_text);

        if(!mIsNewDiary)
            getLoaderManager().initLoader(LOADER_DIARIES, null, this);

        mViewModuleStatus = (ModuleStatusView) findViewById(R.id.module_status);
        loadModuleStatusValues();
    }

    private void loadModuleStatusValues() {
        int totalNumberOfModules = 11;
        int completedNumberOfModules = 7;
        boolean[] moduleStatus = new boolean[totalNumberOfModules];
        for (int moduleIndex = 0; moduleIndex < completedNumberOfModules; moduleIndex++)
            moduleStatus[moduleIndex] = true;
        mViewModuleStatus.setModuleStatus(moduleStatus);
    }

    private void loadSubjectData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        String[] subjectColumns = {
                SubjectInfoEntry.COLUMN_SUBJECT_TITLE,
                SubjectInfoEntry.COLUMN_SUBJECT_ID,
                SubjectInfoEntry._ID
        };
        Cursor cursor = db.query(SubjectInfoEntry.TABLE_NAME, subjectColumns,
                null, null, null, null, SubjectInfoEntry.COLUMN_SUBJECT_TITLE);
        mAdapterSubjects.changeCursor(cursor);
    }

    private void loadDiaryData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        String selection = DiaryInfoEntry._ID + "= ?";
        String[] selectionArgs = {Integer.toString(mDiaryId)};

        String[] diaryColumns = {
                DiaryInfoEntry.COLUMN_SUBJECT_ID,
                DiaryInfoEntry.COLUMN_DIARY_TITLE,
                DiaryInfoEntry.COLUMN_DIARY_TEXT
        };
        mDiaryCursor = db.query(DiaryInfoEntry.TABLE_NAME, diaryColumns,
                selection, selectionArgs, null, null, null);
        mSubjectIdPos = mDiaryCursor.getColumnIndex(DiaryInfoEntry.COLUMN_SUBJECT_ID);
        mDiaryTitlePos = mDiaryCursor.getColumnIndex(DiaryInfoEntry.COLUMN_DIARY_TITLE);
        mDiaryTextPos = mDiaryCursor.getColumnIndex(DiaryInfoEntry.COLUMN_DIARY_TEXT);
        mDiaryCursor.moveToNext();
        displayDiary();
    }


    private void restoreOriginalDiaryValues(Bundle savedInstanceState) {
        mOriginalDiarySubjectId = savedInstanceState.getString(ORIGINAL_DIARY_SUBJECT_ID);
        mOriginalDiaryTitle = savedInstanceState.getString(ORIGINAL_DIARY_TITLE);
        mOriginalDiaryText = savedInstanceState.getString(ORIGINAL_DIARY_TEXT);
    }

    private void saveOriginalDiaryValues() {
        if(mIsNewDiary)
            return;
        mOriginalDiarySubjectId = mDiary.getSubject().getSubjectId();
        mOriginalDiaryTitle = mDiary.getTitle();
        mOriginalDiaryText = mDiary.getText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling){
//            Log.i(TAG, "Cancelling diary at position: " + mDiaryId);
            if(mIsNewDiary) {
                deleteDiaryFromDatabase();
            } else {
                storePreviousDiaryValues();
            }
        }else {
            saveDiary();
        }
        Log.d(TAG, "onPause");
    }

    private void deleteDiaryFromDatabase() {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                getContentResolver().delete(mDiaryUri, null, null);
                return null;
            }
        };
        task.execute();
    }

    private void storePreviousDiaryValues() {
        SubjectInfo subject = DataManager.getInstance().getSubject(mOriginalDiarySubjectId);
        mDiary.setSubject(subject);
        mDiary.setTitle(mOriginalDiaryTitle);
        mDiary.setText(mOriginalDiaryText);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_DIARY_SUBJECT_ID, mOriginalDiarySubjectId);
        outState.putString(ORIGINAL_DIARY_TITLE, mOriginalDiaryTitle);
        outState.putString(ORIGINAL_DIARY_TEXT, mOriginalDiaryText);

        outState.putString(DIARY_URI, mDiaryUri.toString());
    }

    private void saveDiary() {
        String subjectId = selectedSubjectId();
        String diaryTitle = mTextDiaryTitle.getText().toString();
        String diaryText = mTextDiaryText.getText().toString();
        saveDiaryToDatabase(subjectId, diaryTitle, diaryText);
    }

    private String selectedSubjectId() {
        int selectedPosition = mSpinnerSubjects.getSelectedItemPosition();
        Cursor cursor = mAdapterSubjects.getCursor();
        cursor.moveToPosition(selectedPosition);
        int subjectIdPos = cursor.getColumnIndex(SubjectInfoEntry.COLUMN_SUBJECT_ID);
        String subjectId = cursor.getString(subjectIdPos);
        return subjectId;
    }

    private void saveDiaryToDatabase(String subjectId, String diaryTitle, String diaryText) {
        ContentValues values = new ContentValues();
        values.put(Diaries.COLUMN_SUBJECT_ID, subjectId);
        values.put(Diaries.COLUMN_DIARY_TITLE, diaryTitle);
        values.put(Diaries.COLUMN_DIARY_TEXT, diaryText);

        getContentResolver().update(mDiaryUri, values, null, null);
     }

    private void displayDiary() {
        String subjectId = mDiaryCursor.getString(mSubjectIdPos);
        String diaryTitle = mDiaryCursor.getString(mDiaryTitlePos);
        String diaryText = mDiaryCursor.getString(mDiaryTextPos);

        int subjectIndex = getIndexOfSubjectId(subjectId);

        mSpinnerSubjects.setSelection(subjectIndex);
        mTextDiaryTitle.setText(diaryTitle);
        mTextDiaryText.setText(diaryText);

        SubjectEventBroadcastHelper.sendEventBroadcast(this, subjectId, "Editing Diary");
    }

    private int getIndexOfSubjectId(String subjectId) {
        Cursor cursor = mAdapterSubjects.getCursor();
        int subjectIdPos = cursor.getColumnIndex(SubjectInfoEntry.COLUMN_SUBJECT_ID);
        int subjectRowIndex = 0;

        boolean more = cursor.moveToFirst();
        while (more) {
            String cursorSubjectId = cursor.getString(subjectIdPos);
            if (subjectId.equals(cursorSubjectId))
                break;

            subjectRowIndex++;
            more = cursor.moveToNext();
        }
        return subjectRowIndex;
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mDiaryId = intent.getIntExtra(DIARY_ID, ID_NOT_SET);
        mIsNewDiary = mDiaryId == ID_NOT_SET;
        if(mIsNewDiary){
            createNewDiary();
        }
//        Log.i(TAG, "mDiaryId: " + mDiaryId);
//        mDiary = DataManager.getInstance().getDiaries().get(mDiaryId);
    }

    private void createNewDiary() {
        AsyncTask<ContentValues, Integer, Uri> task = new AsyncTask<ContentValues, Integer, Uri>() {
            private ProgressBar mProgressBar;

            @Override
            protected void onPreExecute() {
                mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(1);
            }

            @Override
            protected Uri doInBackground(ContentValues... params) {
                Log.d(TAG, "doInBackground - thread: " + Thread.currentThread().getId());
                ContentValues insertValues = params[0];
                Uri rowUri = getContentResolver().insert(Diaries.CONTENT_URI, insertValues);
                simulateLongRunningWork();
                publishProgress(2);

                simulateLongRunningWork();
                publishProgress(3);
                return rowUri;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                int progressValue = values[0];
                mProgressBar.setProgress(progressValue);
            }

            @Override
            protected void onPostExecute(Uri uri) {
                Log.d(TAG, "call to execute - thread: " + Thread.currentThread().getId());
                mDiaryUri = uri;
                displaySnackbar(mDiaryUri.toString());
                mProgressBar.setVisibility(View.GONE);
            }
        };

        ContentValues values = new ContentValues();
        values.put(Diaries.COLUMN_SUBJECT_ID, "");
        values.put(Diaries.COLUMN_DIARY_TITLE, "");
        values.put(Diaries.COLUMN_DIARY_TEXT, "");

        Log.d(TAG, "call to execute - thread: " + Thread.currentThread().getId());
        task.execute(values);
    }

    private void displaySnackbar(String message) {
        View view = findViewById(R.id.spinner_subjects);
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    private void simulateLongRunningWork() {
        try {
            Thread.sleep(2000);
        } catch(Exception ex) {}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_diary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel){
            mIsCancelling = true;
            finish();
        } else if (id == R.id.action_next){
            moveNext();
        } else if (id == R.id.action_set_reminder){
            showReminderNotification();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showReminderNotification() {
        String diaryTitle = mTextDiaryTitle.getText().toString();
        String diaryText = mTextDiaryText.getText().toString();
        int diaryId = (int)ContentUris.parseId(mDiaryUri);

        Intent intent = new Intent(this, DiaryReminderReceiver.class);
        intent.putExtra(DiaryReminderReceiver.EXTRA_DIARY_TITLE, diaryTitle);
        intent.putExtra(DiaryReminderReceiver.EXTRA_DIARY_TEXT, diaryText);
        intent.putExtra(DiaryReminderReceiver.EXTRA_DIARY_ID, diaryId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long currentTimeInMilliseconds = SystemClock.elapsedRealtime();
        long ONE_HOUR = 60 * 60 * 1000;
        long TEN_SECONDS = 10 * 1000;
        long alarmTime = currentTimeInMilliseconds + TEN_SECONDS;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME, alarmTime, pendingIntent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastDiaryIndex = DataManager.getInstance().getDiaries().size() -1;
        item.setEnabled(mDiaryId < lastDiaryIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveDiary();

        ++mDiaryId;
        mDiary = DataManager.getInstance().getDiaries().get(mDiaryId);

        saveOriginalDiaryValues();
        displayDiary();
        invalidateOptionsMenu();
    }

    private void sendEmail() {
        SubjectInfo subject = (SubjectInfo) mSpinnerSubjects.getSelectedItem();
        String course = mTextDiaryTitle.getText().toString();
        String text = "Look what i have here for today\"" +
                subject.getTitle() + "\"\n" + mTextDiaryText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, course);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg) {
        CursorLoader loader = null;
        if (id == LOADER_DIARIES)
            loader = createLoaderDiaries();
        else if (id == LOADER_SUBJECTS)
            loader = createLoaderSubjects();
        return loader;
    }

    private CursorLoader createLoaderSubjects() {
        mSubjectsQueryFinished = false;
        Uri uri = Subjects.CONTENT_URI;
        String[] subjectColumns = {
                Subjects.COLUMN_SUBJECT_TITLE,
                Subjects.COLUMN_SUBJECT_ID,
                Subjects._ID
        };
        return new CursorLoader(this, uri, subjectColumns, null, null, Subjects.COLUMN_SUBJECT_TITLE);
    }

    private CursorLoader createLoaderDiaries() {
        mDiariesQueryFinished = false;
        String[] diaryColumns = {
                Diaries.COLUMN_SUBJECT_ID,
                Diaries.COLUMN_DIARY_TITLE,
                Diaries.COLUMN_DIARY_TEXT
        };
        mDiaryUri = ContentUris.withAppendedId(Diaries.CONTENT_URI, mDiaryId);
        return new CursorLoader(this, mDiaryUri, diaryColumns, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       if (loader.getId() == LOADER_DIARIES)
           loadFinishedDiaries(data);
       else if (loader.getId() == LOADER_SUBJECTS) {
           mAdapterSubjects.changeCursor(data);
           mSubjectsQueryFinished = true;
           displayDiaryWhenQueriesFinished();
       }
    }

    private void loadFinishedDiaries(Cursor data) {
        mDiaryCursor = data;
        mSubjectIdPos = mDiaryCursor.getColumnIndex(DiaryInfoEntry.COLUMN_SUBJECT_ID);
        mDiaryTitlePos = mDiaryCursor.getColumnIndex(DiaryInfoEntry.COLUMN_DIARY_TITLE);
        mDiaryTextPos = mDiaryCursor.getColumnIndex(DiaryInfoEntry.COLUMN_DIARY_TEXT);

        mDiaryCursor.moveToFirst();

        mDiariesQueryFinished = true;
        displayDiaryWhenQueriesFinished();

    }

    private void displayDiaryWhenQueriesFinished() {
        if (mDiariesQueryFinished && mSubjectsQueryFinished)
            displayDiary();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_DIARIES){
            if (mDiaryCursor != null)
                mDiaryCursor.close();
        }else if (loader.getId() == LOADER_SUBJECTS){
            mAdapterSubjects.changeCursor(null);
        }
    }
}
