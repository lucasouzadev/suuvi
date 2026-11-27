package app.suuvi

import app.suuvi.domain.ContourRules
import app.suuvi.domain.IPoint
import java.io.File
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class ContourFixtureTest {
    private val root = JSONObject(File("../../fixtures/contour.json").readText())

    private fun pts(a: JSONArray) = List(a.length()) { a.getJSONArray(it).let { p -> IPoint(p.getInt(0), p.getInt(1)) } }

    @Test
    fun trace() {
        val cases = root.getJSONArray("trace")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val rows = c.getJSONArray("rows").let { a -> List(a.length()) { a.getString(it) } }
            val got = ContourRules.trace(rows[0].length, rows.size) { x, y -> rows[y][x] == '#' }
            assertEquals(c.getString("name"), pts(c.getJSONArray("expect")), got)
            assertEquals(c.getString("name"), c.getInt("area2"), ContourRules.area2(got))
        }
    }

    @Test
    fun simplify() {
        val cases = root.getJSONArray("simplify")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            assertEquals(c.getString("name"), pts(c.getJSONArray("expect")), ContourRules.simplify(pts(c.getJSONArray("points")), c.getInt("eps")))
        }
    }
}
