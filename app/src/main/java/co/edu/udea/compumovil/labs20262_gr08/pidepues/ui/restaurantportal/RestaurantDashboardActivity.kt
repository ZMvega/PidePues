package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurantportal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRestaurantDashboardBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.profile.ProfileActivity

class RestaurantDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantDashboardBinding
    private var isOpen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupBottomNavigation()
    }

    private fun setupListeners() {
        binding.cardStoreStatus.setOnClickListener {
            isOpen = !isOpen
            if (isOpen) {
                binding.cardStoreStatus.setCardBackgroundColor(Color.parseColor("#E6F9E6"))
                binding.viewStatusDot.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#00C853"))
                binding.tvStoreStatus.text = "Abierto"
                binding.tvStoreStatus.setTextColor(Color.parseColor("#00C853"))
                Toast.makeText(this, "Tu restaurante está ahora Abierto 🟢", Toast.LENGTH_SHORT).show()
            } else {
                binding.cardStoreStatus.setCardBackgroundColor(Color.parseColor("#F3F4F6"))
                binding.viewStatusDot.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#9CA3AF"))
                binding.tvStoreStatus.text = "Cerrado"
                binding.tvStoreStatus.setTextColor(Color.parseColor("#9CA3AF"))
                Toast.makeText(this, "Tu restaurante está ahora Cerrado 🔴", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cardNewOrdersAlert.setOnClickListener {
            startActivity(Intent(this, RestaurantOrdersManagementActivity::class.java))
        }

        binding.btnQuickMenu.setOnClickListener {
            val intent = Intent(this, RestaurantMenuManagementActivity::class.java)
            startActivity(intent)
        }

        binding.btnQuickHistory.setOnClickListener {
            val intent = Intent(this, RestaurantSalesHistoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnSimulateOrder.setOnClickListener {
            Toast.makeText(this, "🔔 ¡Nuevo pedido simulado de Carlos R.! ($ 44.000)", Toast.LENGTH_LONG).show()
        }

        binding.tvViewBoard.setOnClickListener {
            Toast.makeText(this, "Tablero de pedidos en tiempo real", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationRestaurant.selectedItemId = R.id.nav_restaurant_panel
        binding.bottomNavigationRestaurant.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_restaurant_panel -> true
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
