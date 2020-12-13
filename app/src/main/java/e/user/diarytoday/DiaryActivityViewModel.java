package e.user.diarytoday;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;

public class DiaryActivityViewModel extends ViewModel {
    public static final String ORIGINAL_DIARY_SUBJECT_ID = "e.user.diarytoday.ORIGINAL_DIARY_SUBJECT_ID";
    public static final String ORIGINAL_DIARY_TITLE = "e.user.diarytoday.ORIGINAL_DIARY_TITLE";
    public static final String ORIGINAL_DIARY_TEXT = "e.user.diarytoday.ORIGINAL_DIARY_TEXT";
    public String mOriginalDiarySubjectId;
    public String mOriginalDiaryTitle;
    public String mOriginalDiaryText;
    public boolean mIsNewlyCreated = true;

    public void saveState(Bundle outState) {
        outState.putString(ORIGINAL_DIARY_SUBJECT_ID, mOriginalDiarySubjectId);
        outState.putString(ORIGINAL_DIARY_TITLE, mOriginalDiaryTitle);
        outState.putString(ORIGINAL_DIARY_TEXT, mOriginalDiaryText);
    }

    public void restoreState(Bundle inState){
        mOriginalDiarySubjectId = inState.getString(ORIGINAL_DIARY_SUBJECT_ID);
        mOriginalDiaryTitle = inState.getString(ORIGINAL_DIARY_TEXT);
        mOriginalDiaryText = inState.getString(ORIGINAL_DIARY_TITLE);
    }
}