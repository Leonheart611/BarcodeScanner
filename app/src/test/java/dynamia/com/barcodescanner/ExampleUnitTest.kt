package dynamia.com.barcodescanner

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun remove_isCorrect(){
        val value = "1p000001"
        val result = value.removePrefix("1p")
        assertEquals(result,"000001")
    }

    @Test
    fun remove_isCorrectAgain(){
        val value = "XPAI000001"
        val result = value.removePrefix("1p")
        assertEquals(result,"XPAI000001")
    }
}
