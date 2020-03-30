package dynamia.com.core.base

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dynamia.com.core.util.Constant


abstract class ViewModelBase(private val sharedPreferences: SharedPreferences):ViewModel(){

    fun saveLoginVariable(hostname:String,username:String,password:String,employee:String):Boolean{
        return with(sharedPreferences.edit()){
            putString(Constant.EMPLOYEE_SHARED_PREFERENCES,employee)
            putString(Constant.HOST_DOMAIN_SHARED_PREFERENCES,hostname)
            putString(Constant.PASSWORD_SHARED_PREFERENCES,password)
            putString(Constant.USERNAME_SHARED_PREFERENCES,username)
            commit()
        }
    }
    fun getEmployeeName():String?{
        return sharedPreferences.getString(Constant.EMPLOYEE_SHARED_PREFERENCES,"")
    }

    fun checkLoginVariables(){

    }
}