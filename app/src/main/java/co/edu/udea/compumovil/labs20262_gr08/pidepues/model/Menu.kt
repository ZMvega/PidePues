package co.edu.udea.compumovil.labs20262_gr08.pidepues.model

/** Categorías de productos administrables por cada restaurante. */
enum class MenuCategory(val key: String, val label: String) {
    HAMBURGUESAS("hamburguesas", "Hamburguesas"),
    ACOMPANAMIENTOS("acompanantes", "Acompañantes"),
    BEBIDAS("bebidas", "Bebidas"),
    POSTRES("postres", "Postres");

    companion object {
        fun fromKey(key: String?): MenuCategory = entries.firstOrNull {
            it.key.equals(key, ignoreCase = true) || it.label.equals(key, ignoreCase = true)
        } ?: HAMBURGUESAS
    }
}

/** Plato del menú compartido por los flujos de Restaurante y Cliente. */
data class MenuItem(
    val id: String,
    val restaurantId: Int,
    val name: String,
    val description: String,
    /** Precio en pesos colombianos. */
    val price: Int,
    val emoji: String,
    val category: MenuCategory = MenuCategory.HAMBURGUESAS,
    val isAvailable: Boolean = true,
    val imageUri: String? = null,
    /** Firestore document ID; string because restaurant accounts are keyed by Firebase UID. */
    val restaurantDocumentId: String = restaurantId.toString()
)

data class CartLine(val item: MenuItem, val quantity: Int) {
    val total: Int get() = item.price * quantity
}
