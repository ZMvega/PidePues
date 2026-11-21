package co.edu.udea.compumovil.labs20262_gr08.pidepues.model

/** Estados de un pedido, en orden: recibido → preparando → en camino → entregado. */
enum class OrderStatus(val key: String) {
    RECEIVED("received"),
    PREPARING("preparing"),
    ON_THE_WAY("on_the_way"),
    DELIVERED("delivered");

    /** true mientras el pedido no ha sido entregado. */
    val isActive: Boolean get() = this != DELIVERED

    /** Siguiente estado, o null si ya fue entregado. */
    fun next(): OrderStatus? = values().getOrNull(ordinal + 1)

    companion object {
        fun fromKey(key: String?): OrderStatus = values().firstOrNull { it.key == key } ?: RECEIVED
    }
}

/** Formas de pago (por ahora se coordinan al recibir el pedido; no se procesa ningún cobro en línea). */
enum class PaymentMethod(val key: String) {
    CARD("tarjeta"),
    NEQUI("nequi"),
    DAVIPLATA("daviplata"),
    CASH("efectivo");

    companion object {
        fun fromKey(key: String?): PaymentMethod = values().firstOrNull { it.key == key } ?: CASH
    }
}

data class OrderLine(
    val name: String,
    val emoji: String,
    val unitPrice: Int,
    val quantity: Int
) {
    val total: Int get() = unitPrice * quantity
}

/** Pedido guardado en Firestore (orders/{id}). */
data class Order(
    val id: String,
    val restaurantId: Int,
    val restaurantName: String,
    val restaurantEmoji: String,
    val lines: List<OrderLine>,
    val subtotal: Int,
    val deliveryFee: Int,
    val serviceFee: Int,
    val total: Int,
    val address: String,
    val instructions: String,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus,
    val createdAtMillis: Long,
    /** Minutos totales estimados al crear el pedido (los del restaurante). */
    val etaMinutes: Int,
    val driverName: String? = null,
    val driverRating: Double? = null
) {
    /** Código corto para mostrar, ej. "#PP-AB3XK". */
    val shortCode: String get() = "#PP-" + id.take(5).uppercase()

    val itemCount: Int get() = lines.sumOf { it.quantity }

    /**
     * Minutos que faltan según el estado: baja a medida que el pedido avanza.
     * (Estimación simple; cuando haya domiciliarios reales se reemplaza por su ruta.)
     */
    fun remainingMinutes(): Int = when (status) {
        OrderStatus.RECEIVED -> etaMinutes
        OrderStatus.PREPARING -> maxOf(8, (etaMinutes * 0.7).toInt())
        OrderStatus.ON_THE_WAY -> maxOf(5, (etaMinutes * 0.35).toInt())
        OrderStatus.DELIVERED -> 0
    }
}

/** Dirección guardada del usuario (Firestore: users/{uid}/addresses/{id}). */
data class DeliveryAddress(
    val id: String,
    val label: String,
    val emoji: String,
    val text: String
)
