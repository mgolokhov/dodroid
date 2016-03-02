package doit.study.droid;

import android.content.Context;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {
    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
        Log.i("FAB", "ctor");
    }

    public ScrollAwareFABBehavior() {
        super();
        Log.i("FAB", "ctor empty");
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        Log.i("FAB", "layoutDependsOn " + dependency.getClass());
        return super.layoutDependsOn(parent, child, dependency) || dependency instanceof NestedScrollView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        Log.i("FAB", "onDependentViewChanged");
        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {
        Log.i("FAB", "onLayoutChild");
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, FloatingActionButton child, MotionEvent ev) {
        Log.i("FAB", "onInterceptTouchEvent");
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, FloatingActionButton child, MotionEvent ev) {
        Log.i("FAB", "onTouchEvent");
        return super.onTouchEvent(parent, child, ev);
    }

    @Override
    public boolean blocksInteractionBelow(CoordinatorLayout parent, FloatingActionButton child) {
        Log.i("FAB", "blocksInteractionBelow");
        return super.blocksInteractionBelow(parent, child);
    }

    @Override
    public void onDependentViewRemoved(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        Log.i("FAB", "onDependentViewRemoved");
        super.onDependentViewRemoved(parent, child, dependency);
    }

    @Override
    public boolean isDirty(CoordinatorLayout parent, FloatingActionButton child) {
        Log.i("FAB", "isDirty");
        return super.isDirty(parent, child);
    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, FloatingActionButton child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        Log.i("FAB", "onMeasureChild");
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        Log.i("FAB", "onStartNestedScroll");
//        if (child.getVisibility() == View.VISIBLE) {
//            child.hide();
////            return true;
//        }
//        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
        return true;
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        Log.i("FAB", "onNestedScrollAccepted");
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target) {
        Log.i("FAB", "onStopNestedScroll " + target.getClass());
        //if (child.getVisibility() == View.INVISIBLE)
            //child.show();

        //super.onStopNestedScroll(coordinatorLayout, child, target);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.i("FAB", "onNestedScroll");
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            child.hide();
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            child.show();
        }
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dx, int dy, int[] consumed) {
        Log.i("FAB", "onNestedPreScroll");
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, float velocityX, float velocityY, boolean consumed) {
        Log.i("FAB", "onNestedFling");
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, float velocityX, float velocityY) {
        Log.i("FAB", "onNestedPreFling");
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public WindowInsetsCompat onApplyWindowInsets(CoordinatorLayout coordinatorLayout, FloatingActionButton child, WindowInsetsCompat insets) {
        Log.i("FAB", "onApplyWindowInsets");
        return super.onApplyWindowInsets(coordinatorLayout, child, insets);
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, FloatingActionButton child, Parcelable state) {
        Log.i("FAB", "onRestoreInstanceState");
        super.onRestoreInstanceState(parent, child, state);
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, FloatingActionButton child) {
        Log.i("FAB", "onSaveInstanceState");
        return super.onSaveInstanceState(parent, child);
    }
}