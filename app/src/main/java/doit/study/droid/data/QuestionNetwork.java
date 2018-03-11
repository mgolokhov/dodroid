package doit.study.droid.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionNetwork {
    @SerializedName("ID")
    public int mId;
    @SerializedName("question")
    public String mText;
    @SerializedName("wrong")
    public List<String> mWrongAnswers;
    @SerializedName("right")
    public List<String> mRightAnswers;
    @SerializedName("tags")
    public List<String> mTags;
    @SerializedName("docRef")
    public String mDocRef;
    @SerializedName("questionType")
    public int mQuestionType;


    public QuestionNetwork(int mId, String mText, List<String> mWrongAnswers, List<String> mRightAnswers, List<String> mTags, String mDocRef, int mQuestionType) {
        this.mId = mId;
        this.mText = mText;
        this.mWrongAnswers = mWrongAnswers;
        this.mRightAnswers = mRightAnswers;
        this.mTags = mTags;
        this.mDocRef = mDocRef;
        this.mQuestionType = mQuestionType;
    }
}
