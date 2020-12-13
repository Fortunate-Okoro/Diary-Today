package e.user.diarytoday;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import e.user.diarytoday.DiaryTodayDatabaseContract.DiaryInfoEntry;
import e.user.diarytoday.DiaryTodayDatabaseContract.SubjectInfoEntry;

public class DiaryRecyclerAdapter extends RecyclerView.Adapter<DiaryRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mSubjectPos;
    private int mDiaryTitlePos;
    private int mIdPos;

    public DiaryRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if (mCursor == null)
            return;
        // Get column indexes from mCursor
        mSubjectPos = mCursor.getColumnIndex(SubjectInfoEntry.COLUMN_SUBJECT_TITLE);
        mDiaryTitlePos = mCursor.getColumnIndex(DiaryInfoEntry.COLUMN_DIARY_TITLE);
        mIdPos = mCursor.getColumnIndex(DiaryInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor) {
        if (mCursor != null)
            mCursor.close();
        mCursor = cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_diary_list, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String subject = mCursor.getString(mSubjectPos);
        String diaryTitle = mCursor.getString(mDiaryTitlePos);
        int id = mCursor.getInt(mIdPos);

        holder.mTextSubject.setText(subject);
        holder.mTextTitle.setText(diaryTitle);
        holder.mId = id;
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView mTextSubject;
        public final TextView mTextTitle;
        public int mId;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextSubject = (TextView) itemView.findViewById(R.id.text_subject);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DiaryActivity.class);
                    intent.putExtra(DiaryActivity.DIARY_ID, mId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
