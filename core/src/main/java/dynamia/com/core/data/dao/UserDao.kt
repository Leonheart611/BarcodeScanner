package dynamia.com.core.data.dao

import androidx.room.*
import dynamia.com.core.data.model.UserData

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getUserData(): UserData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserData(data: UserData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUserData(data: UserData)

    @Query("DELETE FROM user")
    fun clearUserData()

}