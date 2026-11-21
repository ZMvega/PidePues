package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.OrderRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Pedidos del cliente en tiempo real: un solo oyente de Firestore alimenta el historial,
 * el seguimiento y el aviso de "pedido en curso" de Inicio.
 */
class OrdersViewModel : ViewModel() {

    data class UiState(
        /** Más recientes primero. */
        val orders: List<Order> = emptyList(),
        val isLoading: Boolean = false,
        val loadError: Boolean = false
    ) {
        val active: List<Order> get() = orders.filter { it.status.isActive }
        val history: List<Order> get() = orders.filter { !it.status.isActive }
        fun find(id: String): Order? = orders.firstOrNull { it.id == id }
    }

    private var registration: ListenerRegistration? = null

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    /** Empieza a escuchar los pedidos del usuario (se llama al iniciar sesión como cliente). */
    fun start(customerId: String) {
        if (registration != null) return
        _state.value = UiState(isLoading = true)
        registration = OrderRepository.listenOrders(customerId) { result ->
            val list = result.getOrNull()
            _state.value = if (list != null) {
                UiState(orders = list.sortedByDescending { it.createdAtMillis })
            } else {
                UiState(loadError = true)
            }
        }
    }

    /** Deja de escuchar y limpia (se llama al cerrar sesión). */
    fun stop() {
        registration?.remove()
        registration = null
        _state.value = UiState()
    }

    /** SOLO PRUEBAS: simula que el restaurante / domiciliario avanzan el estado. */
    fun simulateNextStatus(order: Order, onError: () -> Unit) {
        OrderRepository.simulateNextStatus(order) { result ->
            if (result.isFailure) onError()
        }
    }

    override fun onCleared() {
        registration?.remove()
        registration = null
    }
}
