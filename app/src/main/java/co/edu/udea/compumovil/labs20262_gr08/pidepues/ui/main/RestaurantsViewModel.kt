package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.FirestoreSeeder
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.RestaurantRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.domain.RestaurantRanking
import co.edu.udea.compumovil.labs20262_gr08.pidepues.location.LocationProvider
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Category
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Restaurant
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantFilters
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantWithDistance
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.SortOption
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserLocation
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.haversineKm
import co.edu.udea.compumovil.labs20262_gr08.pidepues.domain.RecommendationEngine
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado compartido entre Inicio, Buscar, Filtros y el detalle del restaurante.
 * Los restaurantes vienen de Firestore; cada cambio recalcula las listas con [RestaurantRanking].
 */
class RestaurantsViewModel : ViewModel() {

    data class UiState(
        val location: UserLocation,
        val query: String,
        val filters: RestaurantFilters,
        /** Restaurantes que cumplen los filtros (pantalla Inicio). */
        val nearby: List<RestaurantWithDistance>,
        /** Los anteriores, además del texto buscado (pantalla Buscar). */
        val searchResults: List<RestaurantWithDistance>,
        val recentSearches: List<String>,
        val isLoading: Boolean,
        val loadError: Boolean
    ) {
        /** Buscar muestra resultados si hay texto o algún filtro distinto del predeterminado. */
        val isSearching: Boolean get() = query.isNotBlank() || !filters.isDefault
    }

    private var restaurants: List<Restaurant> = emptyList()
    private var orderHistory: List<Order> = emptyList()
    private var location: UserLocation = LocationProvider.DEMO_CENTER
    private var query = ""
    private var filters = RestaurantFilters()
    private var recents: List<String> = DEFAULT_RECENTS
    private var isLoading = false
    private var loadError = false
    private var seedIfEmpty = false

    private val _state = MutableStateFlow(buildState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private fun buildState(): UiState {
        val all = RestaurantRanking.withDistance(restaurants, location)
        val preferences = RecommendationEngine.buildPreferences(orderHistory, restaurants, recents)
        val nearby = RestaurantRanking.filterAndSort(all, filters, preferences)
        return UiState(
            location, query, filters, nearby, RestaurantRanking.search(nearby, query),
            recents, isLoading, loadError
        )
    }

    private fun render() {
        _state.value = buildState()
    }

    // ---------- Datos (Firestore) ----------
    /**
     * Descarga los restaurantes. Con [seed] = true y la colección vacía, carga primero los
     * datos de ejemplo (solo en depuración).
     */
    fun loadRestaurants(seed: Boolean = seedIfEmpty) {
        seedIfEmpty = seed
        isLoading = true
        loadError = false
        render()
        RestaurantRepository.fetchRestaurants { result ->
            val list = result.getOrNull()
            if (list != null && list.isEmpty() && seedIfEmpty) {
                FirestoreSeeder.seedIfEmpty { seeded ->
                    if (seeded.isSuccess) {
                        RestaurantRepository.fetchRestaurants { applyFetch(it) }
                    } else {
                        applyFetch(Result.failure(seeded.exceptionOrNull() ?: IllegalStateException()))
                    }
                }
            } else {
                applyFetch(result)
            }
        }
    }

    private fun applyFetch(result: Result<List<Restaurant>>) {
        isLoading = false
        val list = result.getOrNull()
        if (list != null) {
            restaurants = list
            loadError = false
        } else {
            loadError = true
        }
        render()
    }

    /** Busca un restaurante por id (sin aplicar filtros) con su distancia al usuario. */
    fun findById(id: Int): RestaurantWithDistance? =
        restaurants.firstOrNull { it.id == id }?.let {
            RestaurantWithDistance(it, haversineKm(location.latitude, location.longitude, it.latitude, it.longitude))
        }

    // ---------- Ubicación ----------
    fun loadDeviceLocation(context: Context) {
        LocationProvider.getCurrentLocation(context.applicationContext) { device ->
            if (device != null) {
                location = device
                render()
            }
        }
    }

    // ---------- Búsqueda ----------
    fun setQuery(newQuery: String) {
        if (newQuery == query) return
        query = newQuery
        render()
    }

    /** Se llama al presionar "buscar" en el teclado: guarda la búsqueda en recientes. */
    fun submitSearch(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        query = clean
        recents = pushRecent(clean)
        render()
    }

    fun selectRecent(text: String) {
        query = text
        recents = pushRecent(text)
        render()
    }

    fun clearRecents() {
        recents = emptyList()
        render()
    }

    private fun pushRecent(text: String): List<String> =
        (listOf(text) + recents.filterNot { it.equals(text, ignoreCase = true) }).take(MAX_RECENTS)

    // ---------- Filtros ----------
    fun setSort(sort: SortOption) {
        if (sort == filters.sort) return
        filters = filters.copy(sort = sort)
        render()
    }

    fun setFilters(newFilters: RestaurantFilters) {
        if (newFilters == filters) return
        filters = newFilters
        render()
    }

    fun applyCategory(category: Category) {
        filters = filters.copy(categories = setOf(category))
        render()
    }

    /** Botón "Aplicar" del banner de envío gratis: solo restaurantes con 4.5+. */
    fun applyTopRatedPromo() {
        filters = filters.copy(minRating = 4.5f)
        render()
    }

    fun clearFilters() {
        filters = RestaurantFilters()
        render()
    }

    fun clearAll() {
        query = ""
        filters = RestaurantFilters()
        render()
    }

    /** Actualiza el historial de pedidos para personalizar las recomendaciones. */
    fun updateOrderHistory(orders: List<Order>) {
        if (orders == orderHistory) return
        orderHistory = orders
        render()
    }

    companion object {
        private const val MAX_RECENTS = 8
        private val DEFAULT_RECENTS = listOf(
            "Bandeja Paisa", "Sushi", "Hamburguesas", "Pizza italiana", "Comida saludable"
        )
    }
}
