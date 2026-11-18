package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common

import co.edu.udea.compumovil.labs20262_gr08.pidepues.R

private val FOOD_COLORS = intArrayOf(
    R.color.food_1, R.color.food_2, R.color.food_3,
    R.color.food_4, R.color.food_5, R.color.food_6
)

/** Color pastel de la "foto" de un restaurante (hasta que haya imágenes reales). */
fun foodColorRes(restaurantId: Int): Int = FOOD_COLORS[restaurantId % FOOD_COLORS.size]
