package app.suuvi.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SouvenirCollection::class, Souvenir::class, Asset::class, SavedPiece::class],
    version = 2,
    exportSchema = false,
)
abstract class SuuviDatabase : RoomDatabase() {
    abstract fun dao(): SuuviDao
}
