package doit.study.droid.views;

import android.content.Context;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import timber.log.Timber;

public class SnackBarAwareBehavior extends CoordinatorLayout.Behavior<View> {
    private static final boolean DEBUG = false;
    
    public SnackBarAwareBehavior(Context context, AttributeSet attrs) {
        super();
        if (DEBUG) Timber.d("ctor");
    }

    public SnackBarAwareBehavior() {
        super();
        if (DEBUG) Timber.d("ctor empty");
    }



    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        if (DEBUG) Timber.d("layoutDependsOn " + dependency.getClass());
        return super.layoutDependsOn(parent, child, dependency)
                //|| dependency instanceof NestedScrollView
                || dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (DEBUG) Timber.d("onDependentViewChanged");
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        //return super.onDependentViewChanged(parent, child, dependency);
        return true;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        if (DEBUG) Timber.d("onLayoutChild");
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        if (DEBUG) Timber.d("onInterceptTouchEvent");
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        if (DEBUG) Timber.d("onTouchEvent");
        return super.onTouchEvent(parent, child, ev);
    }

    @Override
    public boolean blocksInteractionBelow(CoordinatorLayout parent, View child) {
        if (DEBUG) Timber.d("blocksInteractionBelow");
        return super.blocksInteractionBelow(parent, child);
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, View child, View dependency) {
        if (DEBUG) Timber.d("onDependentViewRemoved");
        super.onDependentViewRemoved(parent, child, dependency);
    }

    @Override
    public boolean isDirty(CoordinatorLayout parent, View child) {
        if (DEBUG) Timber.d("isDirty");
        return super.isDirty(parent, child);
    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if (DEBUG) Timber.d("onMeasureChild");
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        if (DEBUG) Timber.d("onStartNestedScroll");
//        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
        return true;
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        if (DEBUG) Timber.d("onNestedScrollAccepted");
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        if (DEBUG) Timber.d("onStopNestedScroll " + target.getClass());
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (DEBUG) Timber.d("onNestedScroll");
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
        if (DEBUG) Timber.d("onNestedPreScroll");
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY, boolean consumed) {
        if (DEBUG) Timber.d("onNestedFling");
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, View child, View target, float velocityX, float velocityY) {
        if (DEBUG) Timber.d("onNestedPreFling");
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public WindowInsetsCompat onApplyWindowInsets(CoordinatorLayout coordinatorLayout, View child, WindowInsetsCompat insets) {
        if (DEBUG) Timber.d("onApplyWindowInsets");
        return super.onApplyWindowInsets(coordinatorLayout, child, insets);
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, View child, Parcelable state) {
        if (DEBUG) Timber.d("onRestoreInstanceState");
        super.onRestoreInstanceState(parent, child, state);
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, View child) {
        if (DEBUG) Timber.d("onSaveInstanceState");
        return super.onSaveInstanceState(parent, child);
    }
}