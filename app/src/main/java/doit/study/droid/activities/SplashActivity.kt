package doit.study.droid.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import doit.study.droid.R
import doit.study.droid.app.App
import doit.study.droid.data.Configuration
import doit.study.droid.data.Question
import doit.study.droid.data.QuizDBHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SplashActivity : AppCompatActivity() {
    private val sharedPref: SharedPreferences by lazy {getSharedPreferences(PREF_NAME, MODE_PRIVATE)}

    val handleQuestionsResponse = object : Callback<List<Question>> {
        override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
            try {
                populateDB(response)
                navigateToTopicChooser()
            } catch (e: Exception) {
                handleErrCannotLoadQuiz(e)
            }

        }

        override fun onFailure(call: Call<List<Question>>, t: Throwable) {
            handleErrCannotLoadQuiz(t)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val quizDataClient = (application as App).quizService

        quizDataClient.configuration.enqueue(object : Callback<Configuration> {
            override fun onFailure(call: Call<Configuration>, t: Throwable) {
                handleErrCannotLoadQuiz(t)
            }

            override fun onResponse(call: Call<Configuration>, response: Response<Configuration>) {
                try {
                    response.body()?.contentVersion?.let {
                        val previousVersion = sharedPref.getInt(PREF_NAME, 0)
                        if (it > previousVersion) {
                            quizDataClient.questions.enqueue(handleQuestionsResponse)
                            sharedPref.edit().putInt(PREF_NAME, it).apply()
                        } else {
                            navigateToTopicChooser()
                        }
                    }
                } catch (e: Exception) {
                    handleErrCannotLoadQuiz(e)
                }
            }

        })
    }

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

    private fun populateDB(response: Response<List<Question>>){
        val helper = QuizDBHelper(baseContext)
        helper.insertFromFile(response.body(), helper.writableDatabase)
        helper.close()
    }

    companion object {
        private const val PREF_NAME = "SplashActivity.content.version"
    }
}
