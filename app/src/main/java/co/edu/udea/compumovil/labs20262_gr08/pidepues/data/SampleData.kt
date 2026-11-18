package co.edu.udea.compumovil.labs20262_gr08.pidepues.data

import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Category
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuItem
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Restaurant

/**
 * Datos de EJEMPLO alrededor de El Poblado (Medellín).
 * Se usan para cargar (sembrar) Firestore en desarrollo y para las pruebas unitarias.
 * La app lee siempre desde Firestore a través de [RestaurantRepository].
 */
object SampleData {

    fun restaurants(): List<Restaurant> = listOf(
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

    private data class Dish(val name: String, val description: String, val price: Int, val emoji: String)

    private val menus: Map<Category, List<Dish>> = mapOf(
        Category.PIZZA to listOf(
            Dish("Pizza Margarita", "Tomate, mozzarella y albahaca fresca", 28000, "🍕"),
            Dish("Pizza Pepperoni", "Mozzarella y pepperoni artesanal", 32000, "🍕"),
            Dish("Pizza Cuatro Quesos", "Mozzarella, parmesano, gorgonzola y provolone", 36000, "🧀"),
            Dish("Pan de ajo", "Con mantequilla de ajo y orégano", 12000, "🥖"),
            Dish("Limonada de coco", "Cremosa, 400 ml", 9000, "🥥")
        ),
        Category.HAMBURGUESAS to listOf(
            Dish("Hamburguesa clásica", "Carne 150 g, lechuga, tomate y queso", 22000, "🍔"),
            Dish("Doble queso", "Doble carne, doble queso cheddar", 29000, "🍔"),
            Dish("Hamburguesa BBQ", "Carne, tocineta y salsa BBQ ahumada", 31000, "🍔"),
            Dish("Papas rústicas", "Porción grande con salsa de la casa", 10000, "🍟"),
            Dish("Malteada de vainilla", "Cremosa, 350 ml", 13000, "🥤")
        ),
        Category.SUSHI to listOf(
            Dish("California Roll", "Cangrejo, aguacate y pepino (8 piezas)", 30000, "🍣"),
            Dish("Roll de salmón", "Salmón fresco y queso crema (8 piezas)", 36000, "🍣"),
            Dish("Combo 24 piezas", "Selección del chef para compartir", 48000, "🍱"),
            Dish("Gyozas", "Rellenas de cerdo, 6 unidades", 18000, "🥟"),
            Dish("Té helado", "Verde con limón, 350 ml", 8000, "🍵")
        ),
        Category.ENSALADAS to listOf(
            Dish("Ensalada César", "Pollo, parmesano, crutones y aderezo césar", 22000, "🥗"),
            Dish("Bowl vegano", "Quinoa, garbanzos, aguacate y vegetales asados", 26000, "🥙"),
            Dish("Ensalada mediterránea", "Tomate, pepino, aceitunas y queso feta", 20000, "🥗"),
            Dish("Jugo verde", "Piña, apio, espinaca y limón", 9000, "🥬"),
            Dish("Wrap de pollo", "Tortilla integral, pollo y vegetales", 18000, "🌯")
        ),
        Category.MEXICANA to listOf(
            Dish("Tacos al pastor", "3 tacos con piña y cilantro", 24000, "🌮"),
            Dish("Burrito de pollo", "Arroz, frijoles, pollo y guacamole", 26000, "🌯"),
            Dish("Quesadilla", "Tortilla de harina con queso y champiñones", 20000, "🧀"),
            Dish("Nachos con guacamole", "Para compartir", 18000, "🥑"),
            Dish("Limonada con chile", "Refrescante, 400 ml", 9000, "🍋")
        ),
        Category.CAFE to listOf(
            Dish("Café americano", "Café de origen, 8 oz", 7000, "☕"),
            Dish("Cappuccino", "Espresso doble con leche espumada", 10000, "☕"),
            Dish("Croissant de almendras", "Hojaldrado y recién horneado", 9000, "🥐"),
            Dish("Cheesecake de frutos rojos", "Porción individual", 14000, "🍰"),
            Dish("Chocolate caliente", "Con crema batida", 11000, "🍫")
        ),
        Category.ASIATICA to listOf(
            Dish("Ramen de cerdo", "Caldo de miso, huevo y cebollín", 28000, "🍜"),
            Dish("Pad Thai de pollo", "Fideos de arroz, maní y limón", 26000, "🍝"),
            Dish("Arroz frito especial", "Con camarones, pollo y vegetales", 24000, "🍚"),
            Dish("Rollitos primavera", "4 unidades con salsa agridulce", 14000, "🥠"),
            Dish("Té chai frío", "350 ml", 8000, "🥤")
        ),
        Category.COLOMBIANA to listOf(
            Dish("Bandeja paisa", "Frijoles, arroz, chicharrón, carne, huevo y arepa", 38000, "🍲"),
            Dish("Sancocho de gallina", "Con arroz, aguacate y ají", 30000, "🥘"),
            Dish("Ajiaco santafereño", "Con pollo, mazorca y alcaparras", 28000, "🍲"),
            Dish("Empanadas (3)", "Carne y papa con ají", 9000, "🥟"),
            Dish("Jugo de lulo", "Natural, 400 ml", 8000, "🍹")
        )
    )

    /** Menú de ejemplo de un restaurante según su categoría. */
    fun menuFor(restaurant: Restaurant): List<MenuItem> =
        menus.getValue(restaurant.category).mapIndexed { index, dish ->
            MenuItem(
                id = "${restaurant.id}-${index + 1}",
                restaurantId = restaurant.id,
                name = dish.name,
                description = dish.description,
                price = dish.price,
                emoji = dish.emoji
            )
        }
}
