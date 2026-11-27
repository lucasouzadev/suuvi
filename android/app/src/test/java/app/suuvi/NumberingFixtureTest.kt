package app.suuvi

import app.suuvi.domain.Numbering
import java.io.File
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberingFixtureTest {
    @Test
    fun sharedFixtureCases() {
        // Gradle roda os testes com a pasta do modulo (android/app) como diretorio atual.
        val file = File("../../fixtures/numbering.json")
        val cases = JSONObject(file.readText()).getJSONArray("cases")
        assertTrue(cases.length() > 0)
        for (i in 0 until cases.length()) {
            val c = cases.getJSONObject(i)
            val existing = c.getJSONArray("existing").let { a -> List(a.length()) { a.getInt(it) } }
            val last = c.optInt("lastIssued", 0)
            assertEquals(c.getString("name"), c.getInt("expect"), Numbering.next(existing, last))
        }
    }
}
