package dynamia.com.barcodescanner.di

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import dynamia.com.core.util.Constant
import dynamia.com.core.util.getUserName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject

abstract class ViewModelBase(
        private val userRepository: SharedPreferences,
) : ViewModel() {
    private val coroutineJob = Job()
    val coroutineContext = Dispatchers.IO + coroutineJob
    var gson: Gson = GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

    fun getCompanyName(): String {
        return userRepository.getString(Constant.USERNAME_KEY, "") ?: ""
    }

    fun getUserName() = userRepository.getUserName()

    override fun onCleared() {
        super.onCleared()
        coroutineJob.cancel()
    }
}