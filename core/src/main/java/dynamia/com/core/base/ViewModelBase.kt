package dynamia.com.core.base

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dynamia.com.core.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


abstract class ViewModelBase(private val sharedPreferences: SharedPreferences) : ViewModel() {
    val coroutineJob = Job()
    val coroutineContext = Dispatchers.IO + coroutineJob
    val uiScope = CoroutineScope(coroutineContext)
    var gson: Gson = GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create()


    fun saveLoginVariable(
        hostname: String,
        username: String,
        password: String,
        employee: String
    ) {
        with(sharedPreferences.edit()) {
            putString(Constant.EMPLOYEE_SHARED_PREFERENCES, employee).apply()
            putString(Constant.HOST_DOMAIN_SHARED_PREFERENCES, hostname).apply()
            putString(Constant.PASSWORD_SHARED_PREFERENCES, password).apply()
            putString(Constant.USERNAME_SHARED_PREFERENCES, username).apply()
        }
    }

    fun getEmployeeName(): String {
        return sharedPreferences.getString(Constant.EMPLOYEE_SHARED_PREFERENCES, "") ?: ""
    }

    fun checkLoginVariables(): Boolean {
        with(sharedPreferences) {
            val hostDomain = this.getString(Constant.HOST_DOMAIN_SHARED_PREFERENCES, "")
            Log.e("HostDomain", hostDomain)
            return !hostDomain.isNullOrEmpty()
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }
}