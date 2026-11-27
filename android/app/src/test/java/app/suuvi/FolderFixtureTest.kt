package app.suuvi

import app.suuvi.domain.FolderItem
import app.suuvi.domain.FolderRules
import java.io.File
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class FolderFixtureTest {
    @Test
    fun order() {
        val cases = JSONObject(File("../../fixtures/folders.json").readText()).getJSONArray("order")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val items = c.getJSONArray("items").let { a ->
                List(a.length()) {
                    val e = a.getJSONObject(it)
                    FolderItem("$it", e.getString("name"), "", "", 0, e.getLong("lastUsed"))
                }
            }
            val expect = c.getJSONArray("expect").let { a -> List(a.length()) { a.getString(it) } }
            assertEquals(c.getString("name"), expect, FolderRules.ordered(items).map { it.name })
        }
    }
}
