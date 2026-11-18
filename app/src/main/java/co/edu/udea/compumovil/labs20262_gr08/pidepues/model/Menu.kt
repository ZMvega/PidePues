package co.edu.udea.compumovil.labs20262_gr08.pidepues.model

/** Plato del menú de un restaurante (Firestore: restaurants/{id}/menu/{itemId}). */
data class MenuItem(
    val id: String,
    val restaurantId: Int,
    val name: String,
    val description: String,
    /** Precio en pesos colombianos. */
    val price: Int,
    val emoji: String
)

data class CartLine(val item: MenuItem, val quantity: Int) {
    val total: Int get() = item.price * quantity
}
