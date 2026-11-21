package co.edu.udea.compumovil.labs20262_gr08.pidepues.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.CartLine
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Order
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderLine
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderStatus
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.PaymentMethod
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Restaurant
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile

/**
 * Pedidos en Firestore: orders/{id}.
 * El cliente crea el pedido y lo ve actualizarse en tiempo real (addSnapshotListener).
 */
object OrderRepository {

    private const val ORDERS = "orders"

    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    /**
     * Crea un pedido en estado "recibido". [deliveryPoint] solo se guarda cuando el cliente
     * eligió "Tu ubicación actual" (dato de ubicación mínimo necesario para entregar).
     */
    fun placeOrder(
        customer: UserProfile,
        restaurant: Restaurant,
        lines: List<CartLine>,
        subtotal: Int,
        deliveryFee: Int,
        serviceFee: Int,
        total: Int,
        address: String,
        instructions: String,
        payment: PaymentMethod,
        deliveryPoint: GeoPoint?,
        onResult: (Result<String>) -> Unit
    ) {
        val data = hashMapOf<String, Any>(
            "customerId" to customer.uid,
            "customerName" to customer.fullName,
            "customerPhone" to customer.phone,
            "restaurantId" to restaurant.id,
            "restaurantName" to restaurant.name,
            "restaurantEmoji" to restaurant.emoji,
            "items" to lines.map {
                mapOf(
                    "name" to it.item.name,
                    "emoji" to it.item.emoji,
                    "unitPrice" to it.item.price,
                    "quantity" to it.quantity
                )
            },
            "subtotal" to subtotal,
            "deliveryFee" to deliveryFee,
            "serviceFee" to serviceFee,
            "total" to total,
            "address" to address,
            "instructions" to instructions,
            "paymentMethod" to payment.key,
            "status" to OrderStatus.SOLICITADO.key,
            "etaMinutes" to restaurant.deliveryMinutes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (deliveryPoint != null) data["deliveryLocation"] = deliveryPoint

        db.collection(ORDERS).add(data)
            .addOnSuccessListener { ref -> onResult(Result.success(ref.id)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    /** Escucha en tiempo real los pedidos del cliente. Devuelve el registro para cancelarlo. */
    fun listenOrders(customerId: String, onResult: (Result<List<Order>>) -> Unit): ListenerRegistration =
        db.collection(ORDERS).whereEqualTo("customerId", customerId)
            .addSnapshotListener { snapshot, error ->
                if (snapshot == null) {
                    onResult(Result.failure(error ?: IllegalStateException("Sin datos")))
                } else {
                    onResult(Result.success(snapshot.documents.mapNotNull { it.toOrderOrNull() }))
                }
            }

    /**
     * SOLO PRUEBAS (compilaciones de depuración): avanza el pedido al siguiente estado,
     * como lo harían el restaurante y el domiciliario. Al pasar a "en camino" asigna un
     * domiciliario de ejemplo.
     */
    fun simulateNextStatus(order: Order, onResult: (Result<Unit>) -> Unit) {
        val next = order.status.next()
        if (next == null) {
            onResult(Result.success(Unit))
            return
        }
        val update = hashMapOf<String, Any>(
            "status" to next.key,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (next == OrderStatus.EN_CAMINO) {
            update["driverName"] = "Andrés"
            update["driverRating"] = 4.9
        }
        db.collection(ORDERS).document(order.id).update(update)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    private fun DocumentSnapshot.toOrderOrNull(): Order? {
        val restaurantName = getString("restaurantName") ?: return null
        val rawItems = get("items") as? List<*> ?: emptyList<Any>()
        val lines = rawItems.mapNotNull { raw ->
            val item = raw as? Map<*, *> ?: return@mapNotNull null
            OrderLine(
                name = item["name"] as? String ?: return@mapNotNull null,
                emoji = item["emoji"] as? String ?: "",
                unitPrice = (item["unitPrice"] as? Number)?.toInt() ?: 0,
                quantity = (item["quantity"] as? Number)?.toInt() ?: 1
            )
        }
        return Order(
            id = id,
            restaurantId = getLong("restaurantId")?.toInt() ?: 0,
            restaurantName = restaurantName,
            restaurantEmoji = getString("restaurantEmoji").orEmpty(),
            lines = lines,
            subtotal = getLong("subtotal")?.toInt() ?: 0,
            deliveryFee = getLong("deliveryFee")?.toInt() ?: 0,
            serviceFee = getLong("serviceFee")?.toInt() ?: 0,
            total = getLong("total")?.toInt() ?: 0,
            address = getString("address").orEmpty(),
            instructions = getString("instructions").orEmpty(),
            paymentMethod = PaymentMethod.fromKey(getString("paymentMethod")),
            status = OrderStatus.fromKey(getString("status")),
            // Mientras Firestore confirma la hora del servidor, se usa una estimación local
            createdAtMillis = getTimestamp("createdAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                ?.toDate()?.time ?: System.currentTimeMillis(),
            etaMinutes = getLong("etaMinutes")?.toInt() ?: 30,
            driverName = getString("driverName"),
            driverRating = getDouble("driverRating"),
            rated = getBoolean("rated") ?: false
        )
    }
}
