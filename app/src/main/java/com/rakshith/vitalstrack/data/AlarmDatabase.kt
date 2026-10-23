package com.rakshith.vitalstrack.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val priority: String,
    val message: String,
    val timestamp: Long
)

@Dao
interface AlarmDao {
    @Insert
    suspend fun insert(alarm: AlarmEntity)

    @Query("SELECT * FROM alarms ORDER BY timestamp DESC LIMIT 50")
    fun recent(): Flow<List<AlarmEntity>>
}

@Database(entities = [AlarmEntity::class], version = 1, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile private var instance: AlarmDatabase? = null

        fun get(context: Context): AlarmDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AlarmDatabase::class.java,
                    "vitals.db"
                ).build().also { instance = it }
            }
    }
}
