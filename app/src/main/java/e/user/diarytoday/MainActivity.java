package e.user.diarytoday;

import android.app.LoaderManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import e.user.diarytoday.DiaryTodayDatabaseContract.DiaryInfoEntry;
import e.user.diarytoday.DiaryTodayDatabaseContract.SubjectInfoEntry;
import e.user.diarytoday.DiaryTodayProviderContract.Diaries;

import static e.user.diarytoday.DiaryBackUpService.EXTRA_SUBJECT_ID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_DIARIES = 0;
    private static final int DIARY_UPLOADER_JOB_ID = 1;
    private DiaryRecyclerAdapter mDiaryRecyclerAdapter;
    private RecyclerView mRecyclerItems;
    private LinearLayoutManager mDiariesLayoutManager;
    private SubjectRecyclerAdapter mSubjectRecyclerAdapter;
    private GridLayoutManager mSubjectsLayoutManager;
    private DiaryTodayOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        enableStrictMode();

        mDbOpenHelper = new DiaryTodayOpenHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, DiaryActivity.class));
            }
        });

        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_notification, false);
        PreferenceManager.setDefaultValues(this, R.xml.pref_data_sync, false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initializeDisplayContent();
    }

    private void enableStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_DIARIES, null, this);
        updateNavHeader();

        openDrawer();
    }

    private void openDrawer() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.START);
            }
        }, 1000);
    }

    private void loadDiaries() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        final String[] diaryColumns = {
                DiaryInfoEntry.COLUMN_DIARY_TITLE,
                DiaryInfoEntry.COLUMN_SUBJECT_ID,
                DiaryInfoEntry._ID};

        String diaryOrderBy =DiaryInfoEntry.COLUMN_SUBJECT_ID + "," + DiaryInfoEntry.COLUMN_DIARY_TITLE;
        final Cursor diaryCursor = db.query(DiaryInfoEntry.TABLE_NAME, diaryColumns,
                null, null, null, null, diaryOrderBy);
        mDiaryRecyclerAdapter.changeCursor(diaryCursor);
    }

    private void updateNavHeader() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textUserName = (TextView) headerView.findViewById(R.id.text_user_name);
        TextView textEmailAddress = (TextView) headerView.findViewById(R.id.text_email_address);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String userName = pref.getString("user_display_name", "");
        String emailAddress = pref.getString("user_email_address", "");

        textUserName.setText(userName);
        textEmailAddress.setText(emailAddress);
    }

    private void initializeDisplayContent() {
        DataManager.loadFromDatabase(mDbOpenHelper);
        mRecyclerItems = (RecyclerView) findViewById(R.id.list_items);
        mDiariesLayoutManager = new LinearLayoutManager(this);
        mSubjectsLayoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.subject_grid_span));

        mDiaryRecyclerAdapter = new DiaryRecyclerAdapter(this, null);

        List<SubjectInfo> subjects = DataManager.getInstance().getSubjects();
        mSubjectRecyclerAdapter = new SubjectRecyclerAdapter(this, subjects);

        displayDiaries();
    }

    private void displayDiaries() {
        mRecyclerItems.setLayoutManager(mDiariesLayoutManager);
        mRecyclerItems.setAdapter(mDiaryRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_diaries);
    }

    private void selectNavigationMenuItem(int id) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(id).setChecked(true);
    }

    private void displaySubjects() {
        mRecyclerItems.setLayoutManager(mSubjectsLayoutManager);
        mRecyclerItems.setAdapter(mSubjectRecyclerAdapter);

        selectNavigationMenuItem(R.id.nav_subjects);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_backup_diaries) {
            backupDiaries();
        } else if (id == R.id.action_upload_diaries) {
            scheduleDiaryUpload();
        }

        return super.onOptionsItemSelected(item);
    }

    private void scheduleDiaryUpload() {
        PersistableBundle extras = new PersistableBundle();
        extras.putString(DiaryUploaderJobService.EXTRA_DATA_URI, Diaries.CONTENT_URI.toString());

        ComponentName componentName = new ComponentName(this, DiaryUploaderJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(DIARY_UPLOADER_JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(extras)
                .build();
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }

    private void backupDiaries() {
        Intent intent = new Intent(this, DiaryBackUpService.class);
        intent.putExtra(DiaryBackUpService.EXTRA_SUBJECT_ID, DiaryBackup.ALL_SUBJECTS);
        startService(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_diaries) {
            displayDiaries();
        } else if (id == R.id.nav_subjects) {
            displaySubjects();
        } else if (id == R.id.nav_share) {
            handleShare();
        } else if (id == R.id.nav_send) {
            handleSelection(R.string.nav_send_message);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleShare() {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, "Share to - " +
                PreferenceManager.getDefaultSharedPreferences(this).getString("user_favorite_social", ""),
                Snackbar.LENGTH_LONG).show();
    }

    private void handleSelection(int message_id) {
        View view = findViewById(R.id.list_items);
        Snackbar.make(view, message_id, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_DIARIES){
            final String[] diaryColumns = {
                    Diaries._ID,
                    Diaries.COLUMN_DIARY_TITLE,
                    Diaries.COLUMN_SUBJECT_TITLE
            };
            final String diaryOrderBy = Diaries.COLUMN_SUBJECT_TITLE +
                            "," + Diaries.COLUMN_DIARY_TITLE;

            loader = new CursorLoader(this, Diaries.CONTENT_EXPANDED_URI, diaryColumns,
                    null, null, diaryOrderBy);

        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_DIARIES) {
            mDiaryRecyclerAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_DIARIES){
            mDiaryRecyclerAdapter.changeCursor(null);
        }
    }

}
