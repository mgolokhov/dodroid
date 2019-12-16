package doit.study.droid.utils.timber

import timber.log.Timber

class LogcatTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return """MYNSA:${super.createStackElementTag(element)}:${element.lineNumber}"""
    }
}