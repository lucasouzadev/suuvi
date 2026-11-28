package app.suuvi

import app.suuvi.domain.NewPiece
import app.suuvi.domain.PieceRules
import java.io.File
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class PieceFixtureTest {
    private val root = JSONObject(File("../../fixtures/create.json").readText())

    @Test
    fun imagePath() {
        val cases = root.getJSONArray("image")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val cut = c.getJSONArray("cutouts").let { a -> List(a.length()) { if (a.isNull(it)) null else a.getString(it) } }
            val photos = c.getJSONArray("photos").let { a -> List(a.length()) { a.getString(it) } }
            val want = if (c.isNull("expect")) null else c.getString("expect")
            assertEquals(c.getString("name"), want, PieceRules.imagePath(cut, photos))
        }
    }

    @Test
    fun create() {
        val cases = root.getJSONArray("cases")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val ex = c.getJSONArray("existing").let { a -> List(a.length()) { a.getInt(it) } }
            val got = PieceRules.create(c.getString("title"), c.getString("place"), c.getString("tag"), ex, c.getInt("lastIssued"))
            val want = if (c.isNull("expect")) null else c.getJSONObject("expect").let {
                NewPiece(it.getInt("number"), it.getString("title"), it.getString("place"),
                    if (it.isNull("tag")) null else it.getString("tag"), it.getInt("lastIssued"))
            }
            assertEquals(c.getString("name"), want, got)
        }
    }
}
