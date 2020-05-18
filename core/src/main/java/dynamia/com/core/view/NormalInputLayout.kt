package dynamia.com.core.view

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import dynamia.com.core.R
import kotlinx.android.synthetic.main.normal_input_layout.view.*


class NormalInputLayout : LinearLayoutCompat {

    private var attrs: AttributeSet? = null
    private var defStyleAttr: Int = 0

    constructor(ctx: Context) : super(ctx)

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        this.attrs = attrs
        setAttributes(ctx)
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        ctx,
        attrs,
        defStyleAttr
    ) {
        this.attrs = attrs
        this.defStyleAttr = defStyleAttr
        setAttributes(ctx)
    }

    private fun setAttributes(ctx: Context) {
        val wrapContent = LayoutParams.WRAP_CONTENT
        val matchParent = LayoutParams.MATCH_PARENT
        layoutParams = LayoutParams(matchParent, wrapContent)
        LayoutInflater.from(ctx).inflate(R.layout.normal_input_layout, this, true)
        attrs?.let {
            ctx.obtainStyledAttributes(
                it,
                R.styleable.NormalInputLayout, 0, 0
            ).apply {
                try {
                    val title = getText(R.styleable.NormalInputLayout_title)
                    val enabled = getBoolean(R.styleable.NormalInputLayout_etenabled, true)
                    val focusable = getBoolean(R.styleable.NormalInputLayout_etfocusable, true)
                    if (enabled.not()) {
                        et_input_layout.isEnabled = enabled
                    }
                    if (focusable.not()) {
                        et_input_layout.isFocusable = focusable
                    }
                    tv_input_layout.text = title
                } catch (e: Exception) {
                    Log.e("error view text",e.localizedMessage)
                } finally {
                    this.recycle()
                }
            }

        }
    }

    fun getTextAsString(): String {
        return et_input_layout.text.toString()
    }

    fun setText(value: String?) {
        et_input_layout.setText(value)
    }

    fun isEmpty():Boolean{
        return et_input_layout.text.isEmpty()
    }

    fun addTextWatcher(textWatcher: TextWatcher){
        et_input_layout.addTextChangedListener(textWatcher)
    }

    fun getTextLength():Int{
        return et_input_layout.text.length
    }

    fun setError(message:String){
        et_input_layout.error = message
    }
    fun clearText(){
        et_input_layout.text.clear()
    }
}