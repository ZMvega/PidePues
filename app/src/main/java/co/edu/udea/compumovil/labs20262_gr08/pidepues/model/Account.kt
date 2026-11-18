package co.edu.udea.compumovil.labs20262_gr08.pidepues.model

/** Rol de la cuenta. [key] es el valor guardado en Firestore (users/{uid}.role). */
enum class UserRole(val key: String) {
    CLIENT("cliente"),
    RESTAURANT("restaurante"),
    DELIVERY("domiciliario");

    companion object {
        fun fromKey(key: String?): UserRole = values().firstOrNull { it.key == key } ?: CLIENT
    }
}

data class UserProfile(
    val uid: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: UserRole
) {
    val firstName: String get() = fullName.trim().substringBefore(" ")

    val initials: String
        get() = fullName.trim().split(" ").filter { it.isNotBlank() }
            .take(2).joinToString("") { it.first().uppercase() }
}
