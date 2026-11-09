package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurantportal

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRestaurantOrdersManagementBinding

class RestaurantOrdersManagementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantOrdersManagementBinding

    // Workflow state: 0 = Recibido, 1 = Preparando, 2 = En camino, 3 = Entregado
    private var state1049 = 0
    private var state1048 = 0
    private var state1047 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantOrdersManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupStatusFilters()
        setupBottomNavigation()
    }

    private fun setupListeners() {
        binding.cardOrder1049.setOnClickListener {
            openOrderDetail("#1049")
        }
        binding.cardOrder1048.setOnClickListener {
            openOrderDetail("#1048")
        }
        binding.cardOrder1047.setOnClickListener {
            openOrderDetail("#1047")
        }

        binding.btnOrderAction1049.setOnClickListener {
            state1049 = advanceOrderState("#1049", state1049, binding.btnOrderAction1049)
        }
        binding.btnOrderAction1048.setOnClickListener {
            state1048 = advanceOrderState("#1048", state1048, binding.btnOrderAction1048)
        }
        binding.btnOrderAction1047.setOnClickListener {
            state1047 = advanceOrderState("#1047", state1047, binding.btnOrderAction1047)
        }
    }

    private fun advanceOrderState(orderNumber: String, currentState: Int, button: com.google.android.material.button.MaterialButton): Int {
        return when (currentState) {
            0 -> {
                button.text = "Terminado"
                button.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#ED6C02"))
                Toast.makeText(this, "Pedido $orderNumber en preparación 👨‍🍳 (Hora inicio: 21:05)", Toast.LENGTH_SHORT).show()
                1
            }
            1 -> {
                button.text = "Entregado"
                button.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#0288D1"))
                Toast.makeText(this, "Pedido $orderNumber preparado. En camino con el repartidor 🛵", Toast.LENGTH_SHORT).show()
                2
            }
            2 -> {
                button.text = "Finalizado ✓"
                button.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#2E7D32"))
                Toast.makeText(this, "¡Pedido $orderNumber entregado al cliente con éxito! 🎉", Toast.LENGTH_SHORT).show()
                3
            }
            else -> {
                Toast.makeText(this, "Pedido $orderNumber ya está finalizado", Toast.LENGTH_SHORT).show()
                3
            }
        }
    }

    private fun setupStatusFilters() {
        binding.cardFilterReceived.setOnClickListener {
            highlightFilterCard("recibido")
            binding.cardOrder1049.visibility = View.VISIBLE
            binding.cardOrder1048.visibility = View.VISIBLE
            binding.cardOrder1047.visibility = View.VISIBLE
            Toast.makeText(this, "Mostrando pedidos recibidos (3)", Toast.LENGTH_SHORT).show()
        }

        binding.cardFilterPreparing.setOnClickListener {
            highlightFilterCard("preparando")
            binding.cardOrder1049.visibility = View.VISIBLE
            binding.cardOrder1048.visibility = View.GONE
            binding.cardOrder1047.visibility = View.GONE
            Toast.makeText(this, "Mostrando pedidos en preparación (2)", Toast.LENGTH_SHORT).show()
        }

        binding.cardFilterOnTheWay.setOnClickListener {
            highlightFilterCard("encamino")
            binding.cardOrder1049.visibility = View.GONE
            binding.cardOrder1048.visibility = View.VISIBLE
            binding.cardOrder1047.visibility = View.GONE
            Toast.makeText(this, "Mostrando pedidos en camino (1)", Toast.LENGTH_SHORT).show()
        }

        binding.cardFilterDelivered.setOnClickListener {
            highlightFilterCard("entregado")
            binding.cardOrder1049.visibility = View.VISIBLE
            binding.cardOrder1048.visibility = View.VISIBLE
            binding.cardOrder1047.visibility = View.VISIBLE
            Toast.makeText(this, "Mostrando pedidos entregados (4)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun highlightFilterCard(selected: String) {
        binding.cardFilterReceived.setCardBackgroundColor(if (selected == "recibido") Color.parseColor("#D32F2F") else Color.parseColor("#FFEBEE"))
        binding.cardFilterPreparing.setCardBackgroundColor(if (selected == "preparando") Color.parseColor("#ED6C02") else Color.parseColor("#FFF3E0"))
        binding.cardFilterOnTheWay.setCardBackgroundColor(if (selected == "encamino") Color.parseColor("#0288D1") else Color.parseColor("#E1F5FE"))
        binding.cardFilterDelivered.setCardBackgroundColor(if (selected == "entregado") Color.parseColor("#2E7D32") else Color.parseColor("#E8F5E9"))
    }

    private fun openOrderDetail(orderNumber: String) {
        val intent = Intent(this, RestaurantOrderDetailActivity::class.java).apply {
            putExtra("ORDER_NUMBER", orderNumber)
        }
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigationRestaurant.selectedItemId = R.id.nav_restaurant_orders
        binding.bottomNavigationRestaurant.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_restaurant_panel -> {
                    startActivity(Intent(this, RestaurantDashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_restaurant_orders -> true
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
