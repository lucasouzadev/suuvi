package app.suuvi

import app.suuvi.domain.MeshRules
import app.suuvi.domain.PrismMesh
import app.suuvi.domain.Vertex3
import app.suuvi.domain.SampleShape
import app.suuvi.domain.ViewerRules
import java.io.File
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ViewerFixtureTest {
    private val root = JSONObject(File("../../fixtures/viewer.json").readText())

    @Test
    fun drag() {
        val cases = root.getJSONArray("drag")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val e = c.getJSONArray("expect")
            val r = ViewerRules.drag(c.getDouble("yaw"), c.getDouble("pitch"), c.getDouble("dx"), c.getDouble("dy"))
            assertEquals(c.getString("name"), e.getDouble(0), r.first, 1e-9)
            assertEquals(c.getString("name"), e.getDouble(1), r.second, 1e-9)
        }
    }

    @Test
    fun zoom() {
        val cases = root.getJSONArray("zoom")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            assertEquals(c.getString("name"), c.getDouble("expect"), ViewerRules.zoom(c.getDouble("scale"), c.getDouble("pinch")), 1e-9)
        }
    }

    @Test
    fun framing() {
        val cases = root.getJSONArray("framing")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val vs = c.getJSONArray("vertices")
            val m = PrismMesh((0 until vs.length()).map { k -> val v = vs.getJSONArray(k); Vertex3(v.getInt(0), v.getInt(1), v.getInt(2)) }, emptyList())
            val f = ViewerRules.framing(m)
            val e = c.getJSONArray("expect")
            for (k in 0 until 4) assertEquals(c.getString("name"), e.getDouble(k), f[k], 1e-9)
        }
    }

    @Test
    fun sampleShapeIsClosedMesh() {
        val m = SampleShape.coinMesh()
        assertTrue(m.triangles.size > 20)
        assertTrue(MeshRules.volume6(m) > 0)
    }
}
