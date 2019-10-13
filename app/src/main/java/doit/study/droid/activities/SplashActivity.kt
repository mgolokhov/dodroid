package doit.study.droid.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import doit.study.droid.R
import doit.study.droid.app.App
import doit.study.droid.data.Question
import doit.study.droid.data.QuizDBHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SplashActivity : AppCompatActivity() {
    private val sharedPref: SharedPreferences by lazy {
        getSharedPreferences(PREVIOUS_CONTENT_VERSION, MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val quizDataClient = (application as App).quizService

        GlobalScope.launch(Dispatchers.Main) {
            try {
                withContext(Dispatchers.IO) {
                    val config = quizDataClient.configuration()
                    if (getPreviousVersion() < config.contentVersion) {
                        populateDB(quizDataClient.questions())
                        savePreviousVersion(config.contentVersion)
                    }
                }
            } catch (e: Exception) {
                handleErrCannotLoadQuiz(e)
            } finally {
                navigateToTopicChooser()
            }
        }
    }

    private fun getPreviousVersion() = sharedPref.getInt(PREVIOUS_CONTENT_VERSION, 0)

    private fun savePreviousVersion(version: Int) = sharedPref
            .edit()
            .putInt(PREVIOUS_CONTENT_VERSION, version)
            .apply()

    private fun handleErrCannotLoadQuiz(t: Throwable) {
        Toast.makeText(this, R.string.failed_to_load_quiz, Toast.LENGTH_SHORT).show()
        Timber.e(t)
        finish()
    }

    private fun navigateToTopicChooser(){
        val intent = Intent(this, TopicsChooserActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun populateDB(questions: List<Question>){
        val helper = QuizDBHelper(baseContext)
        helper.insertFromFile(questions, helper.writableDatabase)
        helper.close()
    }

    companion object {
        private const val PREVIOUS_CONTENT_VERSION = "SplashActivity.content.version"
    }
}
