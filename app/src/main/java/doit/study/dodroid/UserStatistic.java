package doit.study.dodroid;

import android.os.Parcel;
import android.os.Parcelable;

public class UserStatistic implements Parcelable{
    Integer mTotalRight = 0;
    Integer mTotalWrong = 0;
    Integer mCurrentPosition = 0;
    Integer mTotalQuestions = 0;

    public static final Parcelable.Creator<UserStatistic> CREATOR
            = new Parcelable.Creator<UserStatistic>() {
        public UserStatistic createFromParcel(Parcel in) {
            return new UserStatistic(in);
        }

        public UserStatistic[] newArray(int size) {
            return new UserStatistic[size];
        }
    };

    public UserStatistic(){}

    @Override
    public int describeContents() {
        return 0;
    }

    // pay close attention to the order
    public UserStatistic(Parcel in){
        mTotalRight = in.readInt();
        mTotalWrong = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mTotalRight);
        out.writeInt(mTotalWrong);
    }

    @Override
    public String toString(){
        return "mCurrentPosition "+mCurrentPosition;
    }

}
