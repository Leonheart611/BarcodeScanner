package dynamia.com.core.data.repository

import dynamia.com.core.data.dao.UserDao
import dynamia.com.core.data.model.UserData
import kotlinx.coroutines.runBlocking

interface UserRepository {
    fun getUserData(): UserData
    fun insertUserData(data: UserData)
    fun clearUserData()

}


class UserRepositoryImpl(val dao: UserDao) : UserRepository {
    override fun getUserData(): UserData = dao.getUserData()

    override fun insertUserData(data: UserData) {
        runBlocking {
            dao.insertUserData(data)
        }
    }

    override fun clearUserData() {
        dao.clearUserData()
    }
}