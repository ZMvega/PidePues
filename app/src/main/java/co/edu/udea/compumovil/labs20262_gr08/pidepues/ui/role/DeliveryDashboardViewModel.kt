package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role

import androidx.lifecycle.ViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.DeliveryRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.DeliveryHistoryFilter
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.DeliveryOrder
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderStatus
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantAffiliation
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DeliveryUiState(
    val gpsEnabled: Boolean = false,
    val gpsOnboardingSeen: Boolean = false,
    val isLoading: Boolean = false,
    val availableOrders: List<DeliveryOrder> = emptyList(),
    val activeOrders: List<DeliveryOrder> = emptyList(),
    val historyOrders: List<DeliveryOrder> = emptyList(),
    val affiliations: List<RestaurantAffiliation> = defaultAffiliations(),
    val historyFilter: DeliveryHistoryFilter = DeliveryHistoryFilter.DAYS_30,
    val historyPage: Int = 1,
    val message: String? = null
) {
    val maxActiveOrders: Int get() = 5
    val freeSlots: Int get() = (maxActiveOrders - activeOrders.size).coerceAtLeast(0)
    val canTakeMoreOrders: Boolean get() = gpsEnabled && activeOrders.size < maxActiveOrders
    val pendingInvitations: List<RestaurantAffiliation> get() = affiliations.filter { it.isPending }
    val acceptedAffiliations: List<RestaurantAffiliation> get() = affiliations.filter { it.isAccepted }

    val filteredHistory: List<DeliveryOrder>
        get() {
            val cutoff = System.currentTimeMillis() - historyFilter.days * 24L * 60L * 60L * 1000L
            return historyOrders.filter { it.createdAtMillis >= cutoff }
        }

    val periodEarningsCop: Int get() = filteredHistory.sumOf { it.earningCop }
    val totalPages: Int get() = maxOf(1, (filteredHistory.size + PAGE_SIZE - 1) / PAGE_SIZE)
    val pagedHistory: List<DeliveryOrder>
        get() {
            val start = (historyPage - 1) * PAGE_SIZE
            return filteredHistory.drop(start).take(PAGE_SIZE)
        }

    companion object {
        const val PAGE_SIZE = 10
        private fun defaultAffiliations() = listOf(
            RestaurantAffiliation("inv-1", "1", "Pizzería Napoli", "Cra 37 #8A-41, Lleras", "pending", "Hace 2 h"),
            RestaurantAffiliation("inv-2", "2", "Arepas & Co", "Cl 9 #43B-20, Manila", "pending", "Ayer"),
            RestaurantAffiliation("aff-1", "3", "La Parrilla de Don Pepe", "Cra 43A #7-50, El Poblado", "accepted", "Activo"),
            RestaurantAffiliation("aff-2", "4", "Sushi Zen", "Cl 10 #35-12, Provenza", "accepted", "Activo")
        )
    }
}

class DeliveryDashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(DeliveryUiState())
    val state: StateFlow<DeliveryUiState> = _state.asStateFlow()

    private var listener: ListenerRegistration? = null
    private var courierUid: String? = null
    private var customOrderIds: List<String> = emptyList()

    fun observe(uid: String) {
        if (courierUid == uid && listener != null) return
        stop()
        courierUid = uid
        _state.value = _state.value.copy(isLoading = true)

        listener = DeliveryRepository.listenDeliveryOrders { result ->
            val all = result.getOrNull().orEmpty()
            val available = all.filter { it.status == OrderStatus.PREPARADO }
            val activeRaw = all.filter {
                it.status == OrderStatus.EN_CAMINO && (it.courierId == uid || it.courierId.isBlank())
            }
            val activeOrdered = applyPriorityOrder(activeRaw)
            val history = all.filter {
                it.status == OrderStatus.ENTREGADO && (it.courierId == uid || it.courierId.isBlank())
            }
            _state.value = _state.value.copy(
                isLoading = false,
                availableOrders = available,
                activeOrders = activeOrdered,
                historyOrders = history
            )
        }
    }

    fun stop() {
        listener?.remove()
        listener = null
        courierUid = null
    }

    fun setGpsEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(gpsEnabled = enabled, gpsOnboardingSeen = true)
    }

    fun skipGpsOnboarding() {
        _state.value = _state.value.copy(gpsOnboardingSeen = true)
    }

    fun takeOrder(order: DeliveryOrder, courier: UserProfile) {
        if (!_state.value.canTakeMoreOrders) return
        DeliveryRepository.takeOrder(order.id, courier) { result ->
            _state.value = _state.value.copy(
                message = if (result.isSuccess) "Pedido #${order.orderNumber} asignado" else "No se pudo tomar el pedido"
            )
        }
    }

    fun completeDelivery(orderId: String, proofUri: String, note: String, onDone: (Boolean) -> Unit) {
        DeliveryRepository.completeDelivery(orderId, proofUri, note) { result ->
            val ok = result.isSuccess
            _state.value = _state.value.copy(
                message = if (ok) "¡Entrega completada!" else "No se pudo completar la entrega"
            )
            onDone(ok)
        }
    }

    fun moveOrderPriority(orderId: String, delta: Int) {
        val list = _state.value.activeOrders.toMutableList()
        val idx = list.indexOfFirst { it.id == orderId }
        if (idx == -1) return
        val newIdx = (idx + delta).coerceIn(0, list.lastIndex)
        if (newIdx == idx) return
        val item = list.removeAt(idx)
        list.add(newIdx, item)
        customOrderIds = list.map { it.id }
        _state.value = _state.value.copy(activeOrders = list)
    }

    fun deliverFirst(orderId: String) {
        val list = _state.value.activeOrders.toMutableList()
        val idx = list.indexOfFirst { it.id == orderId }
        if (idx <= 0) return
        val item = list.removeAt(idx)
        list.add(0, item)
        customOrderIds = list.map { it.id }
        _state.value = _state.value.copy(activeOrders = list)
    }

    fun acceptInvitation(id: String) {
        _state.value = _state.value.copy(
            affiliations = _state.value.affiliations.map {
                if (it.id == id) it.copy(status = "accepted") else it
            }
        )
    }

    fun rejectOrRemoveAffiliation(id: String) {
        _state.value = _state.value.copy(
            affiliations = _state.value.affiliations.filterNot { it.id == id }
        )
    }

    fun selectHistoryFilter(filter: DeliveryHistoryFilter) {
        _state.value = _state.value.copy(historyFilter = filter, historyPage = 1)
    }

    fun setHistoryPage(page: Int) {
        _state.value = _state.value.copy(historyPage = page.coerceIn(1, _state.value.totalPages))
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = null)
    }

    private fun applyPriorityOrder(orders: List<DeliveryOrder>): List<DeliveryOrder> {
        if (customOrderIds.isEmpty()) return orders
        val rank = customOrderIds.withIndex().associate { it.value to it.index }
        return orders.sortedBy { rank[it.id] ?: Int.MAX_VALUE }
    }

    override fun onCleared() {
        stop()
    }
}