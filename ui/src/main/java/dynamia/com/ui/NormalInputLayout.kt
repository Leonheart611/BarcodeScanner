package dynamia.com.ui

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import kotlinx.android.synthetic.main.normal_input_layout.view.*


class NormalInputLayout : LinearLayoutCompat{

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
            val typedArray = ctx.obtainStyledAttributes(it, R.styleable.NormalInputLayout, 0, 0)
            val title = resources.getText(typedArray
                .getResourceId(R.styleable.NormalInputLayout_title, 0))
            tv_input_layout.text = title
            typedArray.recycle()
        }
    }

    fun getTextAsString():String{
        return et_input_layout.text.toString()
    }

    fun setText(value:String?){
        et_input_layout.setText(value)
    }

}