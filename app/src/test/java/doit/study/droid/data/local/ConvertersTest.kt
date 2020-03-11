package doit.study.droid.data.local

import com.google.common.truth.Truth.assertThat
import com.google.gson.JsonSyntaxException
import org.junit.Test

class ConvertersTest {
    @Test
    fun testJsonToListMultipleItems() {
        val inputString = """
            [ "<permission>", 
            "<uses-feature>", 
            "<grant-uri-permission>", 
            "<service>" ]
        """.trimIndent()
        val expected = listOf("<permission>", "<uses-feature>", "<grant-uri-permission>", "<service>")
        assertThat(Converters().jsonToList(inputString)).isEqualTo(expected)
    }

    @Test
    fun testJsonToListEmpty() {
        val inputString = """
            [  ]
        """.trimIndent()
        val expected = emptyList<String>()
        assertThat(Converters().jsonToList(inputString)).isEqualTo(expected)
    }

    @Test(expected = IllegalStateException::class)
    fun testJsonToListInputEmpty() {
        val inputString = """

        """.trimIndent()
        Converters().jsonToList(inputString)
    }

    @Test(expected = JsonSyntaxException::class)
    fun testJsonToListInputNotAList() {
        val inputString = """
                "not an array"
        """.trimIndent()
        Converters().jsonToList(inputString)
    }
}
