package com.machy1979.obchodnirejstrik.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
            .addConverterFactory(GsonConverterFactory.create()) // Přidání konvertoru pro JSON
        //    .client(client) // OkHttpClient
            .build()

        return retrofit.create(AresApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAresRepository(apiService: AresApiService): AresRepository {
        return AresRepository(apiService)
    }




}


//pokud bych chtěl logovat response ze serveru na úrovni klienta OkHttp, tak odkomentovat a přidat ho do provideAresApiService, ale protože je to stream odpovědi, tak
// se s to bude házet chybu v AresRespository v metodě getAresDataNazev v případě, že je odpověď 400 - hodí to catch při val rawResponse = apiService.getAresDataEkonomickeSubjektyByNazev(requestBody)
/*
val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)

        // Log the response body even if the response code is 400
        if (!response.isSuccessful) {
            val errorBody = response.body?.string()
            Log.e("HTTP_OR Response Error", "Code: ${response.code}, Body: $errorBody")
        }

        response
    }
    .build()*/
