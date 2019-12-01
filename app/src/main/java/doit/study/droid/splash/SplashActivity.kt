package doit.study.droid.splash

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import doit.study.droid.app.BaseApp
import doit.study.droid.common.MainDrawerActivity
import doit.study.droid.utils.EventObserver
import javax.inject.Inject

class SplashActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        BaseApp.dagger.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[SplashViewModel::class.java]
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
