package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ListenerRegistration
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.AddressRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.OrderRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.DeliveryAddress
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.PaymentMethod
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserLocation
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Estado del flujo de compra: dirección → método de pago → crear el pedido. */
class CheckoutViewModel : ViewModel() {

    data class UiState(
        val addresses: List<DeliveryAddress> = emptyList(),
        /** true = "Tu ubicación actual"; false = una dirección guardada. */
        val useCurrentLocation: Boolean = true,
        val selectedAddressId: String? = null,
        val instructions: String = "",
        val payment: PaymentMethod = PaymentMethod.CASH,
        val isPlacing: Boolean = false,
        @StringRes val messageRes: Int? = null
    ) {
        val selectedAddress: DeliveryAddress?
            get() = if (useCurrentLocation) null else addresses.firstOrNull { it.id == selectedAddressId }
    }

    private var registration: ListenerRegistration? = null

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    /** Empieza a escuchar las direcciones guardadas del usuario. */
    fun start(uid: String) {
        if (registration != null) return
        registration = AddressRepository.listen(uid) { list ->
            val sorted = list.sortedBy { it.label.lowercase() }
            val current = _state.value
            // Si la dirección elegida fue borrada, se vuelve a "ubicación actual"
            val stillThere = sorted.any { it.id == current.selectedAddressId }
            _state.value = current.copy(
                addresses = sorted,
                useCurrentLocation = current.useCurrentLocation || !stillThere,
                selectedAddressId = if (stillThere) current.selectedAddressId else null
            )
        }
    }

    fun stop() {
        registration?.remove()
        registration = null
        _state.value = UiState()
    }

    fun selectCurrentLocation() {
        _state.value = _state.value.copy(useCurrentLocation = true, selectedAddressId = null)
    }

    fun selectSaved(id: String) {
        _state.value = _state.value.copy(useCurrentLocation = false, selectedAddressId = id)
    }

    fun setInstructions(text: String) {
        _state.value = _state.value.copy(instructions = text)
    }

    fun setPayment(method: PaymentMethod) {
        _state.value = _state.value.copy(payment = method)
    }

    fun clearMessage() {
        _state.value = _state.value.copy(messageRes = null)
    }

    /** Guarda una dirección nueva y la deja seleccionada. */
    fun addAddress(uid: String, label: String, emoji: String, text: String) {
        AddressRepository.add(uid, label, emoji, text) { result ->
            val id = result.getOrNull()
            if (id != null) selectSaved(id)
            else _state.value = _state.value.copy(messageRes = R.string.address_error)
        }
    }

    /**
     * Crea el pedido con lo que hay en el carrito. [currentLocationText] es el texto de la
     * dirección cuando se eligió "Tu ubicación actual".
     */
    fun placeOrder(
        customer: UserProfile,
        cart: CartViewModel.CartState,
        device: UserLocation,
        currentLocationText: String,
        onPlaced: (String) -> Unit
    ) {
        val restaurant = cart.restaurant
        val current = _state.value
        if (restaurant == null || cart.isEmpty || current.isPlacing) return

        val selected = current.selectedAddress
        // La ubicación GPS solo se guarda si el cliente eligió "Tu ubicación actual"
        val point = if (current.useCurrentLocation && device.isFromDevice) {
            GeoPoint(device.latitude, device.longitude)
        } else {
            null
        }
        _state.value = current.copy(isPlacing = true, messageRes = null)
        OrderRepository.placeOrder(
            customer = customer,
            restaurant = restaurant,
            lines = cart.lines,
            subtotal = cart.subtotal,
            deliveryFee = cart.deliveryFee,
            serviceFee = cart.serviceFee,
            total = cart.total,
            address = selected?.text ?: currentLocationText,
            instructions = current.instructions.trim(),
            payment = current.payment,
            deliveryPoint = point
        ) { result ->
            val orderId = result.getOrNull()
            if (orderId != null) {
                _state.value = _state.value.copy(isPlacing = false, instructions = "")
                onPlaced(orderId)
            } else {
                val offline = result.exceptionOrNull() is FirebaseNetworkException
                _state.value = _state.value.copy(
                    isPlacing = false,
                    messageRes = if (offline) R.string.auth_error_network else R.string.order_error
                )
            }
        }
    }

    override fun onCleared() {
        registration?.remove()
        registration = null
    }
}
