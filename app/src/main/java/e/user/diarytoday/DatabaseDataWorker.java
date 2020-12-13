package e.user.diarytoday;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseDataWorker {
    private SQLiteDatabase mDb;

    public DatabaseDataWorker(SQLiteDatabase db) {
        mDb = db;
    }

    public void insertSubjects() {
        insertSubject("sport_life", "Sport");
        insertSubject("academic_class", "Academics");
        insertSubject("work_today", "Work");
        insertSubject("politics_today", "Politics");
        insertSubject("religious_belief", "Religion");
        insertSubject("miscellaneous_wow", "Others");
    }

    public void insertSampleDiaries() {
        insertDiary("sport_life", "My sport life", "Wish I can get involve in one of the known sports in life");
        insertDiary("work_today", "Transportation", "It was very difficult getting vehicle, today");

        insertDiary("sport_life","Football" , "A game of luck, but i love you though");
        insertDiary("politics_nigeria", "Nigeria", "Its only connection that will make you survive here");

        insertDiary("politics_nigeria", "Africa", "wonderful continent, nice culture but being progressive, they hate it");
        insertDiary("academic_class", "Engineering, science and technology", "I am an Electrical and Electronic Engineer");

        insertDiary("work_today", "Boss", "She is a nice, friendly, awesome and a wonderful personality");
        insertDiary("academic_class", "Programming: Android development", "I love programming,for it gives me joy");

        insertDiary("sport_life", "Volleyball", "I will love to play this one day");
        insertDiary("work_today", "Colleagues", "They are wonderful, but like aproko");
        insertDiary("politics_nigeria", "World", "Its a norm to meet a renown personality, just work hard");

        insertDiary("religious_belief", "Anglican, Gwagwa", "uhmm, that's all I can say");
        insertDiary("miscellaneous_wow", "Something", "Something, i love doing most");

        insertDiary("religious_belief", "ACOL, FUTO", "A family, i was part of and miss so dearly");
        insertDiary("miscellaneous_wow", "My game life", "I love playing games");

        insertDiary("religious_belief", "ACFM, Maitaima", "Wonderful fellowship, but they can do guy for Africa eh!");
        insertDiary("miscellaneous_wow", "My social life", "Its so boring, dont come closer");
    }

    private void insertSubject(String subjectId, String title) {
        ContentValues values = new ContentValues();
        values.put(DiaryTodayDatabaseContract.SubjectInfoEntry.COLUMN_SUBJECT_ID, subjectId);
        values.put(DiaryTodayDatabaseContract.SubjectInfoEntry.COLUMN_SUBJECT_TITLE, title);

        long newRowId = mDb.insert(DiaryTodayDatabaseContract.SubjectInfoEntry.TABLE_NAME, null, values);
    }

    private void insertDiary(String subjectId, String title, String text) {
        ContentValues values = new ContentValues();
        values.put(DiaryTodayDatabaseContract.DiaryInfoEntry.COLUMN_SUBJECT_ID, subjectId);
        values.put(DiaryTodayDatabaseContract.DiaryInfoEntry.COLUMN_DIARY_TITLE, title);
        values.put(DiaryTodayDatabaseContract.DiaryInfoEntry.COLUMN_DIARY_TEXT, text);

        long newRowId = mDb.insert(DiaryTodayDatabaseContract.DiaryInfoEntry.TABLE_NAME, null, values);
    }

}