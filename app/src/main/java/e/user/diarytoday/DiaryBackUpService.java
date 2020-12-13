package e.user.diarytoday;

import android.app.IntentService;
import android.content.Intent;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DiaryBackUpService extends IntentService {

    public static final String EXTRA_SUBJECT_ID = "e.user.diarytoday.extra.SUBJECT_ID";

    public DiaryBackUpService() {
        super("DiaryBackUpService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String backupSubjectId = intent.getStringExtra(EXTRA_SUBJECT_ID);
            DiaryBackup.doBackup(this, backupSubjectId);
            }
        }

}
