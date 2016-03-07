package doit.study.droid;

import android.content.Context;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class SnackBarAwareBehavior extends CoordinatorLayout.Behavior<View> {
    private static final boolean DEBUG = false;
    private final String LOG_TAG = "NSA " + getClass().getName();

    public SnackBarAwareBehavior(Context context, AttributeSet attrs) {
        super();
        if (DEBUG) Log.d(LOG_TAG, "ctor");
    }

    public SnackBarAwareBehavior() {
        super();
        if (DEBUG) Log.d(LOG_TAG, "ctor empty");
    }



    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        if (DEBUG) Log.d(LOG_TAG, "layoutDependsOn " + dependency.getClass());
        return super.layoutDependsOn(parent, child, dependency)
                //|| dependency instanceof NestedScrollView
                || dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (DEBUG) Log.d(LOG_TAG, "onDependentViewChanged");
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        //return super.onDependentViewChanged(parent, child, dependency);
        return true;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        if (DEBUG) Log.d(LOG_TAG, "onLayoutChild");
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        if (DEBUG) Log.d(LOG_TAG, "onInterceptTouchEvent");
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        if (DEBUG) Log.d(LOG_TAG, "onTouchEvent");
        return super.onTouchEvent(parent, child, ev);
    }

    @Override
    public boolean blocksInteractionBelow(CoordinatorLayout parent, View child) {
        if (DEBUG) Log.d(LOG_TAG, "blocksInteractionBelow");
        return super.blocksInteractionBelow(parent, child);
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
        if (DEBUG) Log.d(LOG_TAG, "onDependentViewRemoved");
        super.onDependentViewRemoved(parent, child, dependency);
    }

    @Override
    public boolean isDirty(CoordinatorLayout parent, View child) {
        if (DEBUG) Log.d(LOG_TAG, "isDirty");
        return super.isDirty(parent, child);
    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if (DEBUG) Log.d(LOG_TAG, "onMeasureChild");
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        if (DEBUG) Log.d(LOG_TAG, "onStartNestedScroll");
//        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
        return true;
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        if (DEBUG) Log.d(LOG_TAG, "onNestedScrollAccepted");
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        if (DEBUG) Log.d(LOG_TAG, "onStopNestedScroll " + target.getClass());
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (DEBUG) Log.d(LOG_TAG, "onNestedScroll");
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
//        if (dyConsumed > 0) {
//            // User scrolled down
//            child.animate()
//                    .alpha(0.0f)
//                    .translationY(child.getHeight())
//                    .setDuration(500);
//        } else if (dyConsumed < 0) {
//            // User scrolled up
//            child.animate()
//                    .alpha(1.0f)
//                    .translationY(0)
//                    .setDuration(500);
//        }
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        if (DEBUG) Log.d(LOG_TAG, "onNestedPreScroll");
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY, boolean consumed) {
        if (DEBUG) Log.d(LOG_TAG, "onNestedFling");
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        if (DEBUG) Log.d(LOG_TAG, "onNestedPreFling");
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public WindowInsetsCompat onApplyWindowInsets(CoordinatorLayout coordinatorLayout, View child, WindowInsetsCompat insets) {
        if (DEBUG) Log.d(LOG_TAG, "onApplyWindowInsets");
        return super.onApplyWindowInsets(coordinatorLayout, child, insets);
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, View child, Parcelable state) {
        if (DEBUG) Log.d(LOG_TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(parent, child, state);
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, View child) {
        if (DEBUG) Log.d(LOG_TAG, "onSaveInstanceState");
        return super.onSaveInstanceState(parent, child);
    }
}