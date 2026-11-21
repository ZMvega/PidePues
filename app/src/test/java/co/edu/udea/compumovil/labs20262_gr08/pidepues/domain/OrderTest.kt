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
        assertEquals(OrderStatus.PREPARING, OrderStatus.RECEIVED.next())
        assertEquals(OrderStatus.ON_THE_WAY, OrderStatus.PREPARING.next())
        assertEquals(OrderStatus.DELIVERED, OrderStatus.ON_THE_WAY.next())
        assertNull(OrderStatus.DELIVERED.next())
    }

    @Test
    fun status_isActiveUntilDelivered() {
        assertTrue(OrderStatus.RECEIVED.isActive)
        assertTrue(OrderStatus.ON_THE_WAY.isActive)
        assertFalse(OrderStatus.DELIVERED.isActive)
    }

    @Test
    fun status_fromKeyFallsBackToReceived() {
        assertEquals(OrderStatus.ON_THE_WAY, OrderStatus.fromKey("on_the_way"))
        assertEquals(OrderStatus.RECEIVED, OrderStatus.fromKey("otra-cosa"))
        assertEquals(OrderStatus.RECEIVED, OrderStatus.fromKey(null))
    }

    @Test
    fun paymentMethod_fromKey() {
        assertEquals(PaymentMethod.NEQUI, PaymentMethod.fromKey("nequi"))
        assertEquals(PaymentMethod.CASH, PaymentMethod.fromKey(null))
    }

    @Test
    fun order_codeAndItemCount() {
        val o = order(OrderStatus.RECEIVED)
        assertEquals("#PP-ABC12", o.shortCode)
        assertEquals(3, o.itemCount)
    }

    @Test
    fun order_remainingMinutesDropsAsItAdvances() {
        val received = order(OrderStatus.RECEIVED).remainingMinutes()
        val preparing = order(OrderStatus.PREPARING).remainingMinutes()
        val onTheWay = order(OrderStatus.ON_THE_WAY).remainingMinutes()
        assertTrue(received > preparing && preparing > onTheWay && onTheWay >= 5)
        assertEquals(0, order(OrderStatus.DELIVERED).remainingMinutes())
    }
}
