package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main

import androidx.lifecycle.ViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.domain.AddResult
import co.edu.udea.compumovil.labs20262_gr08.pidepues.domain.Cart
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.CartLine
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuItem
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Restaurant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Carrito compartido por el detalle del restaurante, la pestaña Carrito y la barra inferior. */
class CartViewModel : ViewModel() {

    data class CartState(
        val restaurant: Restaurant?,
        val lines: List<CartLine>,
        val itemCount: Int,
        val subtotal: Int,
        val deliveryFee: Int,
        val serviceFee: Int,
        val total: Int
    ) {
        val isEmpty: Boolean get() = lines.isEmpty()
        fun quantityOf(itemId: String): Int = lines.firstOrNull { it.item.id == itemId }?.quantity ?: 0
    }

    private val cart = Cart()

    private val _state = MutableStateFlow(snapshot())
    val state: StateFlow<CartState> = _state.asStateFlow()

    private fun snapshot() = CartState(
        cart.restaurant, cart.lines, cart.itemCount, cart.subtotal, cart.deliveryFee, cart.serviceFee, cart.total
    )

    private fun publish() {
        _state.value = snapshot()
    }

    /** Devuelve DIFFERENT_RESTAURANT si el carrito es de otro restaurante (la UI pregunta qué hacer). */
    fun add(restaurant: Restaurant, item: MenuItem): AddResult {
        val result = cart.add(restaurant, item)
        publish()
        return result
    }

    fun replaceWith(restaurant: Restaurant, item: MenuItem) {
        cart.replaceWith(restaurant, item)
        publish()
    }

    fun increment(itemId: String) {
        cart.increment(itemId)
        publish()
    }

    fun decrement(itemId: String) {
        cart.decrement(itemId)
        publish()
    }

    fun clear() {
        cart.clear()
        publish()
    }
}
