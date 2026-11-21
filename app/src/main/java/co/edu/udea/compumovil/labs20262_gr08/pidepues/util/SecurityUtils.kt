package co.edu.udea.compumovil.labs20262_gr08.pidepues.util

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Utilidades de cifrado autenticado (AES-256-GCM) para proteger datos sensibles
 * de ubicación, direcciones o referencias de pago antes de almacenarlos localmente.
 */
object SecurityUtils {

    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_SIZE_BYTES = 12
    private const val TAG_SIZE_BITS = 128

    fun generateKey(): SecretKey = KeyGenerator.getInstance("AES").apply {
        init(256)
    }.generateKey()

    /** Cifra [plainText] con IV aleatorio y devuelve el resultado en formato hexadecimal (IV + cifrado). */
    fun encrypt(plainText: String, key: SecretKey): String {
        val iv = ByteArray(IV_SIZE_BYTES).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_SIZE_BITS, iv))
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return (iv + encrypted).toHex()
    }

    /** Descifra una cadena hexadecimal generada por [encrypt]. Lanza excepción si fue alterada. */
    fun decrypt(hexPayload: String, key: SecretKey): String {
        val bytes = hexPayload.fromHex()
        require(bytes.size > IV_SIZE_BYTES) { "Payload cifrado inválido" }
        val iv = bytes.copyOfRange(0, IV_SIZE_BYTES)
        val cipherText = bytes.copyOfRange(IV_SIZE_BYTES, bytes.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_SIZE_BITS, iv))
        return String(cipher.doFinal(cipherText), Charsets.UTF_8)
    }

    /** Enmascara números de teléfono o referencias sensibles para logs y UI (ej. "*******8821"). */
    fun maskSensitive(value: String, visibleEnd: Int = 4): String {
        val clean = value.trim()
        if (clean.length <= visibleEnd) return "*".repeat(clean.length)
        return "*".repeat(clean.length - visibleEnd) + clean.takeLast(visibleEnd)
    }

    private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

    private fun String.fromHex(): ByteArray {
        require(length % 2 == 0) { "Hex inválido" }
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
}