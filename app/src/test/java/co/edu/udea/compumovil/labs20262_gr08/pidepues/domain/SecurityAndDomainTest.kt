package co.edu.udea.compumovil.labs20262_gr08.pidepues.domain

import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.SampleData
import co.edu.udea.compumovil.labs20262_gr08.pidepues.domain.RecommendationEngine
import co.edu.udea.compumovil.labs20262_gr08.pidepues.domain.Validators
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Order
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderStatus
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.PaymentMethod
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import javax.crypto.AEADBadTagException

class SecurityAndDomainTest {

    // ----- PRUEBA DE SEGURIDAD 1: Validación estricta de credenciales y entradas -----
    @Test
    fun validators_rejectWeakPasswordsAndMalformedInputs() {
        assertFalse(Validators.isValidPassword("1234567"))
        assertTrue(Validators.isValidPassword("PidePues2026!"))

        assertFalse(Validators.isValidEmail("usuario_sin_arroba.com"))
        assertFalse(Validators.isValidEmail("admin' OR '1'='1@test.com"))
        assertTrue(Validators.isValidEmail("cliente@udea.edu.co"))

        assertFalse(Validators.isValidPhone("12345"))
        assertTrue(Validators.isValidPhone("3001234567"))
        assertEquals("+573001234567", Validators.normalizePhone("300 123 4567"))
    }

    // ----- PRUEBA DE SEGURIDAD 2: Cifrado AES-GCM de ubicación/dirección sensible -----
    @Test
    fun securityUtils_encryptsAndDecryptsSensitiveAddressWithUniqueIv() {
        val key = SecurityUtils.generateKey()
        val sensitiveAddress = "Cra 43A #1-50, Apto 301, El Poblado (6.2088, -75.5671)"

        val encrypted1 = SecurityUtils.encrypt(sensitiveAddress, key)
        val encrypted2 = SecurityUtils.encrypt(sensitiveAddress, key)

        // El texto cifrado no debe exponer el texto plano ni repetirse con el mismo dato
        assertFalse(encrypted1.contains("El Poblado"))
        assertNotEquals(encrypted1, encrypted2)
        assertEquals(sensitiveAddress, SecurityUtils.decrypt(encrypted1, key))
        assertEquals(sensitiveAddress, SecurityUtils.decrypt(encrypted2, key))
    }

    // ----- PRUEBA DE SEGURIDAD 3: Integridad (detecta alteración de datos cifrados) -----
    @Test(expected = AEADBadTagException::class)
    fun securityUtils_detectsTamperedCiphertext() {
        val key = SecurityUtils.generateKey()
        val encrypted = SecurityUtils.encrypt("Nequi 3124458821", key)

        // Alterar el último carácter hexadecimal simula manipulación del dato
        val lastChar = if (encrypted.last() == '0') '1' else '0'
        val tampered = encrypted.dropLast(1) + lastChar

        SecurityUtils.decrypt(tampered, key)
    }

    // ----- PRUEBA DE SEGURIDAD 4: Enmascaramiento de datos sensibles -----
    @Test
    fun securityUtils_masksPhoneNumbersProperly() {
        assertEquals("*********8821", SecurityUtils.maskSensitive("+573124458821"))
    }

    // ----- PRUEBA FUNCIONAL: Motor de recomendación (Semana 10) -----
    @Test
    fun recommendationEngine_boostsPreferredCategoryFromOrderHistory() {
        val restaurants = SampleData.restaurants()
        val sushiRestaurant = restaurants.first { it.id == 6 } // Sakura Roll (SUSHI)
        val pizzaRestaurant = restaurants.first { it.id == 2 } // Pizza Napoletana (PIZZA)

        val fakeOrder = Order(
            id = "ord-1",
            restaurantId = sushiRestaurant.id,
            restaurantName = sushiRestaurant.name,
            restaurantEmoji = sushiRestaurant.emoji,
            lines = emptyList(),
            subtotal = 36000,
            deliveryFee = 5000,
            serviceFee = 1000,
            total = 42000,
            address = "El Poblado",
            instructions = "",
            paymentMethod = PaymentMethod.CASH,
            status = OrderStatus.ENTREGADO,
            createdAtMillis = System.currentTimeMillis(),
            etaMinutes = 40
        )

        val prefs = RecommendationEngine.buildPreferences(
            orders = listOf(fakeOrder),
            restaurants = restaurants,
            recentSearches = listOf("Sushi")
        )

        val sushiScore = RecommendationEngine.score(sushiRestaurant, 1.5, prefs)
        val pizzaScore = RecommendationEngine.score(pizzaRestaurant, 1.5, prefs)

        assertTrue("El restaurante de la categoría preferida debe tener mayor puntaje", sushiScore > pizzaScore)
    }
}