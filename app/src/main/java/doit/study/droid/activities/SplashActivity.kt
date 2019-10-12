package doit.study.droid.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import doit.study.droid.R
import doit.study.droid.app.App
import doit.study.droid.data.Question
import doit.study.droid.data.QuizDBHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        (application as App).quizService.questions.enqueue(object : Callback<List<Question>> {
            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                try {
                    val helper = QuizDBHelper(baseContext)
                    helper.insertFromFile(response.body(), helper.writableDatabase)
                    helper.close()

                    val intent = Intent(this@SplashActivity, TopicsChooserActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    handleErrCannotLoadQuiz(e)
                }

            }

            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                handleErrCannotLoadQuiz(t)
            }


            private fun handleErrCannotLoadQuiz(t: Throwable) {
                Toast.makeText(this@SplashActivity, R.string.failed_to_load_quiz, Toast.LENGTH_SHORT).show()
                Timber.e(t)
                finish()
            }
        })


    }
}
