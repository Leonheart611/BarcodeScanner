package dynamia.com.core.base

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dynamia.com.core.data.model.UserData
import dynamia.com.core.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


abstract class ViewModelBase(private val userRepository: UserRepository) : ViewModel() {
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
        userRepository.insertUserData(
            UserData(
                hostName = hostname,
                username = username,
                employeeCode = employee,
                password = password
            )
        )
    }

    fun getEmployeeName(): String {
        return userRepository.getUserData().employeeCode
    }

    fun checkLoginVariables(): Boolean {
        val userData = userRepository.getUserData()
        if (userData != null) {
            with(userData) {
                Log.e("HostDomain", hostName)
                return !hostName.isNullOrEmpty()
            }
        } else {
            return false
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }
}