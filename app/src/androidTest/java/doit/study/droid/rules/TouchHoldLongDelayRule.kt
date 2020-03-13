package com.myfitnesspal.android.testrules

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.IOException

class TouchHoldLongDelayRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                longPressTimeout()
                try {
                    base.evaluate()
                } finally {
                    shortPressTimeout()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun longPressTimeout() {
        uiDevice.executeShellCommand("$LONG_PRESS_TIMEOUT $LONG_DELAY_MS")
    }

    @Throws(IOException::class)
    private fun shortPressTimeout() {
        uiDevice.executeShellCommand("$LONG_PRESS_TIMEOUT $DEFAULT_SHORT_DELAY_MS")
    }

    companion object {
        private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        private const val LONG_DELAY_MS = 1_500
        private const val DEFAULT_SHORT_DELAY_MS = 500
        private const val LONG_PRESS_TIMEOUT = "settings put secure long_press_timeout"

    }
}