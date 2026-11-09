package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityHomeBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.filter.FilterActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.location.DeliveryAddressActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.orders.OrdersActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.profile.ProfileActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurant.RestaurantMenuActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.search.SearchActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
        setupCategoryChips()
        setupListeners()
    }

    private fun setupCategoryChips() {
        val chips = listOf(
            binding.chipHomeAll,
            binding.chipHomeNearby,
            binding.chipHomeTop,
            binding.chipHomeFast
        )

        chips.forEach { chip ->
            chip.setOnClickListener {
                chips.forEach { c ->
                    c.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                    c.setTextColor(Color.parseColor("#1A1A1A"))
                }
                chip.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#F04E23"))
                chip.setTextColor(Color.parseColor("#FFFFFF"))

                when (chip.id) {
                    R.id.chipHomeAll -> {
                        binding.tvSortLabel.text = "🎯 Ordenados por cercanía + calificación"
                        binding.cardRinconPaisa.visibility = View.VISIBLE
                        binding.cardBurgerBros.visibility = View.VISIBLE
                        binding.cardSushiNikkei.visibility = View.VISIBLE
                    }
                    R.id.chipHomeNearby -> {
                        binding.tvSortLabel.text = "📍 Ordenados por más cercanos (0.4 km)"
                        binding.cardRinconPaisa.visibility = View.VISIBLE
                        binding.cardBurgerBros.visibility = View.VISIBLE
                        binding.cardSushiNikkei.visibility = View.GONE
                    }
                    R.id.chipHomeTop -> {
                        binding.tvSortLabel.text = "⭐ Ordenados por mejor calificación (4.8+)"
                        binding.cardRinconPaisa.visibility = View.VISIBLE
                        binding.cardSushiNikkei.visibility = View.VISIBLE
                        binding.cardBurgerBros.visibility = View.GONE
                    }
                    R.id.chipHomeFast -> {
                        binding.tvSortLabel.text = "⚡ Ordenados por entrega más rápida (< 30 min)"
                        binding.cardRinconPaisa.visibility = View.VISIBLE
                        binding.cardBurgerBros.visibility = View.VISIBLE
                        binding.cardSushiNikkei.visibility = View.GONE
                    }
                }
                Toast.makeText(this, "Filtro aplicado: ${chip.text}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                    true
                }
                R.id.nav_orders -> {
                    startActivity(Intent(this, OrdersActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupListeners() {
        binding.btnChangeLocation.setOnClickListener {
            startActivity(Intent(this, DeliveryAddressActivity::class.java))
        }

        binding.btnNotifications.setOnClickListener {
            Toast.makeText(this, "No tienes notificaciones nuevas", Toast.LENGTH_SHORT).show()
        }

        binding.searchContainer.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.btnFilter.setOnClickListener {
            startActivity(Intent(this, FilterActivity::class.java))
        }

        binding.cardRinconPaisa.setOnClickListener {
            val intent = Intent(this, RestaurantMenuActivity::class.java).apply {
                putExtra("RESTAURANT_NAME", "El Rincón Paisa")
            }
            startActivity(intent)
        }

        binding.cardBurgerBros.setOnClickListener {
            val intent = Intent(this, RestaurantMenuActivity::class.java).apply {
                putExtra("RESTAURANT_NAME", "Burger Bros")
            }
            startActivity(intent)
        }

        binding.cardSushiNikkei.setOnClickListener {
            val intent = Intent(this, RestaurantMenuActivity::class.java).apply {
                putExtra("RESTAURANT_NAME", "Sushi Nikkei")
            }
            startActivity(intent)
        }

        binding.btnSeeAll.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.selectedItemId = R.id.nav_home
    }
}
