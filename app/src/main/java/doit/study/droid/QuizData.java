package doit.study.droid;

import android.app.Application;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

import doit.study.droid.sqlite.helper.DatabaseHelper;

public class QuizData extends Application {
    // A common compound key for all tables
    public static class Table {
        public static final String TOPIC_NUM_ID = "topic_num_id";
        public static final String TEST_NUM_ID = "test_num_id";
        public static final String QUESTION_NUM_ID = "question_num_id";
    }

    private DatabaseHelper mDBHelper;


    @Override
    public void onCreate() {
        super.onCreate();
        mDBHelper = new DatabaseHelper(this);
    }

    public Question getQuestionById(QuizData.Id id){
        return mDBHelper.getQuestionById(id);
    }

    public HashMap<Id, Question> getQuestionsById(Id id){
        return mDBHelper.getQuestionsById(id);
    }

    public HashMap<Id, Question> getQuestionsByLearningStatus(boolean onLearning){
        return mDBHelper.getQuestionsByLearningStatus(onLearning);
    }

    public HashMap<Id, Statistics> getStatisticsById(Id id){
        return mDBHelper.getStatisticsById(id);
    }

    public Statistics getStatById(Id id){
        return mDBHelper.getStatById(id);
    }


    // Parcelable faster than standard java Serializable
    public static class Id implements Parcelable {
        public int topic;
        public int test;
        public int question;

        public Id(int topic, int test, int question) {
            this.topic = topic;
            this.test = test;
            this.question = question;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id)) return false;
            Id key = (Id) o;
            return topic == key.topic && test == key.test && question == key.question;
        }

        @Override
        public int hashCode() {
            int result = topic;
            result = 31 * result + test;
            result = 31 * result + topic;
            result = 31 * result + question;
            return result;
        }

        @Override
        public String toString() {
            return String.format("%d_%d_%d", topic, test, question);
        }

        public static final Creator<Id> CREATOR
                = new Creator<Id>() {
            public Id createFromParcel(Parcel in) {
                return new Id(in);
            }

            public Id[] newArray(int size) {
                return new Id[size];
            }
        };
        @Override
        public int describeContents() {
            return 0;
        }

        public Id(){}

        // pay close attention to the order
        public Id(Parcel in){
            topic = in.readInt();
            test = in.readInt();
            question = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(topic);
            out.writeInt(test);
            out.writeInt(question);
        }
    }
}
