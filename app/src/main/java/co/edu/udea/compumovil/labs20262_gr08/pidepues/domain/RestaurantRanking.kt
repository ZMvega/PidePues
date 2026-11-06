package co.edu.udea.compumovil.labs20262_gr08.pidepues.domain

import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Restaurant
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantFilters
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantWithDistance
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.SortOption
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserLocation
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.haversineKm
import java.text.Normalizer

/**
 * logica de cercanía + calificación
 * Kotlin puro para  probarla con pruebas unitarias
 */
object RestaurantRanking {

    /**distancia a partir de la cual la cercania ya no suma puntos al ranking */
    private const val MAX_REFERENCE_KM = 10.0
    private const val WEIGHT_RATING = 0.6
    private const val WEIGHT_PROXIMITY = 0.4

    /**
     * Puntaje de Recomendados: 60 % calificación + 40 % cercanía (0 a 1).
     * Es la linea base que en la semana 10 la IA reemplaza/enriquece con preferencias e historial.
     */
    fun recommendationScore(rating: Float, distanceKm: Double): Double {
        val ratingPart = (rating / 5f).toDouble().coerceIn(0.0, 1.0)
        val proximityPart = 1.0 - (distanceKm / MAX_REFERENCE_KM).coerceIn(0.0, 1.0)
        return WEIGHT_RATING * ratingPart + WEIGHT_PROXIMITY * proximityPart
    }

    /**calcula la distancia de cada restaurante al usuario */
    fun withDistance(restaurants: List<Restaurant>, location: UserLocation): List<RestaurantWithDistance> =
        restaurants.map {
            RestaurantWithDistance(
                it,
                haversineKm(location.latitude, location.longitude, it.latitude, it.longitude)
            )
        }

    /**aplica los filtros (distancia, calificación, tipo de cocina) y el orden elegido */
    fun filterAndSort(
        items: List<RestaurantWithDistance>,
        filters: RestaurantFilters
    ): List<RestaurantWithDistance> {
        val filtered = items.filter {
            it.distanceKm <= filters.maxDistanceKm &&
                it.restaurant.rating >= filters.minRating &&
                (filters.categories.isEmpty() || it.restaurant.category in filters.categories)
        }
        val byOption: Comparator<RestaurantWithDistance> = when (filters.sort) {
            SortOption.RECOMMENDED -> compareByDescending {
                recommendationScore(it.restaurant.rating, it.distanceKm)
            }
            SortOption.NEAREST -> compareBy { it.distanceKm }
            SortOption.BEST_RATED -> compareByDescending<RestaurantWithDistance> { it.restaurant.rating }
                .thenBy { it.distanceKm }
            SortOption.FASTEST -> compareBy<RestaurantWithDistance> { it.restaurant.deliveryMinutes }
                .thenBy { it.distanceKm }
        }
        // Los restaurantes cerrados siempre van al final de la lista.
        return filtered.sortedWith(compareByDescending<RestaurantWithDistance> { it.restaurant.isOpen }.then(byOption))
    }

    /**busqueda por texto en nombre, cocina, etiqueta, categoría y palabras clave */
    fun matchesQuery(restaurant: Restaurant, query: String): Boolean {
        val tokens = query.normalized().split(" ").filter { it.isNotBlank() }
        if (tokens.isEmpty()) return true
        val haystack = listOf(
            restaurant.name, restaurant.cuisine, restaurant.dishTag,
            restaurant.category.label, restaurant.keywords
        ).joinToString(" ").normalized()
        return tokens.all { haystack.contains(it) }
    }

    fun search(items: List<RestaurantWithDistance>, query: String): List<RestaurantWithDistance> =
        items.filter { matchesQuery(it.restaurant, query) }

    private fun String.normalized(): String =
        Normalizer.normalize(this.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{M}+"), "")
}
