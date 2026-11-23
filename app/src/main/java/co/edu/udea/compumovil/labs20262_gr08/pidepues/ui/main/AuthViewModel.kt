package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.AuthRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserRole
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.authMessageRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Estado de las pantallas de inicio de sesión y registro (cargando / mensaje). */
class AuthViewModel : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        /** Mensaje para mostrar al usuario (error o aviso). */
        @StringRes val messageRes: Int? = null,
        val isError: Boolean = true
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    fun clearMessage() {
        _state.value = _state.value.copy(messageRes = null)
    }

    fun login(email: String, password: String, onSuccess: (UserProfile) -> Unit) {
        _state.value = UiState(isLoading = true)
        AuthRepository.login(email, password) { result ->
            val profile = result.getOrNull()
            if (profile != null) {
                _state.value = UiState()
                onSuccess(profile)
            } else {
                _state.value = UiState(messageRes = result.exceptionOrNull()?.authMessageRes() ?: R.string.auth_error_generic)
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        phone: String,
        password: String,
        role: UserRole,
        documentType: String = "CC",
        documentNumber: String = "",
        vehicleType: String = "Moto",
        vehiclePlate: String = "",
        onSuccess: (UserProfile) -> Unit
    ) {
        _state.value = UiState(isLoading = true)
        AuthRepository.register(
            fullName, email, phone, password, role,
            documentType, documentNumber, vehicleType, vehiclePlate
        ) { result ->
            val profile = result.getOrNull()
            if (profile != null) {
                _state.value = UiState()
                onSuccess(profile)
            } else {
                _state.value = UiState(messageRes = result.exceptionOrNull()?.authMessageRes() ?: R.string.auth_error_generic)
            }
        }
    }

    fun sendPasswordReset(email: String) {
        AuthRepository.sendPasswordReset(email) { result ->
            _state.value = if (result.isSuccess) {
                UiState(messageRes = R.string.reset_email_sent, isError = false)
            } else {
                UiState(messageRes = result.exceptionOrNull()?.authMessageRes() ?: R.string.auth_error_generic)
            }
        }
    }
}
