package doit.study.droid.data.local

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizContentVersion @Inject constructor(val context: Application) {
    private val pref = context.getSharedPreferences(
            QUIZ_CONTENT_VERSION,
            AppCompatActivity.MODE_PRIVATE
    )

    fun getVersion(): Int = pref.getInt(QUIZ_CONTENT_VERSION_KEY, 0)

    fun saveVersion(version: Int) = pref.edit().putInt(QUIZ_CONTENT_VERSION_KEY, version).apply()

    companion object {
        const val QUIZ_CONTENT_VERSION = "quiz.content.version"
        const val QUIZ_CONTENT_VERSION_KEY = "quiz.content.version.key"
    }

}