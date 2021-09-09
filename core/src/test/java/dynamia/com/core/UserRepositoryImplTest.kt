package dynamia.com.core

import dynamia.com.core.data.dao.UserDao
import dynamia.com.core.data.entinty.UserData
import dynamia.com.core.data.repository.UserRepositoryImpl
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.text.Typography.times

class UserRepositoryImplTest {
    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()
    private val userDao: UserDao = spyk()
    private val userRepository = UserRepositoryImpl(userDao)

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `testing user insert data should inserted to database`() {
        testDispatcher.runBlockingTest {
            coEvery { userDao.getUserData() } returns DataHelper.dataUser
            var result: UserData? = null
            userRepository.getUserData().collect {
                result = it
            }
            coVerify { userDao.getUserData() }
            Assert.assertEquals(DataHelper.dataUser, result)
        }
    }

    @Test
    fun `testing user delete data should deleted from database`() {
        testDispatcher.runBlockingTest {
            coEvery { userDao.insertUserData(DataHelper.dataUser) }
            coEvery { userDao.clearUserData() }
            userRepository.insertUserData(DataHelper.dataUser)
            userRepository.clearUserData()
            coVerify { userDao.insertUserData(DataHelper.dataUser) }
            coVerify { userDao.clearUserData() }
        }
    }

}