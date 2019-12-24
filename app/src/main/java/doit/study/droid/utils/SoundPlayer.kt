package doit.study.droid.utils

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class SoundPlayer @Inject constructor(val appContext: Application): DefaultLifecycleObserver {
    private var mediaPlayer: MediaPlayer? = null
    private val volume = 0.04f // should be scaled logarithmically

    fun play(lifecycle: Lifecycle, filename: String) {
        lifecycle.addObserver(this)
        try {
            val afd = appContext.resources.assets.openFd(filename)
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setVolume(volume, volume)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                prepare()
                start()
                setOnCompletionListener {
                    Timber.d("mediaPlayer done")
                }
            }
        } catch (e: IOException) {
            Timber.e(e)
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        mediaPlayer?.release()
    }
}