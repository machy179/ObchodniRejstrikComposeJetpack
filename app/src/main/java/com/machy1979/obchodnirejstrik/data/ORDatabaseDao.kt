package com.machy1979.obchodnirejstrik.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ORDatabaseDao {
    @Query("SELECT * from queries_tbl")
    fun getQueries():
            Flow<List<com.machy1979.obchodnirejstrik.model.Query>>     //musíme tady mít Flow a ne MutableState, protože Flow je asynchronní...na room knihovnu se to doporučuje

    @Query("SELECT * from queries_tbl where id =:id")
    suspend fun getNoteById(id: String): com.machy1979.obchodnirejstrik.model.Query              //suspend, aby to bylo možné pozastavit ve vláknu

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(query: com.machy1979.obchodnirejstrik.model.Query)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(note: com.machy1979.obchodnirejstrik.model.Query)

    @Query("DELETE from queries_tbl")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteNote(note: com.machy1979.obchodnirejstrik.model.Query)
}