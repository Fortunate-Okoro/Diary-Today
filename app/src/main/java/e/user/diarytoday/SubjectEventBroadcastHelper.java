package e.user.diarytoday;

import android.content.Context;
import android.content.Intent;

public class SubjectEventBroadcastHelper {
    public static final String ACTION_SUBJECT_EVENT = "e.user.diarytoday.action.SUBJECT_EVENT";
    public static final String EXTRA_SUBJECT_ID =  "e.user.diarytoday.extra.SUBJECT_ID";
    public static final String EXTRA_SUBJECT_MESSAGE =  "e.user.diarytoday.extra.SUBJECT_MESSAGE";

    public static void sendEventBroadcast(Context context, String subjectId, String message) {

        Intent intent = new Intent(ACTION_SUBJECT_EVENT);
        intent.putExtra(EXTRA_SUBJECT_ID, subjectId);
        intent.putExtra(EXTRA_SUBJECT_MESSAGE, message);

        context.sendBroadcast(intent);
    }
}
