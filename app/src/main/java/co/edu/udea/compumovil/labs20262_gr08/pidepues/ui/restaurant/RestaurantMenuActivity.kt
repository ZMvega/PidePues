package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurant

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRestaurantMenuBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ItemDishCardBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.cart.CartActivity

class RestaurantMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantMenuBinding
    private lateinit var categoryTabs: List<TextView>
    private var currentCategory: String = "Platos Fuertes"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader()
        setupCategories()
        loadDishesForCategory("Platos Fuertes")
        setupListeners()
    }

    private fun setupHeader() {
        val restaurantName = intent.getStringExtra("RESTAURANT_NAME") ?: "El Rincón Paisa"
        binding.tvRestaurantTitle.text = restaurantName
    }

    private fun setupCategories() {
        categoryTabs = listOf(
            binding.catPlatosFuertes,
            binding.catSopas,
            binding.catEntradas,
            binding.catPostres
        )

        categoryTabs.forEach { tab ->
            tab.setOnClickListener { selectedTab ->
                selectCategory(selectedTab as TextView)
            }
        }
    }

    private fun selectCategory(selected: TextView) {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val surfaceColor = ContextCompat.getColor(this, R.color.surface)
        val inkColor = ContextCompat.getColor(this, R.color.ink)
        val grayBg = Color.parseColor("#F0F1F5")

        categoryTabs.forEach { tab ->
            if (tab == selected) {
                tab.backgroundTintList = ColorStateList.valueOf(primaryColor)
                tab.setTextColor(surfaceColor)
            } else {
                tab.backgroundTintList = ColorStateList.valueOf(grayBg)
                tab.setTextColor(inkColor)
            }
        }

        currentCategory = selected.text.toString()
        loadDishesForCategory(currentCategory)
    }

    private fun loadDishesForCategory(category: String) {
        when (category) {
            "Platos Fuertes" -> {
                binding.tvDishesCount.text = "5 platillos"
                binding.dish1.root.visibility = View.VISIBLE
                binding.dish2.root.visibility = View.VISIBLE
                binding.dish3.root.visibility = View.VISIBLE
                binding.dish4.root.visibility = View.VISIBLE
                binding.dish5.root.visibility = View.VISIBLE

                configureDish(
                    binding.dish1,
                    name = "Bandeja Paisa Completa",
                    description = "Frijoles, arroz blanco, chicharrón, carne molida, chorizo paisa, huevo frito, aguacate, arepa y tajadas",
                    priceStr = "$32.000",
                    priceInt = 32000,
                    isPopular = true
                )
                configureDish(
                    binding.dish2,
                    name = "Cazuela de Frijoles",
                    description = "Frijoles antioqueños con chicharrón crocante, maicito dulce, ripio de papa y aguacate.",
                    priceStr = "$26.000",
                    priceInt = 26000,
                    isPopular = false
                )
                configureDish(
                    binding.dish3,
                    name = "Carne Asada con Arepa",
                    description = "Corte de res a la parrilla servido con arepa de queso, papas criollas y ensalada fresca.",
                    priceStr = "$30.000",
                    priceInt = 30000,
                    isPopular = false
                )
                configureDish(
                    binding.dish4,
                    name = "Lomo al Trapo",
                    description = "Jugoso lomo de res preparado al carbón acompañado de yuca cocida y hogao casero.",
                    priceStr = "$36.000",
                    priceInt = 36000,
                    isPopular = false
                )
                configureDish(
                    binding.dish5,
                    name = "Pescado Frito Antioqueño",
                    description = "Mojarra frita crocante acompañada de patacón gigante, arroz con coco y ensalada.",
                    priceStr = "$28.000",
                    priceInt = 28000,
                    isPopular = false
                )
            }
            "Sopas" -> {
                binding.tvDishesCount.text = "3 platillos"
                binding.dish1.root.visibility = View.VISIBLE
                binding.dish2.root.visibility = View.VISIBLE
                binding.dish3.root.visibility = View.VISIBLE
                binding.dish4.root.visibility = View.GONE
                binding.dish5.root.visibility = View.GONE

                configureDish(
                    binding.dish1,
                    name = "Sancocho Trifásico",
                    description = "Sopa tradicional con res, cerdo y pollo, plátano verde, yuca, papa y mazorca.",
                    priceStr = "$28.000",
                    priceInt = 28000,
                    isPopular = true
                )
                configureDish(
                    binding.dish2,
                    name = "Mondongo Antioqueño",
                    description = "Sopa típica cocinada a fuego lento con carne de cerdo, chorizo, papa y cilantro.",
                    priceStr = "$24.000",
                    priceInt = 24000,
                    isPopular = false
                )
                configureDish(
                    binding.dish3,
                    name = "Sopa de Guineo",
                    description = "Sopa casera a base de plátano verde, trozos de carne desmechada y guiso tradicional.",
                    priceStr = "$18.000",
                    priceInt = 18000,
                    isPopular = false
                )
            }
            "Entradas" -> {
                binding.tvDishesCount.text = "3 platillos"
                binding.dish1.root.visibility = View.VISIBLE
                binding.dish2.root.visibility = View.VISIBLE
                binding.dish3.root.visibility = View.VISIBLE
                binding.dish4.root.visibility = View.GONE
                binding.dish5.root.visibility = View.GONE

                configureDish(
                    binding.dish1,
                    name = "Arepa de Choclo con Queso",
                    description = "Arepa artesanal de maíz tierno tostada a la parrilla con abundante queso campesino.",
                    priceStr = "$12.000",
                    priceInt = 12000,
                    isPopular = true
                )
                configureDish(
                    binding.dish2,
                    name = "Empanadas Antioqueñas (4 ud)",
                    description = "Empanadas crocantes de maíz compuestas de carne desmechada con papa y ají casero.",
                    priceStr = "$10.000",
                    priceInt = 10000,
                    isPopular = false
                )
                configureDish(
                    binding.dish3,
                    name = "Patacón con Hogao y Queso",
                    description = "Patacón crujiente bañado en hogao caliente de tomate y cebolla con queso derretido.",
                    priceStr = "$14.000",
                    priceInt = 14000,
                    isPopular = false
                )
            }
            "Postres" -> {
                binding.tvDishesCount.text = "2 platillos"
                binding.dish1.root.visibility = View.VISIBLE
                binding.dish2.root.visibility = View.VISIBLE
                binding.dish3.root.visibility = View.GONE
                binding.dish4.root.visibility = View.GONE
                binding.dish5.root.visibility = View.GONE

                configureDish(
                    binding.dish1,
                    name = "Brevas con Arequipe y Queso",
                    description = "Brevas caladas en melado con arequipe artesanal y tajada de queso fresco.",
                    priceStr = "$9.000",
                    priceInt = 9000,
                    isPopular = true
                )
                configureDish(
                    binding.dish2,
                    name = "Arroz con Leche Cremoso",
                    description = "Receta tradicional con leche condensada, canela en polvo y pasas al ron.",
                    priceStr = "$8.000",
                    priceInt = 8000,
                    isPopular = false
                )
            }
        }
    }

    private fun configureDish(
        dishInclude: ItemDishCardBinding,
        name: String,
        description: String,
        priceStr: String,
        priceInt: Int,
        isPopular: Boolean
    ) {
        dishInclude.tvDishName.text = name
        dishInclude.tvDishDescription.text = description
        dishInclude.tvDishPrice.text = priceStr

        if (isPopular) {
            dishInclude.tvTagPopular.visibility = View.VISIBLE
        } else {
            dishInclude.tvTagPopular.visibility = View.GONE
        }

        val openDetailAction = View.OnClickListener {
            val intent = Intent(this, ProductDetailActivity::class.java).apply {
                putExtra("PRODUCT_NAME", name)
                putExtra("PRODUCT_DESCRIPTION", description)
                putExtra("PRODUCT_CATEGORY", currentCategory)
                putExtra("PRODUCT_PRICE", priceInt)
                putExtra("IS_POPULAR", isPopular)
            }
            startActivity(intent)
        }

        dishInclude.root.setOnClickListener(openDetailAction)
        dishInclude.btnAddDish.setOnClickListener(openDetailAction)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java).apply {
                putExtra("HAS_ITEMS", false)
            }
            startActivity(intent)
        }
    }
}