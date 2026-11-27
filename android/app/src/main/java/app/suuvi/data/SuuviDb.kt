package app.suuvi.data

import android.content.Context
import androidx.room.Room

/** Acesso unico ao banco local. Antes do lancamento nao ha dados a preservar, entao migracao destrutiva. */
object SuuviDb {
    @Volatile private var instance: SuuviDatabase? = null

    fun get(context: Context): SuuviDatabase = instance ?: synchronized(this) {
        instance ?: Room.databaseBuilder(context.applicationContext, SuuviDatabase::class.java, "suuvi.db")
            .fallbackToDestructiveMigration(true)
            .build().also { instance = it }
    }
}
