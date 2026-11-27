package app.suuvi

import app.suuvi.domain.ShelfRules
import java.io.File
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class ShelfFixtureTest {
    private fun load() = JSONObject(File("../../fixtures/shelf.json").readText())

    @Test
    fun dailyPick() {
        val cases = load().getJSONArray("dailyPick")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val expect: Int? = if (c.isNull("expect")) null else c.getInt("expect")
            assertEquals(c.getString("name"), expect, ShelfRules.dailyIndex(c.getLong("epochDay"), c.getInt("count")))
        }
    }

    @Test
    fun chipLetter() {
        val cases = load().getJSONArray("chipLetter")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            assertEquals(c.getString("title"), c.getString("expect"), ShelfRules.chipLetter(c.getString("title")))
        }
    }
}
