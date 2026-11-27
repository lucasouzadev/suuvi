package app.suuvi

import app.suuvi.domain.SearchItem
import app.suuvi.domain.SearchRules
import java.io.File
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchFixtureTest {
    private val root = JSONObject(File("../../fixtures/search.json").readText())

    private fun strings(a: org.json.JSONArray) = List(a.length()) { a.getString(it) }

    private val items = root.getJSONArray("items").let { a ->
        List(a.length()) {
            val e = a.getJSONObject(it)
            SearchItem(e.getString("id"), e.getString("name"), "", e.getString("place"), 0, strings(e.getJSONArray("tags")))
        }
    }

    @Test
    fun filter() {
        val cases = root.getJSONArray("filter")
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val got = SearchRules.filter(items, c.getString("query"), strings(c.getJSONArray("selected")).toSet()).map { it.id }
            assertEquals(c.getString("name"), strings(c.getJSONArray("expect")), got)
        }
    }

    @Test
    fun allTags() {
        assertEquals(strings(root.getJSONArray("allTags")), SearchRules.allTags(items))
    }
}
