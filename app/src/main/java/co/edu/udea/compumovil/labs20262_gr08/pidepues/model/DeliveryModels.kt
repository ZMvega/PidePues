package co.edu.udea.compumovil.labs20262_gr08.pidepues.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class DeliveryHistoryFilter(val label: String, val days: Int) {
    TODAY("Hoy", 1),
    DAYS_7("7 días", 7),
    DAYS_30("30 días", 30)
}

/** Pedido visto desde el flujo del domiciliario. */
data class DeliveryOrder(
    val id: String,
    val orderNumber: String,
    val restaurantId: String,
    val restaurantName: String,
    val pickupAddress: String,
    val deliveryAddress: String,
    val customerName: String,
    val customerPhone: String,
    val itemCount: Int,
    val distanceKm: Double,
    val etaMinutes: Int,
    val earningCop: Int,
    val status: OrderStatus,
    val courierId: String = "",
    val createdAtMillis: Long = System.currentTimeMillis(),
    val deliveryNote: String = "",
    val proofImageUri: String? = null
) {
    val restaurantInitial: String
        get() = restaurantName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "R"

    val customerInitial: String
        get() = customerName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "C"

    val shortCode: String
        get() = "#PP-$orderNumber"

    val formattedTime: String
        get() = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(createdAtMillis))

    val formattedFullDate: String
        get() = SimpleDateFormat("dd 'de' MMM 'de' yyyy · HH:mm", Locale("es", "CO"))
            .format(Date(createdAtMillis))
}

/** Afiliación o invitación de un restaurante hacia el repartidor. */
data class RestaurantAffiliation(
    val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val restaurantAddress: String,
    val status: String = "pending", // "pending" | "accepted" | "rejected"
    val timeLabel: String = "Hoy"
) {
    val initial: String
        get() = restaurantName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "R"

    val isPending: Boolean get() = status == "pending"
    val isAccepted: Boolean get() = status == "accepted"
}