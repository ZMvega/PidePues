package co.edu.udea.compumovil.labs20262_gr08.pidepues.domain

import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Order
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderLine
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderStatus
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OrderTest {

    private fun order(status: OrderStatus, eta: Int = 30) = Order(
        id = "abc123xyz", restaurantId = 2, restaurantName = "Pizza Napoletana", restaurantEmoji = "🍕",
        lines = listOf(OrderLine("Pizza Margarita", "🍕", 28000, 2), OrderLine("Pan de ajo", "🥖", 12000, 1)),
        subtotal = 68000, deliveryFee = 4000, serviceFee = 1000, total = 73000,
        address = "Cra 43 #1-15", instructions = "", paymentMethod = PaymentMethod.CASH,
        status = status, createdAtMillis = 0L, etaMinutes = eta
    )

    @Test
    fun status_advancesInOrderAndStopsAtDelivered() {
        assertEquals(OrderStatus.TOMADO, OrderStatus.SOLICITADO.next())
        assertEquals(OrderStatus.PREPARADO, OrderStatus.TOMADO.next())
        assertEquals(OrderStatus.EN_CAMINO, OrderStatus.PREPARADO.next())
        assertEquals(OrderStatus.ENTREGADO, OrderStatus.EN_CAMINO.next())
        assertNull(OrderStatus.ENTREGADO.next())
    }

    @Test
    fun status_isActiveUntilDelivered() {
        assertTrue(OrderStatus.SOLICITADO.isActive)
        assertTrue(OrderStatus.TOMADO.isActive)
        assertTrue(OrderStatus.PREPARADO.isActive)
        assertTrue(OrderStatus.EN_CAMINO.isActive)
        assertFalse(OrderStatus.ENTREGADO.isActive)
    }

    @Test
    fun status_fromKeyFallsBackToReceived() {
        assertEquals(OrderStatus.EN_CAMINO, OrderStatus.fromKey("en_camino"))
        assertEquals(OrderStatus.EN_CAMINO, OrderStatus.fromKey("on_the_way"))
        assertEquals(OrderStatus.TOMADO, OrderStatus.fromKey("tomado"))
        assertEquals(OrderStatus.PREPARADO, OrderStatus.fromKey("preparado"))
        assertEquals(OrderStatus.SOLICITADO, OrderStatus.fromKey("otra-cosa"))
        assertEquals(OrderStatus.SOLICITADO, OrderStatus.fromKey(null))
    }

    @Test
    fun paymentMethod_fromKey() {
        assertEquals(PaymentMethod.NEQUI, PaymentMethod.fromKey("nequi"))
        assertEquals(PaymentMethod.CASH, PaymentMethod.fromKey(null))
    }

    @Test
    fun order_codeAndItemCount() {
        val o = order(OrderStatus.SOLICITADO)
        assertEquals("#PP-ABC12", o.shortCode)
        assertEquals(3, o.itemCount)
    }

    @Test
    fun order_remainingMinutesDropsAsItAdvances() {
        val solicitado = order(OrderStatus.SOLICITADO).remainingMinutes()
        val tomado = order(OrderStatus.TOMADO).remainingMinutes()
        val preparado = order(OrderStatus.PREPARADO).remainingMinutes()
        val enCamino = order(OrderStatus.EN_CAMINO).remainingMinutes()
        assertTrue(solicitado >= tomado && tomado >= preparado && preparado >= enCamino && enCamino >= 5)
        assertEquals(0, order(OrderStatus.ENTREGADO).remainingMinutes())
    }
}
