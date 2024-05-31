package com.machy1979.obchodnirejstrik.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.machy1979.obchodnirejstrik.data.ORDatabase
import com.machy1979.obchodnirejstrik.data.ORDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    //zde děláme instance závislostí pro Hilt - DI
    @Singleton
    @Provides
    fun provideNotesDao(orDatabase: ORDatabase): ORDatabaseDao
            = orDatabase.orDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): ORDatabase
            = Room.databaseBuilder(
        context,
        ORDatabase::class.java,
        "or_db")
        .fallbackToDestructiveMigration()
        .build()


}