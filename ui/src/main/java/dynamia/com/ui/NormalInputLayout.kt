package dynamia.com.ui

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.normal_input_layout.view.*


class NormalInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr){
    init {
        val wrapContent = LayoutParams.WRAP_CONTENT
        val matchParent = LayoutParams.MATCH_PARENT
        layoutParams = LayoutParams(matchParent, wrapContent)
        LayoutInflater.from(context).inflate(R.layout.normal_input_layout, this, true)
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.NormalInputLayout, 0, 0)
            val title = resources.getText(typedArray
                .getResourceId(R.styleable.NormalInputLayout_title, 0))
            tv_input_layout.text = title
            when(resources.getText(typedArray.getResourceId(R.styleable.NormalInputLayout_inputType,0))){
                "number"->{
                    et_input_layout.inputType = InputType.TYPE_CLASS_NUMBER
                }
                else->{
                    et_input_layout.inputType = InputType.TYPE_CLASS_TEXT
                }
            }
            typedArray.recycle()
        }
    }

    fun getTextAsString():String{
        return et_input_layout.text.toString()
    }

    fun setText(value:String){
        et_input_layout.setText(value)
    }

}