package com.example

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.core.data.repository.*
import com.example.core.database.AppDatabase
import com.example.core.database.dao.BookingDao
import com.example.core.database.dao.UserDao
import com.example.core.domain.repository.*
import com.example.core.domain.usecase.GetFinalEstimatedFareUseCase
import com.example.core.domain.usecase.ValidateBookingDistanceUseCase
import com.example.core.config.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import timber.log.Timber

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cabkaro_preferences")

class AppContainer(private val context: Context) {
    // Firebase
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val firebaseStorage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }
    val firebaseMessaging: FirebaseMessaging by lazy { FirebaseMessaging.getInstance() }

    // Room Database
    val database: AppDatabase by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, "cabkaro_database")
            .fallbackToDestructiveMigration()
            .build()
    }
    val userDao: UserDao by lazy { database.userDao() }
    val bookingDao: BookingDao by lazy { database.bookingDao() }
    val configDao: ConfigDao by lazy { database.configDao() }

    // Network / Retrofit
    val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.cabkaro.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    // DataStore
    val dataStore: DataStore<Preferences> by lazy { context.dataStore }

    // Repositories
    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(firebaseAuth, userDao) }
    val bookingRepository: BookingRepository by lazy { BookingRepositoryImpl(firestore, bookingDao) }
    val vehicleRepository: VehicleRepository by lazy { VehicleRepositoryImpl() }
    val settingsRepository: SettingsRepository by lazy { SettingsRepositoryImpl(dataStore) }
    val mapRepository: MapRepository by lazy { MapRepositoryImpl() }
    val historyRepository: HistoryRepository by lazy { HistoryRepositoryImpl(bookingDao) }
    val adminConfigRepository: AdminConfigRepository by lazy { AdminConfigRepositoryImpl(firestore) }
    val appConfigRepository: AppConfigRepository by lazy { AppConfigRepositoryImpl(firestore, configDao) }

    // Use Cases
    val validateBookingDistanceUseCase by lazy { ValidateBookingDistanceUseCase() }
    val getFinalEstimatedFareUseCase by lazy { GetFinalEstimatedFareUseCase(adminConfigRepository, validateBookingDistanceUseCase) }
}

class CabkaroApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        container = AppContainer(this)
        
        // Initialize the singleton ConfigManager on App Startup
        ConfigManager.initialize(container.appConfigRepository)
    }
}

