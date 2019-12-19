package doit.study.droid.data.local

import android.app.Application
import android.content.res.AssetManager
import doit.study.droid.data.Outcome
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundRepository @Inject constructor(appContext: Application) {
    private var assetManager: AssetManager = appContext.resources.assets
    private var cachedFilenamesForSuccess: List<String> = emptyList()
    private var cachedFilenamesForFailure: List<String> = emptyList()

    fun getRandomSoundFileForSuccess(): Outcome<String> {
        return getSoundFile(ASSET_FOLDER_FOR_SUCCESS_SOUNDS)
    }

    fun getRandomSoundFileForFailure(): Outcome<String> {
        return getSoundFile(ASSET_FOLDER_FOR_FAILURE_SOUNDS)
    }

    private fun getSoundFile(
            assetFolder: String
    ): Outcome<String> {
        var cachedFilenames = getCache(assetFolder)
        return try {
            if (cachedFilenames.isEmpty()) {
                @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                cachedFilenames = assetManager
                        .list(assetFolder)
                        .asList()
            }
            val path = File(assetFolder, cachedFilenames.random()).path
            Outcome.Success(path)
        } catch (e: Exception) {
            Timber.e(e)
            Outcome.Error(e)
        }
    }

    private fun getCache(assetFolder: String): List<String> {
        return when (assetFolder) {
            ASSET_FOLDER_FOR_SUCCESS_SOUNDS -> { cachedFilenamesForSuccess }
            ASSET_FOLDER_FOR_FAILURE_SOUNDS -> { cachedFilenamesForFailure }
            else -> { emptyList() }
        }
    }

    companion object {
        private const val ASSET_FOLDER_FOR_SUCCESS_SOUNDS = "right"
        private const val ASSET_FOLDER_FOR_FAILURE_SOUNDS = "wrong"
    }
}