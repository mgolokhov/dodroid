package doit.study.droid.data.source.remote;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionWeb {
    @SerializedName("ID")
    public int id;
    @SerializedName("question")
    public String text;
    @SerializedName("wrong")
    public List<String> wrongAnswers;
    @SerializedName("right")
    public List<String> rightAnswers;
    @SerializedName("tags")
    public List<String> tags;
    @SerializedName("docRef")
    public String docRef;
    @SerializedName("questionType")
    public int questionType;


    public QuestionWeb(int id, String text, List<String> wrongAnswers, List<String> rightAnswers, List<String> tags, String docRef, int questionType) {
        this.id = id;
        this.text = text;
        this.wrongAnswers = wrongAnswers;
        this.rightAnswers = rightAnswers;
        this.tags = tags;
        this.docRef = docRef;
        this.questionType = questionType;
    }
}
