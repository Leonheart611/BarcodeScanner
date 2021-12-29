package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import dynamia.com.core.data.dao.UserDao
import dynamia.com.core.data.model.UserData

interface UserRepository {
	fun getUserData(): LiveData<UserData?>
	suspend fun insertUserData(data: UserData)
	suspend fun clearUserData()
}


class UserRepositoryImpl(val dao: UserDao) : UserRepository {
	override fun getUserData(): LiveData<UserData?> =
		dao.getUserData()
	
	override suspend fun insertUserData(data: UserData) {
		dao.insertUserData(data)
	}
	
	override suspend fun clearUserData() {
		dao.clearUserData()
	}
}