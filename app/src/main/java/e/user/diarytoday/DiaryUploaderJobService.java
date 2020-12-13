package e.user.diarytoday;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;

public class DiaryUploaderJobService extends JobService {
    public static final String EXTRA_DATA_URI = "e.user.diarytoday.extras.DATA_URI";
    private DiaryUploader mDiaryUploader;

    public DiaryUploaderJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        AsyncTask<JobParameters, Void, Void> task = new AsyncTask<JobParameters, Void, Void>() {
            @Override
            protected Void doInBackground(JobParameters... backgroundParams) {
                JobParameters jobParams = backgroundParams[0];

                String stringDataUri = jobParams.getExtras().getString(EXTRA_DATA_URI);
                Uri dataUri = Uri.parse(stringDataUri);
                mDiaryUploader.doUpload(dataUri);

                if( ! mDiaryUploader.isCanceled())
                    jobFinished(jobParams, false);

                return null;
            }
        };

        mDiaryUploader = new DiaryUploader(this);
        task.execute(params);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}
