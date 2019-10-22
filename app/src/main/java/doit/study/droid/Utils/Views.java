package doit.study.droid.utils;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import doit.study.droid.R;
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


    public static final class CustomToast{

        /** @hide */
        @IntDef({LENGTH_SHORT, LENGTH_LONG})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Duration {}

        /**
         * Show the view or text notification for a short period of time.  This time
         * could be user-definable.  This is the default.
         * @see Toast#setDuration
         */
        public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;

        /**
         * Show the view or text notification for a long period of time.  This time
         * could be user-definable.
         * @see Toast#setDuration
         */
        public static final int LENGTH_LONG = Toast.LENGTH_LONG;


        /** @hide */
        @IntDef({CENTER, BOTTOM})
        @Retention(RetentionPolicy.SOURCE)
        public @interface Attraction {}

        public static final int CENTER = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        public static final int BOTTOM = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;


        private CustomToast() {}

        private static Toast showCustomToast(Context context, @LayoutRes int layoutId, @IdRes int textId,
                                             String text, @Attraction int gravity, @Duration int duration){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customView = layoutInflater.inflate(layoutId, null);
            TextView textView = (TextView) customView.findViewById(textId);
            textView.setText(text);
            Toast customToast = new Toast(context);
            customToast.setView(customView);
            customToast.setGravity(gravity, 0, 0);
            customToast.setDuration(duration);
            customToast.show();
            return customToast;
        }

        public static Toast showToastError(Context context, String str, @Attraction int gravity, @Duration int duration) {
            return showCustomToast(context, R.layout.toast_wrong, R.id.errorToast, str, gravity, duration);
        }

        public static Toast showToastError(Context context, String str, int gravity){
            return showToastError(context, str, gravity, LENGTH_SHORT);
        }

        public static Toast showToastError(Context context, String str){
            return showToastError(context, str, CENTER);
        }

        public static Toast showToastSuccess(Context context, String str, @Attraction int gravity, @Duration int duration) {
            return showCustomToast(context, R.layout.toast_right, R.id.okToast, str, gravity, duration);
        }

        public static Toast showToastSuccess(Context context, String str, int gravity) {
            return showToastSuccess(context, str, gravity, LENGTH_SHORT);
        }

        public static Toast showToastSuccess(Context context, String str){
            return showToastSuccess(context, str, CENTER);
        }

    }
}
