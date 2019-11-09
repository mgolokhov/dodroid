package doit.study.droid.data.remote

import com.google.gson.annotations.SerializedName

data class QuizData (
        @SerializedName("ID")
        var id: Int = 0,
        @SerializedName("question")
        var text: String,
        @SerializedName("wrong")
        var wrongAnswers: List<String>,
        @SerializedName("right")
        var rightAnswers: List<String>,
        @SerializedName("tags")
        var tags: List<String>,
        @SerializedName("docRef")
        var docRef: String,
        @SerializedName("questionType")
        var questionType: Int = 0
)