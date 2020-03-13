package com.myfitnesspal.android.testrules

import android.graphics.Bitmap
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.IOException

class ScreenshotTakingRule : TestWatcher() {

    override fun failed(e: Throwable?, desc: Description?) {
        try {
            captureScreenshot(desc?.methodName ?: "no_name")
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
    }

    /**
     * Takes screenshot and saves it in sdcard/Pictures/screenshots location.
     * Each screenshot will have name containing unique uuid.
     *
     * @param name screenshot name
     * @throws IOException if there is an IOException
     */
    @Throws(IOException::class)
    private fun captureScreenshot(name: String) {
        val capture = Screenshot.capture()
        capture.format = Bitmap.CompressFormat.PNG
        capture.name = name
        capture.process()
    }
}