package e.user.diarytoday;

import android.os.Parcel;
import android.os.Parcelable;

public final class DiaryInfo implements Parcelable{
    private SubjectInfo mSubject;
    private String mTitle;
    private String mText;
    private int mId;

    public DiaryInfo(int id, SubjectInfo subject, String title, String text) {
        mId = id;
        mSubject = subject;
        mTitle = title;
        mText = text;
    }

    public DiaryInfo(SubjectInfo subject, String title, String text) {
        mSubject = subject;
        mTitle = title;
        mText = text;
    }

    private DiaryInfo(Parcel source) {
        mSubject = source.readParcelable(SubjectInfo.class.getClassLoader());
        mTitle = source.readString();
        mText = source.readString();
    }

   public int getId() {
        return mId;
    }

    public SubjectInfo getSubject() {
        return mSubject;
    }

    public void setSubject(SubjectInfo subject) {
        mSubject = subject;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    private String getCompareKey() {
        return mSubject.getSubjectId() + "|" + mTitle + "|" + mText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

       DiaryInfo that = (DiaryInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mSubject, 0);
        dest.writeString(mTitle);
        dest.writeString(mText);
    }

    public static final Parcelable.Creator<DiaryInfo> CREATOR =
            new Parcelable.Creator<DiaryInfo>() {

                @Override
                public DiaryInfo createFromParcel(Parcel source) {
                    return new DiaryInfo(source);
                }

                @Override
                public DiaryInfo[] newArray(int size) {
                    return new DiaryInfo[size];
                }
            };
}
