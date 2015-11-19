package doit.study.dodroid;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Question implements Parcelable{
    public String question;
    public ArrayList<String> wrong = new ArrayList<>();
    public ArrayList<String> right = new ArrayList<>();
    public ArrayList<String> tags = new ArrayList<>();
    public int rightCounter;
    public int wrongCounter;


    public Question(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    // pay close attention to the order
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(question);
        out.writeStringList(wrong);
        out.writeStringList(right);
        out.writeStringList(tags);
        out.writeInt(rightCounter);
        out.writeInt(wrongCounter);
    }

    public static final Creator<Question> CREATOR
            = new Creator<Question>() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    // pay close attention to the order
    public Question(Parcel in) {
        question = in.readString();
        in.readStringList(wrong);
        in.readStringList(right);
        in.readStringList(tags);
        rightCounter = in.readInt();
        wrongCounter = in.readInt();
    }

    @Override
    public String toString(){
        return "\nquestion: "+question+" wrong: "+wrong.toString()+" right: "+right.toString();
    }
}
