package doit.study.droid.data.local

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizContentVersion @Inject constructor(val context: Application) {
    private val pref = context.getSharedPreferences(
            PREF_NAME,
            AppCompatActivity.MODE_PRIVATE
    )

    fun getVersion(): Int = pref.getInt(VERSION_KEY, 0)

    fun saveVersion(version: Int) = pref.edit().putInt(VERSION_KEY, version).apply()

    companion object {
        private const val PREF_NAME = "quiz.content.version"
        private const val VERSION_KEY = "quiz.content.version.key"
    }
}
