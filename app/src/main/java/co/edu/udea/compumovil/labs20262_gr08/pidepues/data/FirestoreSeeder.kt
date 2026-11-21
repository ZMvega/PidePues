package co.edu.udea.compumovil.labs20262_gr08.pidepues.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuItem
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Restaurant

/**
 * Carga los datos de ejemplo ([SampleData]) en Firestore cuando la colección está vacía.
 * Solo se ejecuta en compilaciones de depuración (ver MainActivity).
 */
object FirestoreSeeder {

    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    /** Devuelve true si cargó datos, false si ya había restaurantes. */
    fun seedIfEmpty(onDone: (Result<Boolean>) -> Unit) {
        db.collection(RestaurantRepository.RESTAURANTS).limit(1).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    onDone(Result.success(false))
                    return@addOnSuccessListener
                }
                val batch = db.batch()
                SampleData.restaurants().forEach { restaurant ->
                    val ref = db.collection(RestaurantRepository.RESTAURANTS).document(restaurant.id.toString())
                    batch.set(ref, restaurantToMap(restaurant))
                    SampleData.menuFor(restaurant).forEachIndexed { index, item ->
                        batch.set(ref.collection(RestaurantRepository.MENU).document(item.id), menuItemToMap(item, index))
                    }
                }
                batch.commit()
                    .addOnSuccessListener { onDone(Result.success(true)) }
                    .addOnFailureListener { onDone(Result.failure(it)) }
            }
            .addOnFailureListener { onDone(Result.failure(it)) }
    }

    private fun restaurantToMap(r: Restaurant): Map<String, Any> = mapOf(
        "name" to r.name,
        "cuisine" to r.cuisine,
        "dishTag" to r.dishTag,
        "category" to r.category.name,
        "rating" to r.rating.toDouble(),
        "deliveryMinutes" to r.deliveryMinutes,
        "deliveryFee" to r.deliveryFee,
        "location" to GeoPoint(r.latitude, r.longitude),
        "emoji" to r.emoji,
        "isOpen" to r.isOpen,
        "keywords" to r.keywords
    )

    private fun menuItemToMap(item: MenuItem, order: Int): Map<String, Any> = mapOf(
        "name" to item.name,
        "description" to item.description,
        "price" to item.price,
        "emoji" to item.emoji,
        "order" to order
    )
}
