// app/src/main/java/com/landroid/features/alerts/data/db/AlertDatabase.kt
package com.landroid.features.alerts.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.landroid.shared.models.Alert
import com.landroid.shared.models.AlertCategory

@Database(entities = [Alert::class], version = 1, exportSchema = false)
@TypeConverters(AlertConverters::class)
abstract class AlertDatabase : RoomDatabase() {
    abstract fun alertDao(): AlertDao

    companion object {
        @Volatile private var INSTANCE: AlertDatabase? = null

        fun getInstance(context: Context): AlertDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AlertDatabase::class.java,
                    "landroid_alerts.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}

class AlertConverters {
    @TypeConverter fun fromCategory(value: AlertCategory): String = value.name
    @TypeConverter fun toCategory(value: String): AlertCategory =
        AlertCategory.entries.firstOrNull { it.name == value } ?: AlertCategory.INSIGHT
}
