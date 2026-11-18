package co.edu.udea.compumovil.labs20262_gr08.pidepues.domain

/** Reglas de los formularios de registro e inicio de sesión (sin Android, fáciles de probar). */
object Validators {

    private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    const val MIN_PASSWORD_LENGTH = 8

    fun isValidEmail(email: String): Boolean = EMAIL.matches(email.trim())

    fun isValidPassword(password: String): Boolean = password.length >= MIN_PASSWORD_LENGTH

    /** Al menos dos letras; el formulario pide "Nombre completo". */
    fun isValidFullName(name: String): Boolean = name.trim().length >= 3 && name.trim().any { it.isLetter() }

    /** Celular colombiano: 10 dígitos que empiezan por 3, con o sin +57. */
    fun isValidPhone(phone: String): Boolean {
        val digits = phone.filter { it.isDigit() }
        val national = if (digits.length == 12 && digits.startsWith("57")) digits.drop(2) else digits
        return national.length == 10 && national.startsWith("3")
    }

    /** Guarda siempre el celular en formato +57XXXXXXXXXX. */
    fun normalizePhone(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        val national = if (digits.length == 12 && digits.startsWith("57")) digits.drop(2) else digits
        return "+57$national"
    }
}
