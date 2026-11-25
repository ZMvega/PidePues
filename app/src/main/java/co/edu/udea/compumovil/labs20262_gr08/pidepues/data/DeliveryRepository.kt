package co.edu.udea.compumovil.labs20262_gr08.pidepues.data

import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.DeliveryOrder
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderStatus
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object DeliveryRepository {

    private const val ORDERS = "orders"
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    /** Escucha todos los pedidos relevantes para el repartidor (disponibles, asignados y entregados). */
    fun listenDeliveryOrders(
        onResult: (Result<List<DeliveryOrder>>) -> Unit
    ): ListenerRegistration = db.collection(ORDERS)
        .addSnapshotListener { snapshot, error ->
            if (error != null) {
                onResult(Result.failure(error))
                return@addSnapshotListener
            }
            val list = snapshot?.documents
                ?.mapNotNull { it.toDeliveryOrderOrNull() }
                ?.sortedByDescending { it.createdAtMillis }
                ?: emptyList()
            onResult(Result.success(list))
        }

    /** El repartidor toma un pedido preparado y lo pasa a "En camino". */
    fun takeOrder(
        orderId: String,
        courier: UserProfile,
        onResult: (Result<Unit>) -> Unit
    ) {
        val updates = mapOf(
            "status" to OrderStatus.EN_CAMINO.key,
            "estado" to OrderStatus.EN_CAMINO.label,
            "courierId" to courier.uid,
            "driverName" to courier.fullName,
            "courierName" to courier.fullName,
            "courierPhone" to courier.phone,
            "driverRating" to 4.9,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection(ORDERS).document(orderId)
            .update(updates)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    /** Finaliza la entrega con foto de respaldo obligatoria y nota opcional. */
    fun completeDelivery(
        orderId: String,
        proofImageUri: String,
        deliveryNote: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        val updates = mapOf(
            "status" to OrderStatus.ENTREGADO.key,
            "estado" to OrderStatus.ENTREGADO.label,
            "proofImageUri" to proofImageUri,
            "deliveryNote" to deliveryNote.trim(),
            "deliveredAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection(ORDERS).document(orderId)
            .update(updates)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    private fun DocumentSnapshot.toDeliveryOrderOrNull(): DeliveryOrder? {
        val restaurantName = getString("restaurantName") ?: return null
        val rawItems = (get("items") ?: get("products") ?: get("lines")) as? List<*>
        val itemCount = rawItems?.sumOf {
            ((it as? Map<*, *>)?.get("quantity") as? Number)?.toInt() ?: 1
        } ?: 1

        val status = OrderStatus.fromKey(getString("status") ?: getString("estado"))
        val fee = (getLong("deliveryFee") ?: getLong("deliveryFeeCop") ?: 4500L).toInt().coerceAtLeast(3000)
        val eta = (getLong("etaMinutes") ?: 15L).toInt()
        val created = getTimestamp("createdAt")?.toDate()?.time
            ?: getLong("createdAt")
            ?: System.currentTimeMillis()

        val parsedRestaurantId = get("restaurantId")?.toString().orEmpty()
        val parsedOrderNumber = get("orderNumber")?.toString() ?: id.take(4).uppercase()

        return DeliveryOrder(
            id = id,
            orderNumber = parsedOrderNumber,
            restaurantId = parsedRestaurantId,
            restaurantName = restaurantName,
            pickupAddress = getString("pickupAddress") ?: "Cra 43A #7-50, El Poblado",
            deliveryAddress = getString("address") ?: getString("customerAddress") ?: "El Poblado, Medellín",
            customerName = getString("customerName") ?: "Cliente",
            customerPhone = getString("customerPhone") ?: "",
            itemCount = itemCount,
            distanceKm = getDouble("distanceKm") ?: 2.2,
            etaMinutes = eta,
            earningCop = fee,
            status = status,
            courierId = getString("courierId").orEmpty(),
            createdAtMillis = created,
            deliveryNote = getString("deliveryNote").orEmpty(),
            proofImageUri = getString("proofImageUri")
        )
    }
}
