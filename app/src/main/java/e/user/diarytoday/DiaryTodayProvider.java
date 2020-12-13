package e.user.diarytoday;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import e.user.diarytoday.DiaryTodayDatabaseContract.DiaryInfoEntry;
import e.user.diarytoday.DiaryTodayDatabaseContract.SubjectInfoEntry;
import e.user.diarytoday.DiaryTodayProviderContract.Diaries;
import e.user.diarytoday.DiaryTodayProviderContract.Subjects;
import e.user.diarytoday.DiaryTodayProviderContract.SubjectsIdColumns;

public class DiaryTodayProvider extends ContentProvider {

    private static final String MIME_VENDOR_TYPE = "vnd." + DiaryTodayProviderContract.AUTHORITY + ".";
    private DiaryTodayOpenHelper mDbOpenHelper;

    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int SUBJECTS = 0;
    public static final int DIARIES = 1;
    public static final int DIARIES_EXPANDED = 2;
    public static final int DIARIES_ROW = 3;
    private static final int SUBJECTS_ROW = 4;
    private static final int DIARIES_EXPANDED_ROW = 5;

    static {
        sUriMatcher.addURI(DiaryTodayProviderContract.AUTHORITY, Subjects.PATH, SUBJECTS);
        sUriMatcher.addURI(DiaryTodayProviderContract.AUTHORITY, Diaries.PATH, DIARIES);
        sUriMatcher.addURI(DiaryTodayProviderContract.AUTHORITY, Diaries.PATH_EXPANDED, DIARIES_EXPANDED);
        sUriMatcher.addURI(DiaryTodayProviderContract.AUTHORITY, Diaries.PATH + "/#" , DIARIES_ROW);
        sUriMatcher.addURI(DiaryTodayProviderContract.AUTHORITY, Subjects.PATH + "/#", SUBJECTS_ROW);
        sUriMatcher.addURI(DiaryTodayProviderContract.AUTHORITY, Diaries.PATH_EXPANDED + "/#", DIARIES_EXPANDED_ROW);

    }

    public DiaryTodayProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        long rowId = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;
        int nRows = -1;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        int uriMatch = sUriMatcher.match(uri);
        switch(uriMatch) {
            case SUBJECTS:
                nRows = db.delete(SubjectInfoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DIARIES:
                nRows = db.delete(DiaryInfoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case DIARIES_EXPANDED:
                // throw exception saying that this is a read-only table
            case SUBJECTS_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = SubjectInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.delete(SubjectInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
                break;
            case DIARIES_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = DiaryInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.delete(DiaryInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
                break;
            case DIARIES_EXPANDED_ROW:
                // throw exception saying that this is a read-only table
                break;
        }

        return nRows;
    }

    @Override
    public String getType(Uri uri) {
        String mimeType = null;
        int uriMatch = sUriMatcher.match(uri);
        switch(uriMatch) {
            case SUBJECTS:
                // vnd.android.cursor.dir/vnd.e.user.diary.provider.courses
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        MIME_VENDOR_TYPE + Subjects.PATH;
                break;
            case DIARIES:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Diaries.PATH;
                break;
            case DIARIES_EXPANDED:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Diaries.PATH_EXPANDED;
                break;
            case SUBJECTS_ROW:
                mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Subjects.PATH;
                break;
            case DIARIES_ROW:
                mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Diaries.PATH;
                break;
            case DIARIES_EXPANDED_ROW:
                mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Diaries.PATH_EXPANDED;
                break;
        }
        return mimeType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        long rowId = -1;
        Uri rowUri = null;
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case DIARIES:
                rowId = db.insert(DiaryInfoEntry.TABLE_NAME, null, values);
                // content://e.user.diarytoday.provider/diaries/1
                rowUri = ContentUris.withAppendedId(Diaries.CONTENT_URI, rowId);
                break;
            case SUBJECTS:
                rowId = db.insert(SubjectInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(Subjects.CONTENT_URI, rowId);
                break;
            case DIARIES_EXPANDED:
                // throw exception saying that this is a read-only table
                break;
        }
        return rowUri;
    }

    @Override
    public boolean onCreate() {
        mDbOpenHelper = new DiaryTodayOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        long rowId = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;
        Cursor cursor = null;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case SUBJECTS:
                cursor = db.query(SubjectInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case DIARIES:
                cursor = db.query(DiaryInfoEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case DIARIES_EXPANDED:
                cursor = diariesExpandedQuery(db, projection, selection, selectionArgs,sortOrder);
                break;

            case SUBJECTS_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = SubjectInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                cursor = db.query(SubjectInfoEntry.TABLE_NAME, projection, rowSelection,
                        rowSelectionArgs, null, null, null);
                break;

            case DIARIES_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = DiaryInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                cursor = db.query(DiaryInfoEntry.TABLE_NAME, projection, rowSelection,
                        rowSelectionArgs, null, null, null);

            case DIARIES_EXPANDED_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = DiaryInfoEntry.getQName(DiaryInfoEntry._ID) + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                cursor = diariesExpandedQuery(db, projection, rowSelection, rowSelectionArgs, null);
                break;
        }
       return cursor;
    }

    private Cursor diariesExpandedQuery(SQLiteDatabase db, String[] projection, String selection,
                                        String[] selectionArgs, String sortOrder) {

        String[] columns = new String[projection.length];
        for(int idx=0; idx < projection.length; idx++) {
            columns[idx] = projection[idx].equals(BaseColumns._ID) ||
                    projection[idx].equals(SubjectsIdColumns.COLUMN_SUBJECT_ID) ?
                    DiaryInfoEntry.getQName(projection[idx]) : projection[idx];
        }

        String tablesWithJoin = DiaryInfoEntry.TABLE_NAME + " JOIN " +
                SubjectInfoEntry.TABLE_NAME + " ON " +
                DiaryInfoEntry.getQName(DiaryInfoEntry.COLUMN_SUBJECT_ID) + " = " +
                SubjectInfoEntry.getQName(SubjectInfoEntry.COLUMN_SUBJECT_ID);

        return db.query(tablesWithJoin, columns, selection, selectionArgs,
                null, null, sortOrder);

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        long rowId = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;
        int nRows = -1;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case SUBJECTS:
                nRows = db.update(SubjectInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case DIARIES:
                nRows = db.update(DiaryInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case DIARIES_EXPANDED:
                // throw exception saying that this is a read-only table
            case SUBJECTS_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = SubjectInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.update(SubjectInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs);
                break;
            case DIARIES_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = DiaryInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.update(DiaryInfoEntry.TABLE_NAME, values, rowSelection, rowSelectionArgs);
                break;
            case DIARIES_EXPANDED_ROW:
                // throw exception saying that this is a read-only table
                break;
        }
        return nRows;
    }
}
