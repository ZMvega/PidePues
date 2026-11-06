package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.RestaurantRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.domain.RestaurantRanking
import co.edu.udea.compumovil.labs20262_gr08.pidepues.location.LocationProvider
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Category
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantFilters
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantWithDistance
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.SortOption
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserLocation

/**
 * Estado compartido entre Inicio, Buscar y Filtros (ViewModel de la Activity).
 * Cada cambio recalcula las listas con [RestaurantRanking].
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
        val recentSearches: List<String>
    ) {
        /** Buscar muestra resultados si hay texto o algún filtro distinto del predeterminado. */
        val isSearching: Boolean get() = query.isNotBlank() || !filters.isDefault
    }

    private val restaurants = RestaurantRepository.getRestaurants()

    private val _state = MutableLiveData(
        buildState(LocationProvider.DEMO_CENTER, "", RestaurantFilters(), DEFAULT_RECENTS)
    )
    val state: LiveData<UiState> get() = _state

    private val current: UiState get() = _state.value!!

    private fun buildState(
        location: UserLocation,
        query: String,
        filters: RestaurantFilters,
        recents: List<String>
    ): UiState {
        val all = RestaurantRanking.withDistance(restaurants, location)
        val nearby = RestaurantRanking.filterAndSort(all, filters)
        return UiState(location, query, filters, nearby, RestaurantRanking.search(nearby, query), recents)
    }

    private fun update(
        location: UserLocation = current.location,
        query: String = current.query,
        filters: RestaurantFilters = current.filters,
        recents: List<String> = current.recentSearches
    ) {
        _state.value = buildState(location, query, filters, recents)
    }

    //ubicacion
    fun loadDeviceLocation(context: Context) {
        LocationProvider.getCurrentLocation(context.applicationContext) { location ->
            if (location != null) update(location = location)
        }
    }

    //busqueda
    fun setQuery(query: String) {
        if (query == current.query) return
        update(query = query)
    }

    /** Se llama al presionar "buscar" en el teclado: guarda la búsqueda en recientes. */
    fun submitSearch(query: String) {
        val text = query.trim()
        if (text.isEmpty()) return
        update(query = text, recents = pushRecent(text))
    }

    fun selectRecent(query: String) {
        update(query = query, recents = pushRecent(query))
    }

    fun clearRecents() = update(recents = emptyList())

    private fun pushRecent(text: String): List<String> =
        (listOf(text) + current.recentSearches.filterNot { it.equals(text, ignoreCase = true) })
            .take(MAX_RECENTS)

    //filtros
    fun setSort(sort: SortOption) {
        if (sort == current.filters.sort) return
        update(filters = current.filters.copy(sort = sort))
    }

    fun setFilters(filters: RestaurantFilters) {
        if (filters == current.filters) return
        update(filters = filters)
    }

    fun applyCategory(category: Category) =
        update(filters = current.filters.copy(categories = setOf(category)))

    /**boton "Aplicar" del banner de enviso gratis: solo restaurantes con 4.5+. */
    fun applyTopRatedPromo() = update(filters = current.filters.copy(minRating = 4.5f))

    fun clearFilters() = update(filters = RestaurantFilters())

    fun clearAll() = update(query = "", filters = RestaurantFilters())

    companion object {
        private const val MAX_RECENTS = 8
        private val DEFAULT_RECENTS = listOf(
            "Bandeja Paisa", "Sushi", "Hamburguesas", "Pizza italiana", "Comida saludable"
        )
    }
}
