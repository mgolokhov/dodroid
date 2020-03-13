package com.myfitnesspal.android.testrules

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import java.io.IOException
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DisableAnimationsRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                disableAnimations()
                try {
                    base.evaluate()
                } finally {
                    enableAnimations()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun disableAnimations() {
        uiDevice.executeShellCommand("$TRANSITION_ANIMATION_SCALE $DISABLED")
        uiDevice.executeShellCommand("$WINDOW_ANIMATION_SCALE $DISABLED")
        uiDevice.executeShellCommand("$ANIMATOR_DURATION_SCALE $DISABLED")
    }

    @Throws(IOException::class)
    private fun enableAnimations() {
        uiDevice.executeShellCommand("$TRANSITION_ANIMATION_SCALE $ENABLED")
        uiDevice.executeShellCommand("$WINDOW_ANIMATION_SCALE $ENABLED")
        uiDevice.executeShellCommand("$ANIMATOR_DURATION_SCALE $ENABLED")
    }

    companion object {
        private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        private const val DISABLED = 0
        private const val ENABLED = 1
        private const val TRANSITION_ANIMATION_SCALE = "settings put global transition_animation_scale"
        private const val WINDOW_ANIMATION_SCALE = "settings put global window_animation_scale"
        private const val ANIMATOR_DURATION_SCALE = "settings put global animator_duration_scale"
    }
}
