package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurantportal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRestaurantProfileSettingsBinding

class RestaurantProfileSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantProfileSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupBottomNavigation()
    }

    private fun setupListeners() {
        binding.tvViewReviews.setOnClickListener {
            startActivity(Intent(this, RestaurantReviewsActivity::class.java))
        }

        binding.btnNavReviews.setOnClickListener {
            startActivity(Intent(this, RestaurantReviewsActivity::class.java))
        }

        binding.btnNavSalesHistory.setOnClickListener {
            startActivity(Intent(this, RestaurantSalesHistoryActivity::class.java))
        }

        binding.btnAccountAhorros.setOnClickListener {
            binding.btnAccountAhorros.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#F04E23"))
            binding.btnAccountAhorros.setTextColor(Color.parseColor("#FFFFFF"))
            binding.btnAccountCorriente.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
            binding.btnAccountCorriente.setTextColor(Color.parseColor("#1A1A1A"))
            Toast.makeText(this, "Tipo de cuenta seleccionado: Ahorros", Toast.LENGTH_SHORT).show()
        }

        binding.btnAccountCorriente.setOnClickListener {
            binding.btnAccountCorriente.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#F04E23"))
            binding.btnAccountCorriente.setTextColor(Color.parseColor("#FFFFFF"))
            binding.btnAccountAhorros.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
            binding.btnAccountAhorros.setTextColor(Color.parseColor("#1A1A1A"))
            Toast.makeText(this, "Tipo de cuenta seleccionado: Corriente", Toast.LENGTH_SHORT).show()
        }

        binding.btnSaveProfileChanges.setOnClickListener {
            Toast.makeText(this, "¡Cambios en el perfil guardados con éxito!", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogoutRestaurant.setOnClickListener {
            val intent = Intent(this, RestaurantLoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationRestaurant.selectedItemId = R.id.nav_restaurant_profile
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
                R.id.nav_restaurant_menu -> {
                    startActivity(Intent(this, RestaurantMenuManagementActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_restaurant_profile -> true
                else -> false
            }
        }
    }
}
