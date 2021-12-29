package dynamia.com.core.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dynamia.com.core.data.model.UserData

@Dao
interface UserDao {
	@Query("SELECT * FROM user where id = 1")
	fun getUserData(): LiveData<UserData?>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertUserData(data: UserData)
	
	@Query("DELETE FROM user")
	fun clearUserData()
	
}