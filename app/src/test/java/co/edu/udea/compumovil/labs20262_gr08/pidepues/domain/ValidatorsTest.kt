package co.edu.udea.compumovil.labs20262_gr08.pidepues.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {

    @Test
    fun email_acceptsValidAndRejectsInvalid() {
        assertTrue(Validators.isValidEmail("carlos@email.com"))
        assertTrue(Validators.isValidEmail("  ana.perez+pide@udea.edu.co "))
        assertFalse(Validators.isValidEmail("carlos@"))
        assertFalse(Validators.isValidEmail("carlos.email.com"))
        assertFalse(Validators.isValidEmail(""))
    }

    @Test
    fun password_needsAtLeastEightCharacters() {
        assertTrue(Validators.isValidPassword("12345678"))
        assertFalse(Validators.isValidPassword("1234567"))
    }

    @Test
    fun fullName_needsLettersAndMinimumLength() {
        assertTrue(Validators.isValidFullName("Carlos Rodríguez"))
        assertFalse(Validators.isValidFullName("  "))
        assertFalse(Validators.isValidFullName("12"))
    }

    @Test
    fun phone_acceptsColombianMobileNumbers() {
        assertTrue(Validators.isValidPhone("300 000 0000"))
        assertTrue(Validators.isValidPhone("+57 312 445 8821"))
        assertTrue(Validators.isValidPhone("3124458821"))
        assertFalse(Validators.isValidPhone("2124458821"))   // no empieza por 3
        assertFalse(Validators.isValidPhone("312445882"))    // 9 dígitos
        assertFalse(Validators.isValidPhone(""))
    }

    @Test
    fun phone_isNormalizedWithCountryCode() {
        assertEquals("+573124458821", Validators.normalizePhone("312 445 8821"))
        assertEquals("+573124458821", Validators.normalizePhone("+57 312 445 8821"))
    }
}
