package doit.study.droid.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import timber.log.Timber;

public final class Distribution {
    private static String sVersion;

    private Distribution(){
    }

    public static String getVersion(Context context) {
        context.getApplicationContext();
        if (sVersion != null)
            return sVersion;

        try {
            sVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e(e, null);
            sVersion = "buggy";
        }
        return sVersion;
    }
}
