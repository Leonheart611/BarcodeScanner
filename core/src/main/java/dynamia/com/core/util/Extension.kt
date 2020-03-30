package dynamia.com.core.util

import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import java.text.SimpleDateFormat


fun View.rotateFab(rotate: Boolean): Boolean {
    this.animate().setDuration(200)
        .setListener(object : AnimatorListenerAdapter() {
        })
        .rotation(if (rotate) 135f else 0f)
    return rotate
}

fun Context.showToast(message:String){
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
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

fun String.toNormalDate():String{
    val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val formatter = SimpleDateFormat("dd/MM/yyyy")
    return formatter.format(parser.parse(this))
}

fun TextView.setUnderlineText(text:String){
    val content = SpannableString(text)
    content.setSpan(UnderlineSpan(), 0, content.length, 0)
    this.text = content
}
