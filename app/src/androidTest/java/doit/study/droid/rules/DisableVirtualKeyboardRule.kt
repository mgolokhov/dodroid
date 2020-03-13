package com.myfitnesspal.android.testrules

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import java.io.IOException
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class DisableVirtualKeyboardRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                disableVirtualKeyboard()
                try {
                    base.evaluate()
                } finally {
                    enableVirtualKeyboard()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun disableVirtualKeyboard() {
        uiDevice.executeShellCommand("$SHOW_IME_WITH_HARD_KEYBOARD $DISABLED")
    }

    @Throws(IOException::class)
    private fun enableVirtualKeyboard() {
        uiDevice.executeShellCommand("$SHOW_IME_WITH_HARD_KEYBOARD $ENABLED")
    }

    companion object {
        private val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        private const val DISABLED = 0
        private const val ENABLED = 1
        private const val SHOW_IME_WITH_HARD_KEYBOARD = "settings put secure show_ime_with_hard_keyboard"
    }
}
