package app.suuvi.services

import android.content.Context
import app.suuvi.data.SavedPiece
import app.suuvi.data.SuuviDao
import app.suuvi.domain.PieceRules

/** Contador do ultimo #n emitido por colecao (nunca baixa ao apagar) e remocao de pecas com os arquivos delas. */
object PieceStore {
    private fun prefs(ctx: Context) = ctx.getSharedPreferences("suuvi_issued", Context.MODE_PRIVATE)
    fun stored(ctx: Context, folderId: String): Int = prefs(ctx).getInt("issued.$folderId", 0)
    fun setIssued(ctx: Context, folderId: String, n: Int) = prefs(ctx).edit().putInt("issued.$folderId", n).apply()

    /** Apaga a peca e a foto/recorte dela. O numero fica gasto: o contador guarda o maior ja emitido. */
    suspend fun delete(ctx: Context, dao: SuuviDao, piece: SavedPiece) {
        val existing = dao.savedNumbers(piece.folderId)
        setIssued(ctx, piece.folderId, PieceRules.issued(existing, stored(ctx, piece.folderId)))
        ImageStore.deleteFiles(ctx, piece.imagePath)
        dao.deleteSavedPiece(piece.id)
    }
}
