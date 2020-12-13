package e.user.diarytoday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DiaryReminderReceiver extends BroadcastReceiver {
    public static final String EXTRA_DIARY_TITLE    =  "e.user.diarytoday.extra.DIARY_TITLE";
    public static final String EXTRA_DIARY_TEXT    =  "e.user.diarytoday.extra.DIARY_TEXT";
    public static final String EXTRA_DIARY_ID    =  "e.user.diarytoday.extra.DIARY_ID";


    @Override
    public void onReceive(Context context, Intent intent) {
        String diaryTitle = intent.getStringExtra(EXTRA_DIARY_TITLE);
        String diaryText = intent.getStringExtra(EXTRA_DIARY_TEXT);
        int diaryId = intent.getIntExtra(EXTRA_DIARY_ID, 0);

        DiaryReminderNotification.notify(context, diaryTitle, diaryText, diaryId);
    }
}
