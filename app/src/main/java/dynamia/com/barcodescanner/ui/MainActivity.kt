package dynamia.com.barcodescanner.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R

@AndroidEntryPoint
class
MainActivity : AppCompatActivity() {
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Log.e("buildConfig", BuildConfig.FLAVOR)
        this.let { context ->
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
