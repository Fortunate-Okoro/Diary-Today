package e.user.diarytoday;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class SubjectRecyclerAdapter extends RecyclerView.Adapter<SubjectRecyclerAdapter.ViewHolder>{

    private final Context mContext;
    private final List<SubjectInfo> mSubjects;
    private final LayoutInflater mLayoutInflater;

    public SubjectRecyclerAdapter(Context context, List<SubjectInfo> subjects) {
        mContext = context;
        mSubjects = subjects;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_subject_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SubjectInfo subject = mSubjects.get(position);
        holder.mTextSubject.setText(subject.getTitle());
        holder.mCurrentPosition = position;
    }

    @Override
    public int getItemCount() {
        return mSubjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView mTextSubject;
        public int mCurrentPosition;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextSubject = (TextView) itemView.findViewById(R.id.text_subject);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v, mSubjects.get(mCurrentPosition).getTitle(),
                            Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
