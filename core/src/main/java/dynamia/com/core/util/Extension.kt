package dynamia.com.core.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import java.text.SimpleDateFormat
import java.util.*


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

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm:ss")
    return sdf.format(Date())
}

fun getDocumentCode(): String {
    val time = SimpleDateFormat("HHmmss")
    val date = SimpleDateFormat("ddMMyyyy")
    return "${date.format(Date())}${time.format(Date())}"
}

fun getNormalDate():String{
    val sdf = SimpleDateFormat("dd-MM-yyyy")
    return sdf.format(Date())
}

fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    return sdf.format(Date())
}

fun String.removeSpecialChart(char: String): String {
    return this.removePrefix(char)
}

fun String.checkFirstCharacter(char: String): String {
    return if (this[0].equals('k', true)) {
        this.replaceFirst(char, "", true)
    } else {
        this
    }
}

fun String.emptySetZero(): String {
    return if (this.isEmpty()) {
        "0"
    } else {
        this
    }
}

fun View.crossFade(animationDuration: Long, hide: View) {
    this.apply {
        alpha = 0f
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .setDuration(animationDuration)
            .setListener(null)
    }
    hide.animate()
        .alpha(0f)
        .setDuration(animationDuration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                hide.visibility = View.GONE
            }
        })
}

fun View.gone() {
    this.visibility = View.GONE
}

fun SharedPreferences.getUserName(): String {
    return this.getString(Constant.USERNAME_KEY, "") ?: ""
}

fun SharedPreferences.getPassword(): String {
    return this.getString(Constant.PASSWORD_KEY, "") ?: ""
}

fun SharedPreferences.getDomain(): String {
    return this.getString(Constant.DOMAIN_KEY, "") ?: ""
}

fun SharedPreferences.getBaseUrl(): String {
    return this.getString(Constant.BASEURL_KEY, "") ?: ""
}