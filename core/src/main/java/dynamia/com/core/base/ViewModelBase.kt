package dynamia.com.core.base

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dynamia.com.core.util.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


abstract class ViewModelBase(val userRepository: SharedPreferences) : ViewModel() {
    val coroutineJob = Job()
    val coroutineContext = Dispatchers.IO + coroutineJob
    val uiScope = CoroutineScope(coroutineContext)
    var gson: Gson = GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create()

    fun getEmployeeName(): String {
        return userRepository.getString(Constant.EMPLOYEE_KEY, "") ?: ""
    }

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }
}