package co.edu.udea.compumovil.labs20262_gr08.pidepues.model

/**
 * Perfil completo y configuración del restaurante en Cloud Firestore (restaurants/{uid}).
 */
data class RestaurantProfile(
    val uid: String = "",
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val logoUrl: String? = null,
    val bankName: String = "",
    val accountType: String = ACCOUNT_TYPE_SAVINGS,
    val accountNumber: String = "",
    val accountHolder: String = "",
    val notifyNewOrders: Boolean = true,
    val notifyNewReviews: Boolean = true,
    val notifyAlertSound: Boolean = true,
    val notifyDailySummary: Boolean = false
) {
    val initial: String
        get() = name.trim().firstOrNull()?.uppercaseChar()?.toString()
            ?: "R"

    companion object {
        const val ACCOUNT_TYPE_SAVINGS = "Ahorros"
        const val ACCOUNT_TYPE_CHECKING = "Corriente"
    }
}
