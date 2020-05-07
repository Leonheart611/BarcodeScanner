package dynamia.com.barcodescanner.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import dynamia.com.barcodescanner.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent).addOnSuccessListener {pendingDynamicLinkData->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.d(javaClass.name, "getDynamicLink:onSuccess $deepLink")
                }
            }.addOnFailureListener{
                Log.e(javaClass.name, "getDynamicLink:onFailure", it)
            }
    }
}
