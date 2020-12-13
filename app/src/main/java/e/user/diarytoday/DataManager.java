package e.user.diarytoday;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

import e.user.diarytoday.DiaryTodayDatabaseContract.DiaryInfoEntry;
import e.user.diarytoday.DiaryTodayDatabaseContract.SubjectInfoEntry;

public class DataManager {
    private static DataManager ourInstance = null;

    private List<SubjectInfo> mSubjects = new ArrayList<>();
    private List<DiaryInfo> mDiaries = new ArrayList<>();

    public static DataManager getInstance() {
        if(ourInstance == null) {
            ourInstance = new DataManager();
//            ourInstance.initializeSubjects();
//            ourInstance.initializeExampleDiaries();
        }
        return ourInstance;
    }
    public static void loadFromDatabase(DiaryTodayOpenHelper dbHelper){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final String[] subjectColumns = {
                SubjectInfoEntry.COLUMN_SUBJECT_ID,
                SubjectInfoEntry.COLUMN_SUBJECT_TITLE};
        final Cursor subjectCursor = db.query(SubjectInfoEntry.TABLE_NAME, subjectColumns,
                null, null, null, null, SubjectInfoEntry.COLUMN_SUBJECT_TITLE + " DESC" );
        loadSubjectsFromDatabase(subjectCursor);

        final String[] diaryColumns = {
                DiaryInfoEntry.COLUMN_DIARY_TITLE,
                DiaryInfoEntry.COLUMN_DIARY_TEXT,
                DiaryInfoEntry.COLUMN_SUBJECT_ID,
                DiaryInfoEntry._ID};
        String diaryOrderBy =DiaryInfoEntry.COLUMN_SUBJECT_ID + "," + DiaryInfoEntry.COLUMN_DIARY_TITLE;
        final Cursor diaryCursor = db.query(DiaryInfoEntry.TABLE_NAME, diaryColumns,
                null, null, null, null, diaryOrderBy);
        loadDiariesFromDatabase(diaryCursor);

    }

    private static void loadDiariesFromDatabase(Cursor cursor) {
        int diaryTitlePos = cursor.getColumnIndex(DiaryInfoEntry.COLUMN_DIARY_TITLE);
        int diaryTextPos = cursor.getColumnIndex(DiaryInfoEntry.COLUMN_DIARY_TEXT);
        int subjectIdPos = cursor.getColumnIndex(DiaryInfoEntry.COLUMN_SUBJECT_ID);
        int idPos = cursor.getColumnIndex(DiaryInfoEntry._ID);

        DataManager dm = getInstance();
        dm.mDiaries.clear();
        while (cursor.moveToNext()){
            String diaryTitle = cursor.getString(diaryTitlePos);
            String diaryText = cursor.getString(diaryTextPos);
            String subjectId = cursor.getString(subjectIdPos);
            int id = cursor.getInt(idPos);

            SubjectInfo diarySubject = dm.getSubject(subjectId);
            DiaryInfo diary = new DiaryInfo(id, diarySubject, diaryTitle, diaryText);
            dm.mDiaries.add(diary);
        }
        cursor.close();
    }

    private static void loadSubjectsFromDatabase(Cursor cursor) {
        int subjectIdPos = cursor.getColumnIndex(SubjectInfoEntry.COLUMN_SUBJECT_ID);
        int subjectTitlePos = cursor.getColumnIndex(SubjectInfoEntry.COLUMN_SUBJECT_TITLE);

        DataManager dm = getInstance();
        dm.mSubjects.clear();
        while (cursor.moveToNext()){
            String subjectId = cursor.getString(subjectIdPos);
            String subjectTitle = cursor.getString(subjectTitlePos);
            SubjectInfo subject = new SubjectInfo(subjectId, subjectTitle, null);

            dm.mSubjects.add(subject);
        }
        cursor.close();
    }

    public String getCurrentUserName() {
        return "Okoro Fortunate";
    }

    public String getCurrentUserEmail() {
        return "okorocfortunate@gmail.com";
    }

    public List<DiaryInfo> getDiaries() {
        return mDiaries;
    }

    public int createNewDiary() {
        DiaryInfo diary = new DiaryInfo(null, null, null);
        mDiaries.add(diary);
        return mDiaries.size() - 1;
    }

    public int findDiary(DiaryInfo diary) {
        for(int index = 0; index < mDiaries.size(); index++) {
            if(diary.equals(mDiaries.get(index)))
                return index;
        }

        return -1;
    }

    public void removeDiary(int index) {
        mDiaries.remove(index);
    }

    public List<SubjectInfo> getSubjects() {
        return mSubjects;
    }

    public SubjectInfo getSubject(String id) {
        for (SubjectInfo subject : mSubjects) {
            if (id.equals(subject.getSubjectId()))
                return subject;
        }
        return null;
    }

    public List<DiaryInfo> getDiaries(SubjectInfo subject) {
        ArrayList<DiaryInfo> diaries = new ArrayList<>();
        for(DiaryInfo diary:mDiaries) {
            if(subject.equals(diary.getSubject()))
                diaries.add(diary);
        }
        return diaries;
    }

    public int getDiaryCount(SubjectInfo subject) {
        int count = 0;
        for(DiaryInfo diary:mDiaries) {
            if(subject.equals(diary.getSubject()))
                count++;
        }
        return count;
    }

    private DataManager() {
    }

    //region Initialization code

    private void initializeSubjects() {
        mSubjects.add(initializeSubject1());
        mSubjects.add(initializeSubject2());
        mSubjects.add(initializeSubject3());
        mSubjects.add(initializeSubject4());
		mSubjects.add(initializeSubject5());
		mSubjects.add(initializeSubject6());
    }

    public void initializeExampleDiaries() {
        final DataManager dm = getInstance();

        SubjectInfo subject = dm.getSubject("sport_life");
        subject.getModule("sport_life_m01").setComplete(true);
        subject.getModule("sport_life_m02").setComplete(true);
        subject.getModule("sport_life_m03").setComplete(true);
        mDiaries.add(new DiaryInfo(subject, "My sport life",
                "Wish I can get involve in one of the known sports in life"));
        mDiaries.add(new DiaryInfo(subject, "Football",
                "A game of luck, but i love you though"));
        mDiaries.add(new DiaryInfo(subject, "Volleyball",
                "I will love to play this one day"));

        subject = dm.getSubject("academic_class");
        subject.getModule("academic_class_m01").setComplete(true);
        subject.getModule("academic_class_m02").setComplete(true);
        mDiaries.add(new DiaryInfo(subject, "Engineering, science and technology",
                "I am an Electrical and Electronic Engineer"));
        mDiaries.add(new DiaryInfo(subject, "Programming: Android development",
                "I love programming,for it gives me joy"));

        subject = dm.getSubject("work_today");
        subject.getModule("work_today_m01").setComplete(true);
        subject.getModule("work_today_m02").setComplete(true);
		subject.getModule("work_today_m03").setComplete(true);
        mDiaries.add(new DiaryInfo(subject, "Transportation",
                "It was very difficult getting vehicle, today"));
        mDiaries.add(new DiaryInfo(subject, "Boss",
                "She is a nice, friendly, awesome and a wonderful personality"));
        mDiaries.add(new DiaryInfo(subject, "Colleagues",
                "They are wonderful, but like aproko"));

        subject = dm.getSubject("politics_nigeria");
        subject.getModule("politics_nigeria_m01").setComplete(true);
        subject.getModule("politics_nigeria_m02").setComplete(true);
        subject.getModule("politics_nigeria_m03").setComplete(true);
        mDiaries.add(new DiaryInfo(subject, "Nigeria",
                "Its only connection that will make you survive here"));
        mDiaries.add(new DiaryInfo(subject, "Africa",
                "wonderful continent, nice culture but being progressive, they hate it"));
        mDiaries.add(new DiaryInfo(subject, "World",
                "Its a norm to meet a renown personality, just work hard"));
				
		subject = dm.getSubject("religious_belief");
        subject.getModule("religious_belief_m01").setComplete(true);
        subject.getModule("religious_belief_m02").setComplete(true);
        subject.getModule("religious_belief_m03").setComplete(true);
        mDiaries.add(new DiaryInfo(subject, "Anglican, Gwagwa",
                "uhmm, that's all I can say"));
        mDiaries.add(new DiaryInfo(subject, "ACOL, FUTO",
                "A family, i was part of and miss so dearly"));
        mDiaries.add(new DiaryInfo(subject, "ACFM, Maitaima",
                "Wonderful fellowship, but they can do guy for Africa eh!"));
				
		subject = dm.getSubject("miscellaneous_wow");
        subject.getModule("miscellaneous_wow_m01").setComplete(true);
        subject.getModule("miscellaneous_wow_m02").setComplete(true);
        subject.getModule("miscellaneous_wow_m03").setComplete(true);
        mDiaries.add(new DiaryInfo(subject, "Something",
                "Something, i love doing most"));
        mDiaries.add(new DiaryInfo(subject, "My game life",
                "I love playing games"));
        mDiaries.add(new DiaryInfo(subject, "My social life",
                "Its so boring, dont come closer"));
    }

    private SubjectInfo initializeSubject1() {
        List<ModuleInfo> modules = new ArrayList<>();
        modules.add(new ModuleInfo("sport_life_m01", "My sport life"));
        modules.add(new ModuleInfo("sport_life_m02", "Football"));
        modules.add(new ModuleInfo("sport_life_m03", "Volleyball"));

            return new SubjectInfo("sport_life", "Sport", modules);
    }

    private SubjectInfo initializeSubject2() {
        List<ModuleInfo> modules = new ArrayList<>();
        modules.add(new ModuleInfo("academic_class_m01", "Engineering, science and technology"));
        modules.add(new ModuleInfo("academic_class_m02", "Programming: Android development"));

        return new SubjectInfo("academic_class", "Academics", modules);
    }

    private SubjectInfo initializeSubject3() {
        List<ModuleInfo> modules = new ArrayList<>();
        modules.add(new ModuleInfo("work_today_m01", "Transportation"));
        modules.add(new ModuleInfo("work_today_m02", "Boss"));
        modules.add(new ModuleInfo("work_today_m03", "Colleagues"));

        return new SubjectInfo("work_today", "Work", modules);
    }

    private SubjectInfo initializeSubject4() {
        List<ModuleInfo> modules = new ArrayList<>();
        modules.add(new ModuleInfo("politics_nigeria_m01", "Nigeria"));
        modules.add(new ModuleInfo("politics_nigeria_m02", "Africa"));
        modules.add(new ModuleInfo("politics_nigeria_m03", "World"));

        return new SubjectInfo("politics_nigeria", "Politics", modules);
    }
	
	private SubjectInfo initializeSubject5() {
        List<ModuleInfo> modules = new ArrayList<>();
        modules.add(new ModuleInfo("religious_belief_m01", "Anglican Gwagwa"));
		modules.add(new ModuleInfo("religious_belief_m02", "ACOL FUTO"));
        modules.add(new ModuleInfo("religious_belief_m03", "ACFM maitaima"));

        return new SubjectInfo("religious_belief", "Religion", modules);
    }
	
	private SubjectInfo initializeSubject6() {
        List<ModuleInfo> modules = new ArrayList<>();
        modules.add(new ModuleInfo("miscellaneous_wow_m01", "Somethiing"));
        modules.add(new ModuleInfo("miscellaneous_wow_m02", "My game life"));
        modules.add(new ModuleInfo("miscellaneous_wow_m03", "My social life"));

        return new SubjectInfo("miscellaneous_wow", "Others", modules);
    }

    public int createNewDiary(SubjectInfo subject, String diaryTitle, String diaryText) {
        int index = createNewDiary();
        DiaryInfo diary = getDiaries().get(index);
        diary.setSubject(subject);
        diary.setTitle(diaryTitle);
        diary.setText(diaryText);

        return index;
    }
    //endregion

}
