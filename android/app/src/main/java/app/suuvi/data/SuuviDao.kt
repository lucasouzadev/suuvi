package app.suuvi.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import app.suuvi.domain.Numbering
import kotlinx.coroutines.flow.Flow

@Dao
interface SuuviDao {
    @Insert suspend fun insertCollection(c: SouvenirCollection)
    @Insert suspend fun insertSouvenir(s: Souvenir)
    @Insert suspend fun insertAsset(a: Asset)
    @Insert suspend fun insertSavedPiece(p: SavedPiece)

    @Query("SELECT * FROM saved_pieces WHERE folderId = :folderId ORDER BY number")
    fun savedPieces(folderId: String): Flow<List<SavedPiece>>

    @Query("SELECT * FROM saved_pieces ORDER BY createdAt DESC")
    fun allSavedPieces(): Flow<List<SavedPiece>>

    @Query("SELECT number FROM saved_pieces WHERE folderId = :folderId")
    suspend fun savedNumbers(folderId: String): List<Int>

    @Query("SELECT * FROM collections WHERE id = :id") suspend fun collection(id: String): SouvenirCollection?
    @Query("SELECT number FROM souvenirs WHERE collectionId = :id") suspend fun numbers(id: String): List<Int>
    @Query("UPDATE collections SET lastIssued = :n WHERE id = :id") suspend fun setLastIssued(id: String, n: Int)
    @Query("SELECT * FROM souvenirs WHERE collectionId = :id ORDER BY number DESC")
    fun souvenirs(id: String): Flow<List<Souvenir>>

    /** Reserva o proximo #n e atualiza o contador na mesma transacao. */
    @Transaction
    suspend fun issueNumber(collectionId: String): Int {
        val last = collection(collectionId)?.lastIssued ?: 0
        val n = Numbering.next(numbers(collectionId), last)
        setLastIssued(collectionId, n)
        return n
    }
}
