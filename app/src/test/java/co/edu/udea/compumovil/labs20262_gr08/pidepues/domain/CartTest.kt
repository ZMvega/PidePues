package co.edu.udea.compumovil.labs20262_gr08.pidepues.domain

import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.SampleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CartTest {

    private val restaurants = SampleData.restaurants()
    private val pizza = restaurants.first { it.id == 2 }      // envío $4.000
    private val burger = restaurants.first { it.id == 5 }
    private val pizzaMenu = SampleData.menuFor(pizza)
    private val burgerMenu = SampleData.menuFor(burger)

    @Test
    fun add_sumsQuantitiesAndTotals() {
        val cart = Cart()
        assertEquals(AddResult.ADDED, cart.add(pizza, pizzaMenu[0]))   // 28.000
        cart.add(pizza, pizzaMenu[0])
        cart.add(pizza, pizzaMenu[3])                                  // 12.000
        assertEquals(3, cart.itemCount)
        assertEquals(2, cart.quantityOf(pizzaMenu[0].id))
        assertEquals(68000, cart.subtotal)
        assertEquals(4000, cart.deliveryFee)
        assertEquals(72000, cart.total)
    }

    @Test
    fun add_fromAnotherRestaurant_isRejected() {
        val cart = Cart()
        cart.add(pizza, pizzaMenu[0])
        assertEquals(AddResult.DIFFERENT_RESTAURANT, cart.add(burger, burgerMenu[0]))
        assertEquals(1, cart.itemCount)
        assertEquals(pizza.id, cart.restaurant?.id)
    }

    @Test
    fun replaceWith_startsANewCart() {
        val cart = Cart()
        cart.add(pizza, pizzaMenu[0])
        cart.replaceWith(burger, burgerMenu[1])
        assertEquals(1, cart.itemCount)
        assertEquals(burger.id, cart.restaurant?.id)
        assertEquals(0, cart.quantityOf(pizzaMenu[0].id))
    }

    @Test
    fun decrement_removesLineAndResetsRestaurantWhenEmpty() {
        val cart = Cart()
        cart.add(pizza, pizzaMenu[0])
        cart.increment(pizzaMenu[0].id)
        cart.decrement(pizzaMenu[0].id)
        assertEquals(1, cart.quantityOf(pizzaMenu[0].id))
        cart.decrement(pizzaMenu[0].id)
        assertTrue(cart.isEmpty)
        assertNull(cart.restaurant)
        assertEquals(0, cart.total)
    }
}
