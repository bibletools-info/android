package rawcomposition.bibletools.info.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import rawcomposition.bibletools.info.data.db.dao.ReferenceDao
import rawcomposition.bibletools.info.data.model.Reference

@Database(entities = [(Reference::class)], exportSchema = false, version = 3)
@TypeConverters(DataTypeConverters::class)
abstract class BibleToolsDb : RoomDatabase() {

    abstract fun referencesDao(): ReferenceDao

    companion object {
        private const val DATABASE_NAME = "bible_tools_db"

        fun create(context: Context): BibleToolsDb = Room.databaseBuilder(context, BibleToolsDb::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}