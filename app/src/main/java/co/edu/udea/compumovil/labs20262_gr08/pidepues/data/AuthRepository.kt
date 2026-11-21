package co.edu.udea.compumovil.labs20262_gr08.pidepues.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import co.edu.udea.compumovil.labs20262_gr08.pidepues.domain.Validators
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserRole

/**
 * Sesión y perfil con Firebase.
 *  - Firebase Authentication: correo + contraseña.
 *  - Firestore: users/{uid} con nombre, correo, celular y rol.
 * Todas las llamadas devuelven el resultado en el hilo principal.
 */
object AuthRepository {

    private const val USERS = "users"

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    val isLoggedIn: Boolean get() = auth.currentUser != null

    fun register(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        role: UserRole,
        onResult: (Result<UserProfile>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) {
                    onResult(Result.failure(IllegalStateException("Cuenta creada sin usuario")))
                    return@addOnSuccessListener
                }
                val profile = UserProfile(
                    uid = user.uid,
                    fullName = fullName.trim(),
                    email = email.trim(),
                    phone = Validators.normalizePhone(phone),
                    role = role
                )
                val data = hashMapOf(
                    "fullName" to profile.fullName,
                    "email" to profile.email,
                    "phone" to profile.phone,
                    "role" to role.key,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                db.collection(USERS).document(user.uid).set(data)
                    .addOnSuccessListener { onResult(Result.success(profile)) }
                    .addOnFailureListener { error ->
                        // Sin perfil la cuenta quedaría incompleta: se elimina para poder reintentar
                        user.delete()
                        onResult(Result.failure(error))
                    }
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun login(email: String, password: String, onResult: (Result<UserProfile>) -> Unit) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { loadProfile(onResult) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    /** Lee el perfil del usuario con sesión iniciada. */
    fun loadProfile(onResult: (Result<UserProfile>) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onResult(Result.failure(IllegalStateException("No hay sesión iniciada")))
            return
        }
        db.collection(USERS).document(user.uid).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    onResult(Result.failure(IllegalStateException("La cuenta no tiene perfil")))
                    return@addOnSuccessListener
                }
                onResult(
                    Result.success(
                        UserProfile(
                            uid = user.uid,
                            fullName = doc.getString("fullName").orEmpty(),
                            email = doc.getString("email") ?: user.email.orEmpty(),
                            phone = doc.getString("phone").orEmpty(),
                            role = UserRole.fromKey(doc.getString("role"))
                        )
                    )
                )
            }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun sendPasswordReset(email: String, onResult: (Result<Unit>) -> Unit) {
        auth.sendPasswordResetEmail(email.trim())
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(it)) }
    }

    fun logout() = auth.signOut()
}
