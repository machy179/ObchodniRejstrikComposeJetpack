package com.machy1979.obchodnirejstrik.repository

import com.machy1979.obchodnirejstrik.data.ORDatabaseDao
import com.machy1979.obchodnirejstrik.model.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ORRepository @Inject constructor(private val orDatabaseDao: ORDatabaseDao) { //repository není povinná, ale zpřehlední nám to kód, když budeme chtít ukládat z ViewModelu do databáze, tak pojedeme přes níže uvedené fce
    //když ViewModel bude načítat data z tohoto repository, tak ho nezajímá, zda je to z netu nebo databáze, toto je mezivrstva mezi ViewModelem a v tomto případě databází
    suspend fun addQuery(query: Query) = orDatabaseDao.insert(query = query)
    suspend fun updateNote(query: Query) = orDatabaseDao.update(query)
    suspend fun deleteNote(query: Query) = orDatabaseDao.deleteNote(query)
    suspend fun deleteAllNotes() = orDatabaseDao.deleteAll()
    fun getAllNotes(): Flow<List<Query>> = orDatabaseDao.getQueries().flowOn(Dispatchers.IO)
        .conflate()

    suspend fun getNote(id: String): Query = orDatabaseDao.getNoteById(id = id)


}