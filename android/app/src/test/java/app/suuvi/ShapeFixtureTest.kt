package app.suuvi

import app.suuvi.domain.IPoint
import app.suuvi.domain.MeshRules
import app.suuvi.domain.ShapeRules
import java.io.File
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ShapeFixtureTest {
    private val root = JSONObject(File("../../fixtures/shape.json").readText())

    private fun alpha(c: JSONObject): Triple<IntArray, Int, Int> {
        if (c.has("rows")) {
            val rows = c.getJSONArray("rows")
            val w = rows.getString(0).length
            val a = IntArray(w * rows.length())
            for (y in 0 until rows.length()) for ((x, ch) in rows.getString(y).withIndex()) {
                a[y * w + x] = if (ch == '#') 255 else if (ch == 'o') 10 else 0
            }
            return Triple(a, w, rows.length())
        }
        val size = c.getJSONArray("size"); val r = c.getJSONArray("rect")
        val w = size.getInt(0); val h = size.getInt(1)
        val a = IntArray(w * h)
        for (y in r.getInt(1) until r.getInt(3)) for (x in r.getInt(0) until r.getInt(2)) a[y * w + x] = 255
        return Triple(a, w, h)
    }

    @Test
    fun shapes() {
        val cases = root.getJSONArray("cases")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val name = c.getString("name")
            val (a, w, h) = alpha(c)
            val s = ShapeRules.shape(a, w, h)
            if (c.isNull("points")) { assertNull(name, s); continue }
            val expect = c.getJSONArray("points")
            assertNotNull(name, s)
            assertEquals(name, expect.length(), s!!.points.size)
            for (k in 0 until expect.length()) {
                assertEquals("$name #$k x", expect.getJSONArray(k).getInt(0), s.points[k].x)
                assertEquals("$name #$k y", expect.getJSONArray(k).getInt(1), s.points[k].y)
            }
            assertEquals(name, c.getInt("depth"), s.depth)
            val m = ShapeRules.mesh(a, w, h)
            assertNotNull(name, m)
            val sol = c.getJSONObject("solid")
            assertEquals(name, sol.getInt("vertices"), m!!.vertices.size)
            assertEquals(name, sol.getInt("triangles"), m.triangles.size)
            val bb = sol.getJSONArray("bbox")
            val got = listOf(m.vertices.minOf { it.x }, m.vertices.minOf { it.y }, m.vertices.minOf { it.z },
                m.vertices.maxOf { it.x }, m.vertices.maxOf { it.y }, m.vertices.maxOf { it.z })
            assertEquals(name, List(6) { bb.getInt(it) }, got)
            assertTrue(name, MeshRules.isClosed(m))
            assertTrue(name, MeshRules.volume6Long(m) > 0)
        }
    }

    @Test
    fun chaikin() {
        val cases = root.getJSONArray("chaikin")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val pts = c.getJSONArray("points").let { a -> List(a.length()) { a.getJSONArray(it).let { p -> IPoint(p.getInt(0), p.getInt(1)) } } }
            val out = MeshRules.chaikin(pts, c.getInt("rounds"))
            val expect = c.getJSONArray("expect").let { a -> List(a.length()) { a.getJSONArray(it).let { p -> IPoint(p.getInt(0), p.getInt(1)) } } }
            assertEquals(c.getString("name"), expect, out)
        }
    }
}
