package dynamia.com.core.data.repository

import androidx.lifecycle.LiveData
import dynamia.com.core.data.dao.UserDao
import dynamia.com.core.data.entinty.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface UserRepository {
    suspend fun getUserData(): Flow<UserData?>
    fun getAllUserData(): LiveData<List<UserData>>
    suspend fun insertUserData(data: UserData)
    suspend fun updateUserData(data: UserData)
    suspend fun deleteUserData(data: UserData): Boolean
    suspend fun clearUserData()
}


class UserRepositoryImpl @Inject constructor(val dao: UserDao) : UserRepository {
    override suspend fun getUserData(): Flow<UserData?> = flow {
        emit(dao.getUserData())
    }

    override fun getAllUserData(): LiveData<List<UserData>> = dao.getAllUser()

    override suspend fun insertUserData(data: UserData) {
        data.id?.let {
            if (dao.getUserDataBy(it) != null)
                dao.updateUserData(data)
        } ?: run {
            dao.insertUserData(data)
        }

    }

    override suspend fun updateUserData(data: UserData) {
        dao.updateUserData(data)
    }

    override suspend fun deleteUserData(data: UserData): Boolean {
        return try {
            dao.deleteUserData(data)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun clearUserData() {
        dao.clearUserData()
    }
}