package app.suuvi.data

import android.content.Context
import androidx.room.Room

/** Acesso unico ao banco local. Antes do lancamento nao ha dados a preservar, entao migracao destrutiva. */
object SuuviDb {
    @Volatile private var instance: SuuviDatabase? = null

    fun get(context: Context): SuuviDatabase = instance ?: synchronized(this) {
        instance ?: Room.databaseBuilder(context.applicationContext, SuuviDatabase::class.java, "suuvi.db")
            .addMigrations(MIGRATION_3_4)
            .fallbackToDestructiveMigration(true)
            .build().also { instance = it }
    }
}

/** 3 -> 4: nota opcional nas pecas salvas (nao perde os dados). */
private val MIGRATION_3_4 = object : androidx.room.migration.Migration(3, 4) {
    override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE saved_pieces ADD COLUMN note TEXT")
    }
}
