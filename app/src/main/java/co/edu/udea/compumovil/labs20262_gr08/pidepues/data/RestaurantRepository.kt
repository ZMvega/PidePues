package co.edu.udea.compumovil.labs20262_gr08.pidepues.data

import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Category
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Restaurant

/**
 * Fuente de datos de restaurantes.
 *
 * Por ahora son datos quemados alrededor de El Poblado, Medellín. En proximas semanas de
 * nube/pedidos se reemplaza el cuerpo de [getRestaurants] por la lectura real
 * (Firebase, API, etc.) sin tocar el resto de la app.
 */
object RestaurantRepository {

    fun getRestaurants(): List<Restaurant> = listOf(
        Restaurant(1, "El Rincón Paisa", "Comida Colombiana", "Bandeja Paisa", Category.COLOMBIANA,
            4.8f, 25, 3000, 6.21192, -75.56529, "🍲", keywords = "comida tipica paisa"),
        Restaurant(2, "Pizza Napoletana", "Pizza Italiana", "Napolitana", Category.PIZZA,
            4.5f, 35, 4000, 6.20419, -75.55435, "🍕", keywords = "italiana horno"),
        Restaurant(3, "Green Garden", "Saludable · Vegano", "Bowls", Category.ENSALADAS,
            4.7f, 20, 3000, 6.20119, -75.56988, "🥗", keywords = "comida saludable vegana bowls"),
        Restaurant(4, "Taquería El Güero", "Mexicana", "Tacos al pastor", Category.MEXICANA,
            4.4f, 30, 3500, 6.21869, -75.58434, "🌮", isOpen = false, keywords = "tacos burritos"),
        Restaurant(5, "Burger Lab", "Hamburguesas artesanales", "Doble queso", Category.HAMBURGUESAS,
            4.6f, 25, 3000, 6.2142, -75.5577, "🍔", keywords = "comida rapida"),
        Restaurant(6, "Sakura Roll", "Sushi y rolls", "Rolls", Category.SUSHI,
            4.9f, 40, 5000, 6.18466, -75.55308, "🍣", keywords = "japonesa"),
        Restaurant(7, "Café Altura", "Café de especialidad", "Café y postres", Category.CAFE,
            4.3f, 15, 2500, 6.20665, -75.57305, "☕", keywords = "desayuno postres"),
        Restaurant(8, "Wok Express", "Comida asiática", "Ramen", Category.ASIATICA,
            4.2f, 30, 3500, 6.23246, -75.57576, "🍜", keywords = "china tailandesa noodles"),
        Restaurant(9, "La Fonda Antioqueña", "Comida Colombiana", "Sancocho", Category.COLOMBIANA,
            4.5f, 35, 4000, 6.21567, -75.5279, "🥘", keywords = "comida tipica sopa"),
        Restaurant(10, "Pizza Piazza", "Pizza al horno de leña", "Margarita", Category.PIZZA,
            4.1f, 45, 4500, 6.16195, -75.60664, "🍕", keywords = "italiana"),
        Restaurant(11, "Fresh Bowl Lab", "Ensaladas y jugos", "Ensalada César", Category.ENSALADAS,
            3.9f, 20, 0, 6.22563, -75.56412, "🥗", keywords = "comida saludable jugos"),
        Restaurant(12, "Burger Station", "Hamburguesas", "Clásica", Category.HAMBURGUESAS,
            3.6f, 15, 2000, 6.20437, -75.56631, "🍔", keywords = "comida rapida"),
        Restaurant(13, "Sushi Corner", "Sushi", "Combo 24 piezas", Category.SUSHI,
            4.0f, 50, 5500, 6.17507, -75.50835, "🍱", keywords = "japonesa")
    )
}
