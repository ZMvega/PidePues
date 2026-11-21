package co.edu.udea.compumovil.labs20262_gr08.pidepues.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Reseña de un cliente sobre un restaurante. */
data class Review(
    val id: String,
    val restaurantId: String,
    val customerName: String,
    val rating: Float = 5f,
    val comment: String = "",
    val createdAtMillis: Long = System.currentTimeMillis(),
    val reply: String = ""
) {
    val customerInitial: String
        get() = customerName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "C"

    val ratingInt: Int
        get() = rating.toInt().coerceIn(1, 5)

    val hasReply: Boolean
        get() = reply.isNotBlank()

    val formattedRelativeDate: String
        get() {
            val todayStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val yesterdayStart = Calendar.getInstance().apply {
                timeInMillis = todayStart
                add(Calendar.DAY_OF_MONTH, -1)
            }.timeInMillis

            val reviewStartOfDay = Calendar.getInstance().apply {
                timeInMillis = createdAtMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            return when {
                reviewStartOfDay >= todayStart -> "Hoy"
                reviewStartOfDay >= yesterdayStart -> "Ayer"
                else -> {
                    val diffDays = ((todayStart - reviewStartOfDay) / (24 * 60 * 60 * 1000)).toInt()
                    if (diffDays in 2..30) {
                        "Hace $diffDays días"
                    } else {
                        SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(createdAtMillis))
                    }
                }
            }
        }
}
