package app.suuvi.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SouvenirCollection::class, Souvenir::class, Asset::class],
    version = 1,
    exportSchema = false,
)
abstract class SuuviDatabase : RoomDatabase() {
    abstract fun dao(): SuuviDao
}
