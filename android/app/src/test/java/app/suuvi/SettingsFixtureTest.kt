package app.suuvi

import app.suuvi.domain.SettingsRules
import java.io.File
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsFixtureTest {
    private val root = JSONObject(File("../../fixtures/settings.json").readText())

    @Test
    fun normalizeName() {
        val cases = root.getJSONArray("normalizeName")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            assertEquals(c.getString("name"), c.getString("expect"), SettingsRules.normalizeName(c.getString("input")))
        }
    }

    @Test
    fun tiers() {
        val a = root.getJSONArray("tiers")
        assertEquals(List(a.length()) { a.getString(it) }, SettingsRules.tiers)
    }
}
