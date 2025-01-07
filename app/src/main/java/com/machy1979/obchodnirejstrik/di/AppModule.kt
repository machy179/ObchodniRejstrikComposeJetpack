package com.machy1979.obchodnirejstrik.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.machy1979.obchodnirejstrik.data.ORDatabase
import com.machy1979.obchodnirejstrik.data.ORDatabaseDao
import com.machy1979.obchodnirejstrik.network.AresApiService
import com.machy1979.obchodnirejstrik.repository.AresRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    //zde děláme instance závislostí pro Hilt - DI
    @Singleton
    @Provides
    fun provideNotesDao(orDatabase: ORDatabase): ORDatabaseDao = orDatabase.orDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): ORDatabase = Room.databaseBuilder(
        context,
        ORDatabase::class.java,
        "or_db"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("billings_prefs", Context.MODE_PRIVATE)
    }

    //retrofit:
    @Provides
    @Singleton
    fun provideAresApiService(): AresApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://ares.gov.cz/")
            .build()

        return retrofit.create(AresApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAresRepository(apiService: AresApiService): AresRepository {
        return AresRepository(apiService)
    }




}