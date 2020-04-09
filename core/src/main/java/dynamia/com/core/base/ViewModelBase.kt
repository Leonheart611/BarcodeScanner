package dynamia.com.core.base

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dynamia.com.core.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


abstract class ViewModelBase(private val sharedPreferences: SharedPreferences) : ViewModel() {
    val coroutineJob = Job()
    val coroutineContext = Dispatchers.IO + coroutineJob
    val uiScope = CoroutineScope(coroutineContext)

    fun saveLoginVariable(
        hostname: String,
        username: String,
        password: String,
        employee: String
    ): Boolean {
        return with(sharedPreferences.edit()) {
            putString(Constant.EMPLOYEE_SHARED_PREFERENCES, employee)
            putString(Constant.HOST_DOMAIN_SHARED_PREFERENCES, hostname)
            putString(Constant.PASSWORD_SHARED_PREFERENCES, password)
            putString(Constant.USERNAME_SHARED_PREFERENCES, username)
            commit()
        }
    }

    fun getEmployeeName(): String? {
        return sharedPreferences.getString(Constant.EMPLOYEE_SHARED_PREFERENCES, "")
    }

    fun checkLoginVariables():Boolean {
       with(sharedPreferences){
          return this.getString(Constant.HOST_DOMAIN_SHARED_PREFERENCES,"").equals("").not()
       }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }
}