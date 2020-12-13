package e.user.diarytoday;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public final class SubjectInfo implements Parcelable {
    private final String mSubjectId;
    private final String mTitle;
    private final List<ModuleInfo> mModules;

    public SubjectInfo(String subjectId, String title, List<ModuleInfo> modules) {
        mSubjectId = subjectId;
        mTitle = title;
        mModules = modules;
    }

    private SubjectInfo(Parcel source) {
        mSubjectId = source.readString();
        mTitle = source.readString();
        mModules = new ArrayList<>();
        source.readTypedList(mModules, ModuleInfo.CREATOR);
    }

    public String getSubjectId() {
        return mSubjectId;
    }

    public String getTitle() {
        return mTitle;
    }

    public List<ModuleInfo> getModules() {
        return mModules;
    }

    public boolean[] getModulesCompletionStatus() {
        boolean[] status = new boolean[mModules.size()];

        for(int i=0; i < mModules.size(); i++)
            status[i] = mModules.get(i).isComplete();

        return status;
    }

    public void setModulesCompletionStatus(boolean[] status) {
        for(int i=0; i < mModules.size(); i++)
            mModules.get(i).setComplete(status[i]);
    }

    public ModuleInfo getModule(String moduleId) {
        for(ModuleInfo moduleInfo: mModules) {
            if(moduleId.equals(moduleInfo.getModuleId()))
                return moduleInfo;
        }
        return null;
    }

    @Override
    public String toString() {
        return mTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubjectInfo that = (SubjectInfo) o;

        return mSubjectId.equals(that.mSubjectId);

    }

    @Override
    public int hashCode() {
        return mSubjectId.hashCode();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSubjectId);
        dest.writeString(mTitle);
        dest.writeTypedList(mModules);
    }

    public static final Parcelable.Creator<SubjectInfo> CREATOR =
            new Parcelable.Creator<SubjectInfo>() {

                @Override
                public SubjectInfo createFromParcel(Parcel source) {
                    return new SubjectInfo(source);
                }

                @Override
                public SubjectInfo[] newArray(int size) {
                    return new SubjectInfo[size];
                }
            };

}
