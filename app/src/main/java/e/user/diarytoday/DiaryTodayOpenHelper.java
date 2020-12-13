package e.user.diarytoday;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import e.user.diarytoday.DiaryTodayDatabaseContract.DiaryInfoEntry;
import e.user.diarytoday.DiaryTodayDatabaseContract.SubjectInfoEntry;

public class DiaryTodayOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DiaryToday.db";
    public static final int DATABASE_VERSION = 2;
    public DiaryTodayOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SubjectInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(DiaryInfoEntry.SQL_CREATE_TABLE);
        db.execSQL(SubjectInfoEntry.SQL_CREATE_INDEX1);
        db.execSQL(DiaryInfoEntry.SQL_CREATE_INDEX1);

        DatabaseDataWorker worker = new DatabaseDataWorker(db);
        worker.insertSubjects();
        worker.insertSampleDiaries();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2){
            db.execSQL(SubjectInfoEntry.SQL_CREATE_INDEX1);
            db.execSQL(DiaryInfoEntry.SQL_CREATE_INDEX1);
        }

    }
}
