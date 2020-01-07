# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/max/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Crashlytics
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
# okhttp3
-dontwarn org.conscrypt.**

# Remove Android Log's methods
# This will strip `Log.v`, `Log.d`, and `Log.i` statements and will leave `Log.w` and `Log.e` statements intact.
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static *** d(...);
    public static *** i(...);
    public static *** v(...);
}

# Remove log methods which CrashlyticsTree doesn't support
-assumenosideeffects class timber.log.Timber {
    public static *** d(...);
    public static *** i(...);
    public static *** v(...);
}

# data classes for room
-keepclassmembers class doit.study.droid.data.local.entity.** { <fields>; }
# retrofit classes use @SerializedName for now

#-printconfiguration proguard-merged-config.txt