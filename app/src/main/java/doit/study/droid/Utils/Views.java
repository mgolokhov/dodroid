package doit.study.droid.utils;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

public final class Views {
    private Views(){
        // No instances.
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in setId(int).
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    @SuppressWarnings("unused")
    public static void printViewCoordinates(String s, View v){
        Rect myViewRect = new Rect();
        v.getGlobalVisibleRect(myViewRect);
        float x = myViewRect.left;
        float y = myViewRect.top;
        int [] screen = new int [2];
        int [] window = new int [2];
        v.getLocationOnScreen(screen);
        v.getLocationInWindow(window);
        Context context = v.getContext();
        Timber.d("Screen size: w:%d h:%d", getScreenWidth(context), getScreenHeight(context));
        Timber.d("%s:: abs screen: %s; abs win: %s; left, top:  %d %d; right, bottom: %d %d; width %d; height %d",
                s,
                Arrays.toString(screen),
                Arrays.toString(window),
                v.getLeft(),
                v.getTop(),
                v.getRight(),
                v.getBottom(),
                v.getWidth(),
                v.getHeight());
    }

    public static int getScreenWidth(Context context){
        return  context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context){
        return  context.getResources().getDisplayMetrics().heightPixels;
    }
}
