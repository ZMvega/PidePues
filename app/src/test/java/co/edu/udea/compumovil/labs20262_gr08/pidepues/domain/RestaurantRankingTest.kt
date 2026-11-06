package co.edu.udea.compumovil.labs20262_gr08.pidepues.domain

import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.RestaurantRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Category
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantFilters
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.SortOption
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserLocation
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.haversineKm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas de la lógica de cercanía + calificación (semanas 5 y 6). */
class RestaurantRankingTest {

    private val elPoblado = UserLocation(6.2088, -75.5671)
    private val all = RestaurantRanking.withDistance(RestaurantRepository.getRestaurants(), elPoblado)

    private fun names(filters: RestaurantFilters) =
        RestaurantRanking.filterAndSort(all, filters).map { it.restaurant.name }

    @Test
    fun haversine_medellinBogota_isAbout240Km() {
        val km = haversineKm(6.2442, -75.5812, 4.7110, -74.0721)
        assertTrue("distancia = $km", km in 235.0..246.0)
    }

    @Test
    fun withDistance_matchesDesignedDistances() {
        val rincon = all.first { it.restaurant.name == "El Rincón Paisa" }
        assertEquals(0.4, rincon.distanceKm, 0.05)
    }

    @Test
    fun recommended_putsBestRatedNearbyFirst_andClosedLast() {
        val result = names(RestaurantFilters())
        assertEquals("El Rincón Paisa", result.first())
        assertEquals("Taquería El Güero", result.last())   // cerrado
    }

    @Test
    fun defaultFilter_excludesRestaurantsBeyond5Km() {
        val result = RestaurantRanking.filterAndSort(all, RestaurantFilters())
        assertTrue(result.all { it.distanceKm <= 5.0 })
    }

    @Test
    fun nearest_sortsByDistanceAscending() {
        val open = RestaurantRanking.filterAndSort(all, RestaurantFilters(sort = SortOption.NEAREST))
            .filter { it.restaurant.isOpen }
        assertTrue(open.zipWithNext().all { (a, b) -> a.distanceKm <= b.distanceKm })
    }

    @Test
    fun bestRated_returnsHighestRatingFirst() {
        val result = names(RestaurantFilters(sort = SortOption.BEST_RATED, maxDistanceKm = 10f))
        assertEquals("Sakura Roll", result.first())
    }

    @Test
    fun minRating_filtersOutLowerRatings() {
        val result = RestaurantRanking.filterAndSort(all, RestaurantFilters(minRating = 4.5f))
        assertTrue(result.isNotEmpty())
        assertTrue(result.all { it.restaurant.rating >= 4.5f })
    }

    @Test
    fun categories_filterByCuisineType() {
        val result = RestaurantRanking.filterAndSort(
            all, RestaurantFilters(maxDistanceKm = 10f, categories = setOf(Category.PIZZA))
        )
        assertEquals(2, result.size)
        assertTrue(result.all { it.restaurant.category == Category.PIZZA })
    }

    @Test
    fun search_ignoresAccentsAndMatchesSeveralWords() {
        fun found(q: String) = RestaurantRanking.search(all, q).map { it.restaurant.name }
        assertTrue("Café Altura" in found("cafe"))
        assertEquals(listOf("El Rincón Paisa"), found("bandeja paisa"))
        assertEquals(setOf("Green Garden", "Fresh Bowl Lab"), found("comida saludable").toSet())
        assertTrue(found("xyz").isEmpty())
        assertEquals(all.size, found("   ").size)
    }

    @Test
    fun recommendationScore_isBetweenZeroAndOne() {
        assertEquals(1.0, RestaurantRanking.recommendationScore(5f, 0.0), 1e-9)
        assertEquals(0.0, RestaurantRanking.recommendationScore(0f, 50.0), 1e-9)
    }
}
