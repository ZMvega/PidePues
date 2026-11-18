package co.edu.udea.compumovil.labs20262_gr08.pidepues.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Category
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuItem
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Restaurant

/**
 * Restaurantes y menús desde Firestore.
 *   restaurants/{id}            -> datos del restaurante (location es un GeoPoint)
 *   restaurants/{id}/menu/{id}  -> platos del menú
 * Firestore guarda una copia local, así que la lista también se ve sin conexión
 * si ya se había cargado antes.
 */
object RestaurantRepository {

    const val RESTAURANTS = "restaurants"
    const val MENU = "menu"

    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    fun fetchRestaurants(onResult: (Result<List<Restaurant>>) -> Unit) {
        db.collection(RESTAURANTS).get()
            .addOnSuccessListener { snapshot ->
                onResult(Result.success(snapshot.documents.mapNotNull { it.toRestaurantOrNull() }))
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun fetchMenu(restaurantId: Int, onResult: (Result<List<MenuItem>>) -> Unit) {
        db.collection(RESTAURANTS).document(restaurantId.toString()).collection(MENU).get()
            .addOnSuccessListener { snapshot ->
                val items = snapshot.documents
                    .mapNotNull { it.toMenuItemOrNull(restaurantId) }
                    .sortedBy { it.second }
                    .map { it.first }
                onResult(Result.success(items))
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    private fun DocumentSnapshot.toRestaurantOrNull(): Restaurant? {
        val restaurantId = id.toIntOrNull() ?: return null
        val point = getGeoPoint("location") ?: return null
        val categoryName = getString("category") ?: return null
        val category = runCatching { Category.valueOf(categoryName) }.getOrNull() ?: return null
        return Restaurant(
            id = restaurantId,
            name = getString("name") ?: return null,
            cuisine = getString("cuisine").orEmpty(),
            dishTag = getString("dishTag").orEmpty(),
            category = category,
            rating = getDouble("rating")?.toFloat() ?: 0f,
            deliveryMinutes = getLong("deliveryMinutes")?.toInt() ?: 0,
            deliveryFee = getLong("deliveryFee")?.toInt() ?: 0,
            latitude = point.latitude,
            longitude = point.longitude,
            emoji = getString("emoji").orEmpty(),
            isOpen = getBoolean("isOpen") ?: true,
            keywords = getString("keywords").orEmpty()
        )
    }

    /** Devuelve el plato y su posición (campo "order") para mantener el orden del menú. */
    private fun DocumentSnapshot.toMenuItemOrNull(restaurantId: Int): Pair<MenuItem, Long>? {
        val name = getString("name") ?: return null
        val item = MenuItem(
            id = id,
            restaurantId = restaurantId,
            name = name,
            description = getString("description").orEmpty(),
            price = getLong("price")?.toInt() ?: 0,
            emoji = getString("emoji").orEmpty()
        )
        return item to (getLong("order") ?: 0L)
    }
}
