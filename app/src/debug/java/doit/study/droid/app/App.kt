package doit.study.droid.app

import timber.log.Timber

class App : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(MyDebugTree())
        Timber.d("Debug mode with logging")
    }

    private class MyDebugTree : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String? {
            return "MYNSA:" + super.createStackElementTag(element) + ":" + element.lineNumber
        }
    }
}
