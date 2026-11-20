package co.edu.udea.compumovil.labs20262_gr08.pidepues.model

import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Estados del pedido según el flujo operativo de la app. */
enum class OrderStatus(val key: String, val label: String) {
    SOLICITADO("solicitado", "Solicitado"),
    TOMADO("tomado", "Tomado"),
    PREPARADO("preparado", "Preparado"),
    EN_CAMINO("en_camino", "En camino"),
    ENTREGADO("entregado", "Entregado");

    fun labelRes(): Int = when (this) {
        SOLICITADO -> R.string.orders_status_solicitado
        TOMADO -> R.string.orders_status_tomado
        PREPARADO -> R.string.orders_status_preparado
        EN_CAMINO -> R.string.orders_status_en_camino
        ENTREGADO -> R.string.orders_status_entregado
    }

    companion object {
        fun fromKey(key: String?): OrderStatus {
            if (key == null) return SOLICITADO
            val normalized = key.trim().lowercase().replace(" ", "_")
            return when (normalized) {
                "solicitado", "nuevo", "pending", "requested" -> SOLICITADO
                "tomado", "aceptado", "en_preparacion", "in_progress", "preparing" -> TOMADO
                "preparado", "listo", "ready" -> PREPARADO
                "en_camino", "on_the_way", "dispatched", "delivering" -> EN_CAMINO
                "entregado", "completado", "completed", "delivered" -> ENTREGADO
                else -> entries.firstOrNull { it.key.equals(normalized, ignoreCase = true) } ?: SOLICITADO
            }
        }
    }
}

/** Producto individual dentro de un pedido. */
data class OrderItem(
    val name: String,
    val quantity: Int,
    val price: Int = 0,
    val total: Int = price * quantity,
)

/** Pedido real de Firestore asociado a un restaurante. */
data class RestaurantOrder(
    val id: String,
    val orderNumber: String,
    val restaurantId: String,
    val customerName: String,
    val customerPhone: String = "",
    val customerAddress: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalCop: Int = 0,
    val subtotalCop: Int = 0,
    val deliveryFeeCop: Int = 0,
    val status: OrderStatus = OrderStatus.SOLICITADO,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val customerNote: String = "",
    val paymentMethod: String = "Efectivo",
    val courierName: String = "",
    val courierPhone: String = "",
) {
    val itemCount: Int
        get() = if (items.isNotEmpty()) items.sumOf { it.quantity } else 0

    val itemsSummary: String
        get() = items.joinToString(", ") { "${it.quantity}× ${it.name}" }

    val formattedTime: String
        get() {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(createdAtMillis))
        }

    /** Fecha relativa y hora: ej. "Hoy · 12:41" o "20 nov · 12:41" */
    val formattedDateTime: String
        get() {
            val now = Calendar.getInstance()
            val orderCal = Calendar.getInstance().apply { timeInMillis = createdAtMillis }
            val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(createdAtMillis))
            return when {
                now.get(Calendar.YEAR) == orderCal.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) == orderCal.get(Calendar.DAY_OF_YEAR) -> "Hoy · $timeStr"
                now.get(Calendar.YEAR) == orderCal.get(Calendar.YEAR) &&
                    now.get(Calendar.DAY_OF_YEAR) - orderCal.get(Calendar.DAY_OF_YEAR) == 1 -> "Ayer · $timeStr"
                else -> {
                    val dateStr = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(createdAtMillis))
                    "$dateStr · $timeStr"
                }
            }
        }

    val customerInitial: String
        get() = customerName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "C"

    val stepIndex: Int
        get() = when (status) {
            OrderStatus.SOLICITADO -> 1
            OrderStatus.TOMADO -> 2
            OrderStatus.PREPARADO -> 3
            OrderStatus.EN_CAMINO -> 4
            OrderStatus.ENTREGADO -> 5
        }

    val isActive: Boolean
        get() = status != OrderStatus.ENTREGADO
}
