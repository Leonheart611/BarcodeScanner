package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dynamia.com.core.data.entinty.UserData

@Dao
interface UserDao {
    @Query("SELECT * FROM user LIMIT 1")
    fun getUserData(): UserData

    @Query("SELECT * FROM user where id =:id")
    fun getUserDataBy(id: Int): UserData?

    @Query("SELECT * FROM user")
    fun getAllUser(): LiveData<List<UserData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserData(data: UserData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUserData(data: UserData)

    @Delete(entity = UserData::class)
    fun deleteUserData(data: UserData)

    @Query("DELETE FROM user")
    fun clearUserData()

}