package rawcomposition.bibletools.info.di

import android.content.Context
import dagger.Module
import dagger.Provides
import rawcomposition.bibletools.info.BibleToolsApp
import rawcomposition.bibletools.info.data.db.BibleToolsDb
import rawcomposition.bibletools.info.data.prefs.AppPrefs
import rawcomposition.bibletools.info.data.prefs.AppPrefsImpl
import rawcomposition.bibletools.info.data.repository.ReferencesRepository
import rawcomposition.bibletools.info.data.repository.ReferencesRepositoryImpl
import rawcomposition.bibletools.info.data.retrofit.BibleToolsApi
import rawcomposition.bibletools.info.data.retrofit.RestClient
import rawcomposition.bibletools.info.utils.RxSchedulers
import javax.inject.Singleton

@Module
internal class BibleToolsModule {

    @Provides
    @Singleton
    fun provideContext(app: BibleToolsApp): Context = app

    @Provides
    @Singleton
    fun provideApi(): BibleToolsApi = RestClient.createService(BibleToolsApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(context: Context): BibleToolsDb = BibleToolsDb.create(context)

    @Provides
    @Singleton
    fun provideRxSchedulers(): RxSchedulers = RxSchedulers()

    @Provides
    @Singleton
    fun providePreferences(context: Context): AppPrefs = AppPrefsImpl(context)

    @Provides
    @Singleton
    fun provideRepository(context: Context, api: BibleToolsApi, db: BibleToolsDb, rxSchedulers: RxSchedulers, appPrefs: AppPrefs):
            ReferencesRepository = ReferencesRepositoryImpl(api, context, db, rxSchedulers, appPrefs)
}