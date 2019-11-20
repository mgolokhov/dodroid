package doit.study.droid.utils

import android.app.Application
import android.content.res.AssetManager
import android.media.MediaPlayer
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

import androidx.preference.PreferenceManager

import java.io.File
import java.io.IOException
import java.util.Random

import doit.study.droid.R
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Sound @Inject constructor(private val context: Application): DefaultLifecycleObserver {
    private var soundsForWrongAnswer: Array<String>? = null
    private var soundsForRightAnswer: Array<String>? = null
    private var assetManager: AssetManager = context.resources.assets
    private var mediaPlayer: MediaPlayer? = null

    private// turn off sound (user may turn it on manually again) if audio list is corrupted
    val isEnabled: Boolean
        get() {
            val SP = PreferenceManager.getDefaultSharedPreferences(context)
            val prefEnabled = SP.getBoolean(context.resources.getString(R.string.pref_sound), true)
            val audioListLoaded = soundsForWrongAnswer != null && soundsForRightAnswer != null
            if (prefEnabled && !audioListLoaded)
                displayErrMessageAndTurnOffSound()
            return prefEnabled && audioListLoaded
        }

    init {
        setupSoundsPlaylist()
    }

    private fun setupSoundsPlaylist() {
        try {
            soundsForWrongAnswer = assetManager.list(PATH_SOUNDS_WRONG)
            soundsForRightAnswer = assetManager.list(PATH_SOUNDS_RIGHT)
        } catch (e: IOException) {
            Timber.e(e)
            displayErrMessageAndTurnOffSound()
        }

    }

    private fun displayErrMessageAndTurnOffSound() {
        Toast.makeText(context, R.string.cannot_load_sound, Toast.LENGTH_SHORT).show()
        val SP = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = SP.edit()
        editor.putBoolean(context.resources.getString(R.string.pref_sound), false)
        editor.commit()
    }

    private fun getRandIndex(maxValue: Int): Int {
        return Random().nextInt(maxValue)
    }

    fun play(isRight: Boolean, lifecycle: Lifecycle) {
        if (!isEnabled)
            return
        lifecycle.addObserver(this)
        val currentSound = if (isRight)
            getRandomlyPathToSound(PATH_SOUNDS_RIGHT, soundsForRightAnswer!!)
        else
            getRandomlyPathToSound(PATH_SOUNDS_WRONG, soundsForWrongAnswer!!)

        try {
            val afd = assetManager.openFd(currentSound)
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        } catch (e: IOException) {
            Timber.e(e)
            displayErrMessageAndTurnOffSound()
        }
    }

    private fun getRandomlyPathToSound(rootPath: String, baseNames: Array<String>): String {
        val randIndex = getRandIndex(baseNames.size)
        val baseName = baseNames[randIndex]
        return File(rootPath, baseName).path
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        mediaPlayer?.release()
        owner.lifecycle.removeObserver(this)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        try {
            mediaPlayer?.run {
                if (isPlaying) stop()
            }
        } catch (e: IllegalStateException) {
            Timber.e(e)
        }
    }

    companion object {
        private const val PATH_SOUNDS_WRONG = "wrong"
        private const val PATH_SOUNDS_RIGHT = "right"
    }
}
