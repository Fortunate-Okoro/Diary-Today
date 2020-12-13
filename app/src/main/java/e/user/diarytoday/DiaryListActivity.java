package e.user.diarytoday;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.animation.Positioning;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

public class DiaryListActivity extends AppCompatActivity {
    private DiaryRecyclerAdapter mDiaryRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(DiaryListActivity.this, DiaryActivity.class));

            }
        });

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDiaryRecyclerAdapter.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {
        final RecyclerView recyclerDiaries = (RecyclerView) findViewById(R.id.list_diaries);
        final LinearLayoutManager diariesLayoutManager = new LinearLayoutManager(this);
        recyclerDiaries.setLayoutManager(diariesLayoutManager);

        List<DiaryInfo> diaries = DataManager.getInstance().getDiaries();
        mDiaryRecyclerAdapter = new DiaryRecyclerAdapter(this, null);
        recyclerDiaries.setAdapter(mDiaryRecyclerAdapter);
    }

}
