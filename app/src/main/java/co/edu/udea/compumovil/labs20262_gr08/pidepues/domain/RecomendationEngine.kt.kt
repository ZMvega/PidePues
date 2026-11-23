package co.edu.udea.compumovil.labs20262_gr08.pidepues.domain

import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Category
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Order
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Restaurant

/**
 * Perfil de preferencias inferido automáticamente del historial del usuario.
 */
data class UserPreferences(
    /** Peso normalizado (0.0 a 1.0) por categoría según frecuencia de pedidos. */
    val categoryWeights: Map<Category, Double> = emptyMap(),
    /** Peso normalizado (0.0 a 1.0) por restaurante según recurrencia de pedidos. */
    val restaurantWeights: Map<Int, Double> = emptyMap(),
    /** Búsquedas recientes para detectar intención inmediata. */
    val recentSearches: List<String> = emptyList()
) {
    val hasHistory: Boolean
        get() = categoryWeights.isNotEmpty() || restaurantWeights.isNotEmpty() || recentSearches.isNotEmpty()
}

/**
 * Motor de recomendación personalizado (Semana 10).
 * Combina calificación, cercanía y afinidad histórica del usuario.
 */
object RecommendationEngine {

    private const val MAX_REFERENCE_KM = 10.0

    // Pesos sin historial (Cold Start)
    private const val COLD_RATING = 0.60
    private const val COLD_PROXIMITY = 0.40

    // Pesos con historial/preferencias
    private const val PERS_RATING = 0.45
    private const val PERS_PROXIMITY = 0.30
    private const val PERS_AFFINITY = 0.25

    /**
     * Construye el perfil de preferencias a partir de los pedidos del cliente y sus búsquedas.
     */
    fun buildPreferences(
        orders: List<Order>,
        restaurants: List<Restaurant>,
        recentSearches: List<String>
    ): UserPreferences {
        if (orders.isEmpty() && recentSearches.isEmpty()) return UserPreferences()

        val restaurantById = restaurants.associateBy { it.id }
        val categoryCounts = mutableMapOf<Category, Int>()
        val restaurantCounts = mutableMapOf<Int, Int>()

        orders.forEach { order ->
            restaurantCounts[order.restaurantId] = (restaurantCounts[order.restaurantId] ?: 0) + 1
            val category = restaurantById[order.restaurantId]?.category
            if (category != null) {
                categoryCounts[category] = (categoryCounts[category] ?: 0) + 1
            }
        }

        val maxCat = (categoryCounts.values.maxOrNull() ?: 1).toDouble()
        val maxRest = (restaurantCounts.values.maxOrNull() ?: 1).toDouble()

        return UserPreferences(
            categoryWeights = categoryCounts.mapValues { it.value / maxCat },
            restaurantWeights = restaurantCounts.mapValues { it.value / maxRest },
            recentSearches = recentSearches
        )
    }

    /**
     * Puntaje final de recomendación (0.0 a 1.0).
     */
    fun score(
        restaurant: Restaurant,
        distanceKm: Double,
        preferences: UserPreferences
    ): Double {
        val ratingPart = (restaurant.rating / 5f).toDouble().coerceIn(0.0, 1.0)
        val proximityPart = 1.0 - (distanceKm / MAX_REFERENCE_KM).coerceIn(0.0, 1.0)

        if (!preferences.hasHistory) {
            return COLD_RATING * ratingPart + COLD_PROXIMITY * proximityPart
        }

        val affinityPart = affinityScore(restaurant, preferences)
        return (PERS_RATING * ratingPart) +
                (PERS_PROXIMITY * proximityPart) +
                (PERS_AFFINITY * affinityPart)
    }

    /**
     * Afinidad del restaurante con el usuario (0.0 a 1.0):
     * - 50% afinidad por categoría pedida frecuentemente
     * - 30% recurrencia en el mismo restaurante
     * - 20% coincidencia con búsquedas recientes
     */
    private fun affinityScore(restaurant: Restaurant, preferences: UserPreferences): Double {
        val catScore = preferences.categoryWeights[restaurant.category] ?: 0.0
        val restScore = preferences.restaurantWeights[restaurant.id] ?: 0.0
        val searchScore = if (preferences.recentSearches.any { query ->
                RestaurantRanking.matchesQuery(restaurant, query)
            }
        ) 1.0 else 0.0

        return (0.50 * catScore + 0.30 * restScore + 0.20 * searchScore).coerceIn(0.0, 1.0)
    }
}