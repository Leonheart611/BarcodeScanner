package dynamia.com.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.core.data.model.UserData

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getUserData(): UserData

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserData(data: UserData)

    @Query("DELETE FROM user")
    fun clearUserData()

}