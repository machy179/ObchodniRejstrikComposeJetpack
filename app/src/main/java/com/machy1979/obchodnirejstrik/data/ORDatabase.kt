package com.machy1979.obchodnirejstrik.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.machy1979.obchodnirejstrik.model.Query

@Database(entities = [Query::class], version = 2, exportSchema = false)
abstract class ORDatabase: RoomDatabase() {
    abstract fun orDao(): ORDatabaseDao
}