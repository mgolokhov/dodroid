package doit.study.droid.splash.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import doit.study.droid.app.App
import doit.study.droid.common.ui.MainDrawerActivity
import doit.study.droid.utils.EventObserver
import doit.study.droid.utils.lazyAndroid
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by lazyAndroid {
        ViewModelProviders.of(this, viewModelFactory)[SplashViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.dagger.inject(this)
        super.onCreate(savedInstanceState)

        setupNavigation()
        viewModel.syncWithServer()
    }

    private fun setupNavigation() {
        viewModel.showErrorAndExitEvent.observe(this, EventObserver {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            finish()
        })
        viewModel.navigateToTopicsEvent.observe(this, EventObserver {
            val intent = Intent(this, MainDrawerActivity::class.java)
            startActivity(intent)
            finish()
        })
    }
}
