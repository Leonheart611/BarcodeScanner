package dynamia.com.core.util

import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import java.text.SimpleDateFormat
import java.util.*


fun View.rotateFab(rotate: Boolean): Boolean {
    this.animate().setDuration(200)
        .setListener(object : AnimatorListenerAdapter() {
        })
        .rotation(if (rotate) 135f else 0f)
    return rotate
}

fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showShortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun Context.readJsonAsset(fileName: String): String {
    val inputStream = assets.open(fileName)
    val size = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    return String(buffer, Charsets.UTF_8)
}

fun String.toNormalDate(): String {
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(parser.parse(this))
}

fun Context.getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm:ss")
    return sdf.format(Date())
}

fun Context.getCurrentDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    return sdf.format(Date())
}

fun String.removeSpecialChart(char: String): String {
    return this.removePrefix(char)
}

fun String.emptySetZero(): String {
    return if (this.isEmpty()) {
        "0"
    } else {
        this
    }
}
