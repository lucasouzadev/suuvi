package app.suuvi

import app.suuvi.domain.CollectionRules
import java.io.File
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class CollectionFixtureTest {
    private val root = JSONObject(File("../../fixtures/collection.json").readText())

    @Test
    fun slots() {
        val cases = root.getJSONArray("slots")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val got = CollectionRules.slots(c.getInt("count")).joinToString("") { if (it) "F" else "E" }
            assertEquals("count ${c.getInt("count")}", c.getString("expect"), got)
        }
    }

    @Test
    fun tilts() {
        val a = root.getJSONArray("tilts")
        assertEquals(List(a.length()) { a.getDouble(it).toFloat() }, CollectionRules.tilts)
    }
}
