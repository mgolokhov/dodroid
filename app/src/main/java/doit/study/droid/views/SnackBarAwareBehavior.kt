package doit.study.droid.views

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowInsetsCompat

import com.google.android.material.snackbar.Snackbar

import timber.log.Timber

class SnackBarAwareBehavior : CoordinatorLayout.Behavior<View> {

    constructor(context: Context, attrs: AttributeSet) : super() {
        if (DEBUG) Timber.d("ctor")
    }

    constructor() : super() {
        if (DEBUG) Timber.d("ctor empty")
    }


    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (DEBUG) Timber.d("layoutDependsOn " + dependency.javaClass)
        return (super.layoutDependsOn(parent, child, dependency)
                //|| dependency instanceof NestedScrollView
                || dependency is Snackbar.SnackbarLayout)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if (DEBUG) Timber.d("onDependentViewChanged")
        val translationY = Math.min(0f, dependency.translationY - dependency.height)
        child.translationY = translationY
        return true
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int): Boolean {
        if (DEBUG) Timber.d("onLayoutChild")
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: View, ev: MotionEvent): Boolean {
        if (DEBUG) Timber.d("onInterceptTouchEvent")
        return super.onInterceptTouchEvent(parent, child, ev)
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: View, ev: MotionEvent): Boolean {
        if (DEBUG) Timber.d("onTouchEvent")
        return super.onTouchEvent(parent, child, ev)
    }

    override fun blocksInteractionBelow(parent: CoordinatorLayout, child: View): Boolean {
        if (DEBUG) Timber.d("blocksInteractionBelow")
        return super.blocksInteractionBelow(parent, child)
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        if (DEBUG) Timber.d("onDependentViewRemoved")
        super.onDependentViewRemoved(parent, child, dependency)
    }

    //    @Override
    //    public boolean isDirty(CoordinatorLayout parent, View child) {
    //        if (DEBUG) Timber.d("isDirty");
    //        return super.isDirty(parent, child);
    //    }

    override fun onMeasureChild(parent: CoordinatorLayout, child: View, parentWidthMeasureSpec: Int, widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int): Boolean {
        if (DEBUG) Timber.d("onMeasureChild")
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed)
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, nestedScrollAxes: Int): Boolean {
        if (DEBUG) Timber.d("onStartNestedScroll")
        return true
    }

    override fun onNestedScrollAccepted(coordinatorLayout: CoordinatorLayout, child: View, directTargetChild: View, target: View, nestedScrollAxes: Int) {
        if (DEBUG) Timber.d("onNestedScrollAccepted")
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes)
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View) {
        if (DEBUG) Timber.d("onStopNestedScroll " + target.javaClass)
        super.onStopNestedScroll(coordinatorLayout, child, target)
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
        if (DEBUG) Timber.d("onNestedScroll")
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, dx: Int, dy: Int, consumed: IntArray) {
        if (DEBUG) Timber.d("onNestedPreScroll")
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed)
    }

    override fun onNestedFling(coordinatorLayout: CoordinatorLayout, child: View, target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        if (DEBUG) Timber.d("onNestedFling")
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed)
    }

    override fun onNestedPreFling(coordinatorLayout: CoordinatorLayout, child: View, target: View, velocityX: Float, velocityY: Float): Boolean {
        if (DEBUG) Timber.d("onNestedPreFling")
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
    }

    override fun onApplyWindowInsets(coordinatorLayout: CoordinatorLayout, child: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        if (DEBUG) Timber.d("onApplyWindowInsets")
        return super.onApplyWindowInsets(coordinatorLayout, child, insets)
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout, child: View, state: Parcelable) {
        if (DEBUG) Timber.d("onRestoreInstanceState")
        super.onRestoreInstanceState(parent, child, state)
    }

    override fun onSaveInstanceState(parent: CoordinatorLayout, child: View): Parcelable? {
        if (DEBUG) Timber.d("onSaveInstanceState")
        return super.onSaveInstanceState(parent, child)
    }

    companion object {
        private const val DEBUG = false
    }
}