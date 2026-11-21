package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common

import androidx.annotation.StringRes
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R

/** Traduce los errores de Firebase a mensajes claros para el usuario. */
@StringRes
fun Throwable.authMessageRes(): Int = when (this) {
    is FirebaseAuthUserCollisionException -> R.string.auth_error_email_in_use
    // WeakPassword hereda de InvalidCredentials: debe ir primero
    is FirebaseAuthWeakPasswordException -> R.string.auth_error_weak_password
    is FirebaseAuthInvalidUserException,
    is FirebaseAuthInvalidCredentialsException -> R.string.auth_error_credentials
    is FirebaseTooManyRequestsException -> R.string.auth_error_too_many
    is FirebaseNetworkException -> R.string.auth_error_network
    else -> R.string.auth_error_generic
}
