package dynamia.com.core.data.repository

import dynamia.com.core.data.dao.UserDao
import dynamia.com.core.data.entinty.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface UserRepository {
    suspend fun getUserData(): Flow<UserData?>
    suspend fun insertUserData(data: UserData)
    suspend fun updateUserData(data: UserData)
    suspend fun clearUserData()
}


class UserRepositoryImpl @Inject constructor(val dao: UserDao) : UserRepository {
    override suspend fun getUserData(): Flow<UserData?> = flow {
        emit(dao.getUserData())
    }

    override suspend fun insertUserData(data: UserData) {
        dao.clearUserData()
        dao.insertUserData(data)
    }

    override suspend fun updateUserData(data: UserData) {
        dao.updateUserData(data)
    }

    override suspend fun clearUserData() {
        dao.clearUserData()
    }
}