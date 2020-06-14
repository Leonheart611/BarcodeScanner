package dynamia.com.core.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dynamia.com.core.R

abstract class BaseFragment : Fragment() {
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { context ->
            loadingDialog = Dialog(context)
            with(loadingDialog) {
                setContentView(R.layout.loading_layout)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window
                    ?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                setCanceledOnTouchOutside(false)
            }
        }
    }

    fun showLoading(show: Boolean) {
        if (show)
            loadingDialog.show()
        else
            loadingDialog.dismiss()
    }
}
