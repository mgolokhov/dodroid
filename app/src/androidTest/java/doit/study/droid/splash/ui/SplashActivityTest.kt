package doit.study.droid.splash.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import doit.study.droid.BaseTestCase

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashActivityTest : BaseTestCase() {
    @get:Rule
    var activityTestRule = ActivityTestRule(
            SplashActivity::class.java, false, true
    )

    @Test
    fun stubTestToVerifyRulesRollback() {
        try {
            Thread.sleep(10_000)
        } catch (ee:InterruptedException) {
            ee.printStackTrace()
        }
    }

}