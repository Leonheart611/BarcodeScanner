package dynamia.com.barcodescanner.util

import android.animation.AnimatorListenerAdapter
import android.view.View


fun View.rotateFab(rotate: Boolean): Boolean {
    this.animate().setDuration(200)
        .setListener(object : AnimatorListenerAdapter() {
        })
        .rotation(if (rotate) 135f else 0f)
    return rotate
}