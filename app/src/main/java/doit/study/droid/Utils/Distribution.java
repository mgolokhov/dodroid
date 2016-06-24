package doit.study.droid.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Distribution {
    private static final Logger logger = Logger.getLogger(Distribution.class.getName());
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
            logger.log(Level.SEVERE, "PackageManager.NameNotFoundException exception in Distribution.getVersion(Context)", e);
            sVersion = "buggy";
        }
        return sVersion;
    }
}
