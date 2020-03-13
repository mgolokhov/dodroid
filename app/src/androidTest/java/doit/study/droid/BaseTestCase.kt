package doit.study.droid

import com.myfitnesspal.android.testrules.DisableAnimationsRule
import com.myfitnesspal.android.testrules.DisableVirtualKeyboardRule
import com.myfitnesspal.android.testrules.ScreenshotTakingRule
import com.myfitnesspal.android.testrules.TouchHoldLongDelayRule
import org.junit.Rule

open class BaseTestCase {
    @get:Rule
    val screenshotTakingRule = ScreenshotTakingRule()
    @get:Rule
    val disableAnimationsRule = DisableAnimationsRule()
    @get:Rule
    val touchHoldLongDelayRule = TouchHoldLongDelayRule()
    @get:Rule
    val disableVirtualKeyboardRule = DisableVirtualKeyboardRule()
}