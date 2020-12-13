package e.user.diarytoday;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static org.hamcrest.Matchers.*;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.*;

@RunWith(AndroidJUnit4.class)
public class DiaryCreationTest {
    static DataManager sDataManager;
    @BeforeClass
    public static void classSetUp() throws Exception {
        sDataManager = DataManager.getInstance();
    }

    @Rule
    public ActivityTestRule<DiaryListActivity> mDiaryListActivityTestRule =
            new ActivityTestRule<>(DiaryListActivity.class);

    @Test
    public void createNewDiary(){
        final SubjectInfo subject = sDataManager.getSubject("work_today");
        final String diaryTitle = "Test diary title";
        final String diaryText = "This is the body of our test diary";

//        ViewInteraction fabNewDiary = onView(withId(R.id.fab));
//        fabNewDiary.perform(click());
        onView(withId(R.id.fab)).perform(click());

        onData(allOf(instanceOf(SubjectInfo.class), equalTo(subject))).perform(click());
        onView(withId(R.id.spinner_subjects)).check(matches(withSpinnerText(
                containsString(subject.getTitle()))));

        onView(withId(R.id.text_diary_title)).perform(typeText(diaryTitle))
                .check(matches(withText(containsString(diaryTitle))));

        onView(withId(R.id.text_diary_text)).perform(typeText(diaryText),
                closeSoftKeyboard());
        onView(withId(R.id.text_diary_text)).check(matches(withText(containsString(diaryText))));

        pressBack();

        int diaryIndex = sDataManager.getDiaries().size() - 1;
        DiaryInfo diary = sDataManager.getDiaries().get(diaryIndex);
        assertEquals(subject, diary.getSubject());
        assertEquals(diaryTitle, diary.getTitle());
        assertEquals(diaryText, diary.getText());
    }
}