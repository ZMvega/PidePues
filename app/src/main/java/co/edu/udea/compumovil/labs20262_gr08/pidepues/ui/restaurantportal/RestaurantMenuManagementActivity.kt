package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurantportal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRestaurantMenuManagementBinding

class RestaurantMenuManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantMenuManagementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantMenuManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupCategoryChips()
        setupSwitchListeners()
        updateAvailableCount()
        setupBottomNavigation()
    }

    private fun setupListeners() {
        binding.btnAddProduct.setOnClickListener {
            startActivity(Intent(this, RestaurantAddProductActivity::class.java))
        }
    }

    private fun setupCategoryChips() {
        val chips = listOf(
            binding.chipAll,
            binding.chipBurgers,
            binding.chipSides,
            binding.chipDrinks,
            binding.chipDesserts
        )

        chips.forEach { chip ->
            chip.setOnClickListener {
                chips.forEach { c ->
                    c.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                    c.setTextColor(Color.parseColor("#1A1A1A"))
                }
                chip.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#F04E23"))
                chip.setTextColor(Color.parseColor("#FFFFFF"))
                Toast.makeText(this, "Filtrando por: ${chip.text}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSwitchListeners() {
        val switches = listOf(
            binding.smProduct1,
            binding.smProduct2,
            binding.smProduct3,
            binding.smProduct4
        )

        switches.forEach { sm ->
            sm.setOnCheckedChangeListener { _, isChecked ->
                updateAvailableCount()
                val statusText = if (isChecked) "Disponible 🟢" else "Agotado 🔴"
                Toast.makeText(this, "Producto $statusText", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateAvailableCount() {
        val activeCount = (if (binding.smProduct1.isChecked) 1 else 0) +
                (if (binding.smProduct2.isChecked) 1 else 0) +
                (if (binding.smProduct3.isChecked) 1 else 0) +
                (if (binding.smProduct4.isChecked) 1 else 0) + 4
        binding.tvMenuStats.text = "$activeCount de 8 productos disponibles"
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationRestaurant.selectedItemId = R.id.nav_restaurant_menu
        binding.bottomNavigationRestaurant.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_restaurant_panel -> {
                    startActivity(Intent(this, RestaurantDashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_restaurant_orders -> {
                    startActivity(Intent(this, RestaurantOrdersManagementActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_restaurant_menu -> true
                R.id.nav_restaurant_profile -> {
                    startActivity(Intent(this, RestaurantProfileSettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
