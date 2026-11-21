package co.edu.udea.compumovil.labs20262_gr08.pidepues.domain

import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.CartLine
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuItem
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Restaurant

/** Resultado de intentar agregar un plato. */
enum class AddResult { ADDED, DIFFERENT_RESTAURANT }

/**
 * Carrito de compras: solo admite platos de UN restaurante a la vez.
 * Es Kotlin puro: la lógica se prueba sin Android.
 */
class Cart {

    var restaurant: Restaurant? = null
        private set

    private val quantities = linkedMapOf<String, CartLine>()

    val lines: List<CartLine> get() = quantities.values.toList()
    val itemCount: Int get() = quantities.values.sumOf { it.quantity }
    val subtotal: Int get() = quantities.values.sumOf { it.total }
    val deliveryFee: Int get() = if (quantities.isEmpty()) 0 else restaurant?.deliveryFee ?: 0
    val serviceFee: Int get() = if (quantities.isEmpty()) 0 else SERVICE_FEE
    val total: Int get() = subtotal + deliveryFee + serviceFee
    val isEmpty: Boolean get() = quantities.isEmpty()

    fun quantityOf(itemId: String): Int = quantities[itemId]?.quantity ?: 0

    /** Agrega una unidad. Si el carrito es de otro restaurante, no agrega y avisa. */
    fun add(from: Restaurant, item: MenuItem): AddResult {
        val current = restaurant
        if (current != null && current.id != from.id && !isEmpty) return AddResult.DIFFERENT_RESTAURANT
        restaurant = from
        val line = quantities[item.id]
        quantities[item.id] = CartLine(item, (line?.quantity ?: 0) + 1)
        return AddResult.ADDED
    }

    /** Vacía el carrito y empieza uno nuevo con este plato. */
    fun replaceWith(from: Restaurant, item: MenuItem) {
        clear()
        add(from, item)
    }

    fun increment(itemId: String) {
        val line = quantities[itemId] ?: return
        quantities[itemId] = line.copy(quantity = line.quantity + 1)
    }

    fun decrement(itemId: String) {
        val line = quantities[itemId] ?: return
        if (line.quantity <= 1) quantities.remove(itemId) else quantities[itemId] = line.copy(quantity = line.quantity - 1)
        if (quantities.isEmpty()) restaurant = null
    }

    fun clear() {
        quantities.clear()
        restaurant = null
    }

    companion object {
        /** Cargo por servicio de PidePues por pedido (en pesos). */
        const val SERVICE_FEE = 1000
    }
}

