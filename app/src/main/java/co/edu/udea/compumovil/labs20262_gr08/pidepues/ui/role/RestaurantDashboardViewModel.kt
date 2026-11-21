package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.RestaurantRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuItem
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderStatus
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantOrder
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantProfile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Review
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

data class RestaurantDashboardOrder(
    val id: String,
    val customerName: String,
    val itemCount: Int,
    val totalCop: Int,
)

data class RestaurantDashboardUiState(
    val dailyRevenueCop: Int = 0,
    val completedOrders: Int = 0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val newOrders: List<RestaurantDashboardOrder> = emptyList(),
    val inProgressOrders: List<RestaurantDashboardOrder> = emptyList(),
    val orderHistory: List<RestaurantDashboardOrder> = emptyList(),
    val menuItems: List<MenuItem> = emptyList(),
    val restaurantDocumentId: String? = null,
    val isMenuLoading: Boolean = false,
    val isSavingMenuItem: Boolean = false,
    @param:StringRes val menuMessageRes: Int? = null,
    val allOrders: List<RestaurantOrder> = emptyList(),
    val selectedFilter: OrderStatus = OrderStatus.SOLICITADO,
    val isOrdersLoading: Boolean = false,
    val ordersError: String? = null,
    val reviews: List<Review> = emptyList(),
    val selectedStarFilter: Int = 0,
    val isReviewsLoading: Boolean = false,
    val reviewsError: String? = null,
    val restaurantProfile: RestaurantProfile = RestaurantProfile(),
    val isProfileLoading: Boolean = false,
    val isSavingProfile: Boolean = false,
    val profileSaveSuccess: Boolean = false,
    @param:StringRes val profileMessageRes: Int? = null,
    val courierInviteSuccess: Boolean = false,
    @param:StringRes val courierInviteMessageRes: Int? = null,
) {
    val solicitadoCount: Int get() = allOrders.count { it.status == OrderStatus.SOLICITADO }
    val tomadoCount: Int get() = allOrders.count { it.status == OrderStatus.TOMADO }
    val preparadoCount: Int get() = allOrders.count { it.status == OrderStatus.PREPARADO }
    val enCaminoCount: Int get() = allOrders.count { it.status == OrderStatus.EN_CAMINO }
    val entregadoCount: Int get() = allOrders.count { it.status == OrderStatus.ENTREGADO }

    /** Pedidos activos (no entregados): Solicitado + Tomado + Preparado + En camino */
    val activeOrdersCount: Int get() = allOrders.count { it.isActive }

    /** Lista de pedidos filtrados según la pestaña de estado seleccionada */
    val filteredOrders: List<RestaurantOrder>
        get() = allOrders.filter { it.status == selectedFilter }

    /** Promedio formateado a 1 decimal (ej. "4.1" o "0.0") */
    val formattedRating: String
        get() = if (reviewCount > 0) String.format(Locale.US, "%.1f", rating) else "0.0"

    val star5Count: Int get() = reviews.count { it.ratingInt == 5 }
    val star4Count: Int get() = reviews.count { it.ratingInt == 4 }
    val star3Count: Int get() = reviews.count { it.ratingInt == 3 }
    val star2Count: Int get() = reviews.count { it.ratingInt == 2 }
    val star1Count: Int get() = reviews.count { it.ratingInt == 1 }

    fun starProgress(starLevel: Int): Float {
        if (reviewCount == 0) return 0f
        val count = when (starLevel) {
            5 -> star5Count
            4 -> star4Count
            3 -> star3Count
            2 -> star2Count
            1 -> star1Count
            else -> 0
        }
        return (count.toFloat() / reviewCount).coerceIn(0f, 1f)
    }

    /** Lista de reseñas filtradas por la cantidad de estrellas seleccionada */
    val filteredReviews: List<Review>
        get() = if (selectedStarFilter == 0) reviews else reviews.filter { it.ratingInt == selectedStarFilter }
}

class RestaurantDashboardViewModel : ViewModel() {

    private val _state = MutableStateFlow(RestaurantDashboardUiState())
    val state: StateFlow<RestaurantDashboardUiState> = _state.asStateFlow()

    private var menuOwnerUid: String? = null
    private var menuListener: ListenerRegistration? = null

    private var ordersOwnerUid: String? = null
    private var ordersListener: ListenerRegistration? = null

    fun observeMenu(ownerUid: String, restaurantName: String) {
        if (menuOwnerUid == ownerUid) return
        menuListener?.remove()
        menuListener = null
        menuOwnerUid = ownerUid
        _state.value = _state.value.copy(
            menuItems = emptyList(),
            restaurantDocumentId = null,
            isMenuLoading = true,
            menuMessageRes = null
        )
        RestaurantRepository.findOrCreateOwnedRestaurant(ownerUid, restaurantName) { result ->
            if (menuOwnerUid != ownerUid) return@findOrCreateOwnedRestaurant
            val documentId = result.getOrNull()
            if (documentId == null) {
                _state.value = _state.value.copy(
                    isMenuLoading = false,
                    menuMessageRes = R.string.restaurant_menu_load_error
                )
                return@findOrCreateOwnedRestaurant
            }
            _state.value = _state.value.copy(restaurantDocumentId = documentId)
            val registration = RestaurantRepository.listenMenu(documentId) { menuResult ->
                if (menuOwnerUid != ownerUid) return@listenMenu
                val items = menuResult.getOrNull()
                _state.value = _state.value.copy(
                    menuItems = items ?: _state.value.menuItems,
                    isMenuLoading = false,
                    menuMessageRes = if (items == null) R.string.restaurant_menu_load_error else null
                )
            }
            if (menuOwnerUid == ownerUid) {
                menuListener = registration
            } else {
                registration.remove()
            }
        }
    }

    fun retryMenu(ownerUid: String, restaurantName: String) {
        menuListener?.remove()
        menuListener = null
        menuOwnerUid = null
        observeMenu(ownerUid, restaurantName)
    }

    fun stopObservingMenu() {
        menuOwnerUid = null
        menuListener?.remove()
        menuListener = null
        _state.value = _state.value.copy(
            menuItems = emptyList(),
            restaurantDocumentId = null,
            isMenuLoading = false
        )
    }

    fun observeOrders(ownerUid: String) {
        if (ordersOwnerUid == ownerUid && ordersListener != null) return
        ordersListener?.remove()
        ordersListener = null
        ordersOwnerUid = ownerUid
        _state.value = _state.value.copy(isOrdersLoading = true)

        ordersListener = RestaurantRepository.listenRestaurantOrders(ownerUid) { result ->
            if (ordersOwnerUid != ownerUid) return@listenRestaurantOrders
            val orders = result.getOrNull()
            if (orders != null) {
                val newOrders = orders.filter { it.status == OrderStatus.SOLICITADO }.map { it.toDashboardOrder() }
                val inProgress = orders.filter { it.isActive }.map { it.toDashboardOrder() }
                val history = orders.filter { it.status == OrderStatus.ENTREGADO }.map { it.toDashboardOrder() }
                val completed = orders.count { it.status == OrderStatus.ENTREGADO }
                val dailyRevenue = orders.filter { it.status == OrderStatus.ENTREGADO }.sumOf { it.totalCop }

                _state.value = _state.value.copy(
                    allOrders = orders,
                    newOrders = newOrders,
                    inProgressOrders = inProgress,
                    orderHistory = history,
                    completedOrders = completed,
                    dailyRevenueCop = dailyRevenue,
                    isOrdersLoading = false,
                    ordersError = null
                )
            } else {
                _state.value = _state.value.copy(
                    isOrdersLoading = false,
                    ordersError = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun stopObservingOrders() {
        ordersOwnerUid = null
        ordersListener?.remove()
        ordersListener = null
        _state.value = _state.value.copy(
            allOrders = emptyList(),
            isOrdersLoading = false
        )
    }

    fun selectOrderFilter(filter: OrderStatus) {
        _state.value = _state.value.copy(selectedFilter = filter)
    }

    fun acceptOrder(order: RestaurantOrder, onComplete: (Boolean) -> Unit = {}) {
        advanceOrderStatus(order, OrderStatus.TOMADO, onComplete)
    }

    fun markOrderAsPrepared(order: RestaurantOrder, onComplete: (Boolean) -> Unit = {}) {
        advanceOrderStatus(order, OrderStatus.PREPARADO, onComplete)
    }

    fun markOrderAsEnCamino(order: RestaurantOrder, onComplete: (Boolean) -> Unit = {}) {
        advanceOrderStatus(order, OrderStatus.EN_CAMINO, onComplete)
    }

    fun advanceOrderStatus(order: RestaurantOrder, nextStatus: OrderStatus, onComplete: (Boolean) -> Unit = {}) {
        RestaurantRepository.updateOrderStatus(order.id, nextStatus) { result ->
            if (result.isSuccess) {
                _state.value = _state.value.copy(
                    allOrders = _state.value.allOrders.map {
                        if (it.id == order.id) it.copy(status = nextStatus) else it
                    }
                )
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    fun setAvailability(item: MenuItem, isAvailable: Boolean) {
        val documentId = _state.value.restaurantDocumentId
        if (documentId == null) {
            _state.value = _state.value.copy(menuMessageRes = R.string.restaurant_menu_update_error)
            return
        }
        _state.value = _state.value.copy(
            menuItems = _state.value.menuItems.map {
                if (it.id == item.id) it.copy(isAvailable = isAvailable) else it
            },
            menuMessageRes = null
        )
        RestaurantRepository.setMenuItemAvailability(documentId, item.id, isAvailable) { result ->
            if (result.isFailure) {
                _state.value = _state.value.copy(
                    menuItems = _state.value.menuItems.map {
                        if (it.id == item.id) it.copy(isAvailable = item.isAvailable) else it
                    },
                    menuMessageRes = R.string.restaurant_menu_update_error
                )
            }
        }
    }

    fun saveMenuItem(item: MenuItem, onComplete: (Boolean) -> Unit) {
        val documentId = _state.value.restaurantDocumentId
        if (documentId == null) {
            _state.value = _state.value.copy(menuMessageRes = R.string.restaurant_menu_save_error)
            onComplete(false)
            return
        }
        _state.value = _state.value.copy(isSavingMenuItem = true, menuMessageRes = null)
        RestaurantRepository.saveMenuItem(documentId, item) { result ->
            _state.value = _state.value.copy(
                isSavingMenuItem = false,
                menuMessageRes = if (result.isFailure) R.string.restaurant_menu_save_error else null
            )
            onComplete(result.isSuccess)
        }
    }

    fun clearMenuMessage() {
        _state.value = _state.value.copy(menuMessageRes = null)
    }

    fun observeReviews(ownerUid: String) {
        if (reviewsOwnerUid == ownerUid && reviewsListener != null) return
        reviewsListener?.remove()
        reviewsListener = null
        reviewsOwnerUid = ownerUid
        _state.value = _state.value.copy(isReviewsLoading = true)

        reviewsListener = RestaurantRepository.listenRestaurantReviews(ownerUid) { result ->
            if (reviewsOwnerUid != ownerUid) return@listenRestaurantReviews
            val reviews = result.getOrNull()
            if (reviews != null) {
                val count = reviews.size
                val rawAvg = if (count > 0) reviews.sumOf { it.rating.toDouble() } / count else 0.0
                val roundedAvg = (Math.round(rawAvg * 10.0) / 10.0)

                _state.value = _state.value.copy(
                    reviews = reviews,
                    rating = roundedAvg,
                    reviewCount = count,
                    isReviewsLoading = false,
                    reviewsError = null
                )
            } else {
                _state.value = _state.value.copy(
                    isReviewsLoading = false,
                    reviewsError = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun stopObservingReviews() {
        reviewsOwnerUid = null
        reviewsListener?.remove()
        reviewsListener = null
        _state.value = _state.value.copy(
            reviews = emptyList(),
            isReviewsLoading = false
        )
    }

    fun selectStarFilter(star: Int) {
        _state.value = _state.value.copy(selectedStarFilter = star)
    }

    fun replyToReview(reviewId: String, replyText: String, onComplete: (Boolean) -> Unit = {}) {
        val ownerUid = reviewsOwnerUid ?: _state.value.restaurantDocumentId ?: return
        RestaurantRepository.replyToReview(ownerUid, reviewId, replyText) { result ->
            if (result.isSuccess) {
                _state.value = _state.value.copy(
                    reviews = _state.value.reviews.map {
                        if (it.id == reviewId) it.copy(reply = replyText.trim()) else it
                    }
                )
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    private var reviewsOwnerUid: String? = null
    private var reviewsListener: ListenerRegistration? = null

    private var profileOwnerUid: String? = null
    private var profileListener: ListenerRegistration? = null

    fun observeProfile(ownerUid: String, fallbackProfile: UserProfile? = null) {
        if (profileOwnerUid == ownerUid && profileListener != null) return
        profileListener?.remove()
        profileListener = null
        profileOwnerUid = ownerUid
        _state.value = _state.value.copy(isProfileLoading = true)

        profileListener = RestaurantRepository.listenRestaurantProfile(ownerUid) { result ->
            if (profileOwnerUid != ownerUid) return@listenRestaurantProfile
            val loaded = result.getOrNull()
            if (loaded != null) {
                val merged = loaded.copy(
                    name = loaded.name.ifBlank { fallbackProfile?.fullName.orEmpty() },
                    email = loaded.email.ifBlank { fallbackProfile?.email.orEmpty() },
                    phone = loaded.phone.ifBlank { fallbackProfile?.phone.orEmpty() }
                )
                _state.value = _state.value.copy(
                    restaurantProfile = merged,
                    isProfileLoading = false
                )
            } else {
                _state.value = _state.value.copy(isProfileLoading = false)
            }
        }
    }

    fun stopObservingProfile() {
        profileOwnerUid = null
        profileListener?.remove()
        profileListener = null
    }

    fun saveRestaurantProfile(profile: RestaurantProfile, onComplete: (Boolean) -> Unit = {}) {
        _state.value = _state.value.copy(isSavingProfile = true, profileMessageRes = null)
        RestaurantRepository.saveRestaurantProfile(profile) { result ->
            val success = result.isSuccess
            _state.value = _state.value.copy(
                isSavingProfile = false,
                restaurantProfile = if (success) profile else _state.value.restaurantProfile,
                profileSaveSuccess = success,
                profileMessageRes = if (success) R.string.profile_save_success else R.string.profile_save_error
            )
            onComplete(success)
        }
    }

    fun inviteCourier(restaurantId: String, phone: String, onComplete: (Boolean) -> Unit = {}) {
        RestaurantRepository.inviteCourier(restaurantId, phone) { result ->
            val success = result.isSuccess
            _state.value = _state.value.copy(
                courierInviteSuccess = success,
                courierInviteMessageRes = if (success) R.string.profile_invite_success else R.string.profile_save_error
            )
            onComplete(success)
        }
    }

    fun clearProfileMessage() {
        _state.value = _state.value.copy(profileMessageRes = null)
    }

    fun clearCourierMessage() {
        _state.value = _state.value.copy(courierInviteMessageRes = null)
    }

    fun stopAllObserving() {
        stopObservingMenu()
        stopObservingOrders()
        stopObservingReviews()
        stopObservingProfile()
    }

    override fun onCleared() {
        stopAllObserving()
    }

    private fun RestaurantOrder.toDashboardOrder() = RestaurantDashboardOrder(
        id = id,
        customerName = customerName,
        itemCount = itemCount,
        totalCop = totalCop
    )
}
