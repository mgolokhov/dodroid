package doit.study.droid.utils

import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import doit.study.droid.R

@IntDef(Toast.LENGTH_SHORT, Toast.LENGTH_LONG)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Duration

@IntDef(CENTER, BOTTOM)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Attraction

const val CENTER = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL
const val BOTTOM = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL


fun Fragment.showToast(
        @LayoutRes layoutId: Int,
        message: String,
        @Attraction gravity: Int = CENTER,
        @Duration duration: Int = Toast.LENGTH_SHORT
): Toast {
    return Toast(context).apply {
        view = LayoutInflater.from(context).inflate(layoutId, null)
        view.findViewById<TextView>(R.id.message).text = message
        setGravity(gravity, 0, 0)
        setDuration(duration)
        show()
    }
}

fun Fragment.showToastFailure(message: String): Toast {
    return this.showToast(layoutId = R.layout.toast_wrong, message = message)
}

fun Fragment.showToastSuccess(message: String): Toast {
    return this.showToast(layoutId = R.layout.toast_right, message = message)
}
