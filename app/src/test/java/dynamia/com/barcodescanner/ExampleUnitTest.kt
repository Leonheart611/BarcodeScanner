package dynamia.com.barcodescanner

import dynamia.com.core.util.checkFirstCharacter
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
    fun remove_isCorrect() {
        val value = "1p000001"
        val result = value.removePrefix("1p")
        assertEquals(result, "000001")
    }

    @Test
    fun remove_isCorrectAgain() {
        val value = "XPAI000001"
        val result = value.removePrefix("1p")
        assertEquals(result, "XPAI000001")
    }

    @Test
    fun removek_correct() {
        val value = "K2019/po.09/0001"
        val result = value.checkFirstCharacter("K")
        assertEquals(result, "2019/po.09/0001")
    }

    @Test
    fun removeK_correct() {
        val value = "KPO NO"
        val result = value.checkFirstCharacter("K")
        assertEquals(result, "PO NO")
    }

    @Test
    fun removek_isCorrect() {
        val value = "2019/pok.09/k0001"
        val result = value.checkFirstCharacter("K")
        assertEquals(result, "2019/pok.09/k0001")
    }

    @Test
    fun checkIsFirstCharIsS_isCorrect() {
        val value = "S64648465136165wef"
        val result = value.startsWith("S")
        assertEquals(result, true)
    }
}
