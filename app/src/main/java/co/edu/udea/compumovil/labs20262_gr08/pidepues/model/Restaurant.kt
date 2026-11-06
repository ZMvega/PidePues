package co.edu.udea.compumovil.labs20262_gr08.pidepues.model

/**categorias de cocina para explorar por categoria y filtro tipo de cocina */
enum class Category(val label: String, val emoji: String) {
    HAMBURGUESAS("Hamburguesas", "🍔"),
    PIZZA("Pizza", "🍕"),
    SUSHI("Sushi", "🍣"),
    ENSALADAS("Ensaladas", "🥗"),
    MEXICANA("Mexicana", "🌮"),
    CAFE("Café", "☕"),
    ASIATICA("Asiática", "🍜"),
    COLOMBIANA("Colombiana", "🇨🇴")
}

/**formas de ordenar la lista de restaurantes */
enum class SortOption { RECOMMENDED, NEAREST, BEST_RATED, FASTEST }

data class Restaurant(
    val id: Int,
    val name: String,
    /** Texto bajo el nombre, ej. "Pizza Italiana". */
    val cuisine: String,
    /** Etiqueta sobre la foto, ej. "Bandeja Paisa". */
    val dishTag: String,
    val category: Category,
    val rating: Float,
    val deliveryMinutes: Int,
    /** Costo de envío en pesos colombianos (0 = gratis). */
    val deliveryFee: Int,
    val latitude: Double,
    val longitude: Double,
    val emoji: String,
    val isOpen: Boolean = true,
    /** Palabras extra para que la búsqueda lo encuentre (ej. "comida saludable"). */
    val keywords: String = ""
)

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    /** true = viene del GPS del dispositivo; false = ubicación por defecto (El Poblado). */
    val isFromDevice: Boolean = false
)

data class RestaurantWithDistance(
    val restaurant: Restaurant,
    val distanceKm: Double
)

data class RestaurantFilters(
    val sort: SortOption = SortOption.RECOMMENDED,
    val maxDistanceKm: Float = DEFAULT_MAX_DISTANCE_KM,
    val minRating: Float = 0f,
    val categories: Set<Category> = emptySet()
) {
    val isDefault: Boolean get() = this == RestaurantFilters()

    companion object {
        const val DEFAULT_MAX_DISTANCE_KM = 5f
    }
}
