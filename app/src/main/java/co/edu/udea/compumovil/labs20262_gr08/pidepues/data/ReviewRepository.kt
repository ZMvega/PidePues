package co.edu.udea.compumovil.labs20262_gr08.pidepues.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.math.roundToInt

class ReviewRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun submitReview(
        orderId: String,
        orderShortCode: String,
        restaurantId: String,
        restaurantName: String,
        customerName: String,
        stars: Int,
        comment: String
    ): Result<Unit> = runCatching {
        require(stars in 1..5) { "La calificación debe estar entre 1 y 5" }

        val user = auth.currentUser ?: error("Usuario no autenticado")
        val restaurantRef = db.collection("restaurants").document(restaurantId)
        val reviewRef = restaurantRef.collection("reviews").document()
        val orderRef = db.collection("orders").document(orderId)

        val reviewData = mapOf(
            "id" to reviewRef.id,
            "orderId" to orderId,
            "orderShortCode" to orderShortCode,
            "restaurantId" to restaurantId,
            "restaurantName" to restaurantName,
            "userId" to user.uid,
            "customerName" to customerName.ifBlank { "Cliente" },
            "rating" to stars.toDouble(),
            "comment" to comment.trim(),
            "reply" to "",
            "createdAt" to FieldValue.serverTimestamp()
        )

        db.runTransaction { tx ->
            val restSnap = tx.get(restaurantRef)
            val currentRating = restSnap.getDouble("rating") ?: 0.0
            val currentCount = (restSnap.getLong("reviewCount") ?: 0L).toInt()

            val newCount = currentCount + 1
            val rawAvg = ((currentRating * currentCount) + stars) / newCount
            val newAverage = (rawAvg * 10.0).roundToInt() / 10.0

            tx.set(reviewRef, reviewData)
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