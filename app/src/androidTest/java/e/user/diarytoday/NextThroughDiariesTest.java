package e.user.diarytoday;

import org.junit.Rule;
import org.junit.Test;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.Matchers.*;
import java.util.List;

import static org.junit.Assert.*;

public class NextThroughDiariesTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule(MainActivity.class);

    @Test
    public void NextThroughDiaries() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_diaries));

        onView(withId(R.id.list_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        List<DiaryInfo> diaries = DataManager.getInstance().getDiaries();
        for(int index = 0; index < diaries.size(); index++) {
            DiaryInfo diary = diaries.get(index);

            onView(withId(R.id.spinner_subjects)).check(
                    matches(withSpinnerText(diary.getSubject().getTitle())));
            onView(withId(R.id.text_diary_title)).check(matches(withText(diary.getTitle())));
            onView(withId(R.id.text_diary_text)).check(matches(withText(diary.getText())));

            if(index < diaries.size() - 1)
                onView(allOf(withId(R.id.action_next), isEnabled())).perform(click());
        }
        onView(withId(R.id.action_next)).check(matches(not(isEnabled())));
        pressBack();
    }
}
