package co.edu.udea.compumovil.labs20262_gr08.pidepues.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.math.roundToInt

class ReviewRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    /**
     * Guarda la reseña y recalcula el promedio del restaurante de forma atómica.
     */
    suspend fun submitReview(
        orderId: String,
        orderShortCode: String,
        restaurantId: String,
        restaurantName: String,
        stars: Int,
        comment: String
    ): Result<Unit> = runCatching {
        require(stars in 1..5) { "La calificación debe estar entre 1 y 5" }

        val user = auth.currentUser ?: error("Usuario no autenticado")
        val reviewRef = db.collection("reviews").document()
        val restaurantRef = db.collection("restaurants").document(restaurantId)
        val orderRef = db.collection("orders").document(orderId)

        val review = Review(
            id = reviewRef.id,
            orderId = orderId,
            orderShortCode = orderShortCode,
            restaurantId = restaurantId,
            restaurantName = restaurantName,
            userId = user.uid,
            userName = user.displayName ?: "Cliente",
            rating = stars,
            comment = comment.trim(),
            createdAtMillis = System.currentTimeMillis()
        )

        db.runTransaction { tx ->
            val restSnap = tx.get(restaurantRef)
            val currentRating = restSnap.getDouble("rating") ?: 0.0
            val currentCount = (restSnap.getLong("reviewCount") ?: 0L).toInt()

            val newCount = currentCount + 1
            val rawAvg = ((currentRating * currentCount) + stars) / newCount
            // Redondeo a 1 decimal (ej. 4.8)
            val newAverage = (rawAvg * 10.0).roundToInt() / 10.0

            tx.set(reviewRef, review)
            tx.update(
                restaurantRef,
                mapOf(
                    "rating" to newAverage,
                    "reviewCount" to newCount
                )
            )
            tx.update(
                orderRef,
                mapOf(
                    "rated" to true,
                    "userRating" to stars
                )
            )
        }.await()
    }
}