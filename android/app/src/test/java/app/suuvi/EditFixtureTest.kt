package app.suuvi

import app.suuvi.domain.EditRules
import java.io.File
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class EditFixtureTest {
    private val root = JSONObject(File("../../fixtures/edit.json").readText())

    @Test
    fun valid() {
        val a = root.getJSONArray("valid")
        for (i in 0 until a.length()) {
            val c = a.getJSONObject(i)
            assertEquals(c.getString("name"), c.getBoolean("expect"), EditRules.isValid(c.getString("title"), c.getString("place")))
        }
    }

    @Test
    fun tag() {
        val a = root.getJSONArray("tag")
        for (i in 0 until a.length()) {
            val c = a.getJSONObject(i)
            assertEquals(c.getString("in"), c.getString("expect"), EditRules.normalizeTag(c.getString("in")))
        }
    }
}
