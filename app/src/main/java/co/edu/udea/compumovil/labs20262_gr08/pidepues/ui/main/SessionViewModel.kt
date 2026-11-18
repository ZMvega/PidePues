package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main

import androidx.lifecycle.ViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.AuthRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Perfil del usuario con sesión iniciada (se descarga una vez y lo comparten Inicio, Perfil y roles). */
class SessionViewModel : ViewModel() {

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    /** Guarda un perfil ya conocido (por ejemplo, el que devolvió el inicio de sesión). */
    fun set(profile: UserProfile) {
        _profile.value = profile
    }

    fun load() {
        AuthRepository.loadProfile { result ->
            result.getOrNull()?.let { _profile.value = it }
        }
    }

    fun clear() {
        _profile.value = null
    }
}
