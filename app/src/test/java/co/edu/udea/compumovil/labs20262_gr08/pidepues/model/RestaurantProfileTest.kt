package co.edu.udea.compumovil.labs20262_gr08.pidepues.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RestaurantProfileTest {

    @Test
    fun defaultProfile_hasBlankValuesAndDefaultSettings() {
        val profile = RestaurantProfile(uid = "uid123")
        assertEquals("uid123", profile.uid)
        assertEquals("", profile.name)
        assertEquals("", profile.address)
        assertEquals("", profile.phone)
        assertEquals("", profile.email)
        assertEquals("", profile.bankName)
        assertEquals(RestaurantProfile.ACCOUNT_TYPE_SAVINGS, profile.accountType)
        assertEquals("", profile.accountNumber)
        assertEquals("", profile.accountHolder)
        assertTrue(profile.notifyNewOrders)
        assertTrue(profile.notifyNewReviews)
        assertTrue(profile.notifyAlertSound)
        assertEquals(false, profile.notifyDailySummary)
        assertEquals("R", profile.initial)
    }

    @Test
    fun initial_extractsFirstLetterUppercase() {
        val profile1 = RestaurantProfile(name = "la Parrilla de Don Pepe")
        assertEquals("L", profile1.initial)

        val profile2 = RestaurantProfile(name = "  burger House  ")
        assertEquals("B", profile2.initial)

        val profileEmpty = RestaurantProfile(name = "  ")
        assertEquals("R", profileEmpty.initial)
    }

    @Test
    fun accountType_constantsAreCorrect() {
        assertEquals("Ahorros", RestaurantProfile.ACCOUNT_TYPE_SAVINGS)
        assertEquals("Corriente", RestaurantProfile.ACCOUNT_TYPE_CHECKING)
    }
}
