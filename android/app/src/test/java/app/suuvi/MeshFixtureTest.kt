package app.suuvi

import app.suuvi.domain.ContourRules
import app.suuvi.domain.IPoint
import app.suuvi.domain.MeshRules
import app.suuvi.domain.Vertex3
import java.io.File
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MeshFixtureTest {
    private val root = JSONObject(File("../../fixtures/mesh.json").readText())

    private fun ints(a: JSONArray) = List(a.length()) { a.getInt(it) }
    private fun rows(a: JSONArray) = List(a.length()) { ints(a.getJSONArray(it)) }

    @Test
    fun prisms() {
        val cases = root.getJSONArray("cases")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val name = c.getString("name")
            val pts = rows(c.getJSONArray("contour")).map { IPoint(it[0], it[1]) }
            val depth = c.getInt("depth")
            val mesh = MeshRules.extrude(pts, depth)!!
            assertEquals(name, rows(c.getJSONArray("vertices")).map { Vertex3(it[0], it[1], it[2]) }, mesh.vertices)
            assertEquals(name, rows(c.getJSONArray("triangles")), mesh.triangles)
            assertEquals(name, c.getInt("volume6"), MeshRules.volume6(mesh))
            assertEquals(name, c.getInt("volume6"), 3 * ContourRules.area2(pts) * depth)
        }
    }

    @Test
    fun delaunay() {
        val cases = root.getJSONArray("delaunay")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val name = c.getString("name")
            val pts = rows(c.getJSONArray("contour")).map { IPoint(it[0], it[1]) }
            val ears = MeshRules.earClip(pts)!!
            assertEquals(name, rows(c.getJSONArray("expect")), MeshRules.delaunay(pts, ears))
        }
    }

    @Test
    fun beveled() {
        val cases = root.getJSONArray("beveled")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val name = c.getString("name")
            val pts = rows(c.getJSONArray("contour")).map { IPoint(it[0], it[1]) }
            val mesh = MeshRules.beveled(pts, c.getInt("depth"), c.getInt("bevel"))!!
            assertEquals(name, rows(c.getJSONArray("vertices")).map { Vertex3(it[0], it[1], it[2]) }, mesh.vertices)
            assertEquals(name, rows(c.getJSONArray("triangles")), mesh.triangles)
            assertTrue(name, MeshRules.isClosed(mesh))
            assertTrue(name, MeshRules.volume6Long(mesh) > 0)
        }
    }

    @Test
    fun degenerateReturnsNull() = assertNull(MeshRules.extrude(listOf(IPoint(0, 0), IPoint(1, 0)), 1))
}
