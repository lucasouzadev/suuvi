package app.suuvi

import app.suuvi.domain.CaptureRules
import app.suuvi.domain.PixelRect
import java.io.File
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class CaptureFixtureTest {
    private val root = JSONObject(File("../../fixtures/capture.json").readText())

    private fun ints(a: JSONArray) = List(a.length()) { a.getInt(it) }

    @Test
    fun max() = assertEquals(root.getInt("maxPhotos"), CaptureRules.MAX_PHOTOS)

    @Test
    fun alphaBounds() {
        val cases = root.getJSONArray("alphaBounds")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val rows = c.getJSONArray("rows").let { a -> List(a.length()) { a.getString(it) } }
            val alpha = rows.flatMap { r -> r.map { if (it == '#') 255 else 0 } }.toIntArray()
            val got = CaptureRules.alphaBounds(alpha, rows[0].length, rows.size)
            val want = if (c.isNull("expect")) null else ints(c.getJSONArray("expect")).let { PixelRect(it[0], it[1], it[2], it[3]) }
            assertEquals(c.getString("name"), want, got)
        }
    }

    @Test
    fun paddedCrop() {
        val cases = root.getJSONArray("paddedCrop")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val b = ints(c.getJSONArray("box")); val im = ints(c.getJSONArray("image")); val e = ints(c.getJSONArray("expect"))
            assertEquals(c.getString("name"), PixelRect(e[0], e[1], e[2], e[3]),
                CaptureRules.paddedCrop(PixelRect(b[0], b[1], b[2], b[3]), im[0], im[1]))
        }
    }

    @Test
    fun cta() {
        val cases = root.getJSONArray("cta")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val got = CaptureRules.cta(c.getInt("photos"), c.getInt("lifted"), c.getBoolean("lifting")).name.lowercase()
            assertEquals(c.getString("expect"), got)
        }
    }
}
