package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.orders

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityOrdersBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ItemOrderCardBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ItemOrderInProgressBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.cart.CartActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.home.HomeActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.payment.OrderTrackingActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.profile.ProfileActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.search.SearchActivity

data class HistoryOrderData(
    val orderId: String,
    val date: String,
    val restaurantName: String,
    val items: List<String>,
    val totalPrice: String,
    val isCanceled: Boolean = false
)

data class ActiveOrderData(
    val orderId: String,
    val restaurantName: String,
    val etaRange: String,
    val progressPercent: Int
)

class OrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrdersBinding
    private var isHistoryTabSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTabListeners()
        setupBottomNavigation()

        // Seleccionar En curso por defecto
        isHistoryTabSelected = false
        updateTabUI()
        loadOrdersForCurrentTab()
    }

    private fun setupTabListeners() {
        binding.tabInProgress.setOnClickListener {
            if (isHistoryTabSelected) {
                isHistoryTabSelected = false
                updateTabUI()
                loadOrdersForCurrentTab()
            }
        }

        binding.tabHistory.setOnClickListener {
            if (!isHistoryTabSelected) {
                isHistoryTabSelected = true
                updateTabUI()
                loadOrdersForCurrentTab()
            }
        }
    }

    private fun updateTabUI() {
        val activeTextColor = ContextCompat.getColor(this, R.color.ink)
        val mutedTextColor = ContextCompat.getColor(this, R.color.muted)

        if (isHistoryTabSelected) {
            binding.tabHistory.setBackgroundResource(R.drawable.bg_tab_selected)
            binding.tvTabHistory.setTextColor(activeTextColor)

            binding.tabInProgress.setBackgroundResource(R.drawable.bg_tab_unselected)
            binding.tvTabInProgress.setTextColor(mutedTextColor)
        } else {
            binding.tabInProgress.setBackgroundResource(R.drawable.bg_tab_selected)
            binding.tvTabInProgress.setTextColor(activeTextColor)

            binding.tabHistory.setBackgroundResource(R.drawable.bg_tab_unselected)
            binding.tvTabHistory.setTextColor(mutedTextColor)
        }
    }

    private fun loadOrdersForCurrentTab() {
        binding.containerOrdersList.removeAllViews()

        val inflater = LayoutInflater.from(this)

        if (isHistoryTabSelected) {
            binding.tvActiveOrdersFooter.visibility = View.GONE
            val historyOrders = getHistoryOrders()

            historyOrders.forEach { order ->
                val itemBinding = ItemOrderCardBinding.inflate(
                    inflater,
                    binding.containerOrdersList,
                    false
                )

                itemBinding.tvRestaurantName.text = order.restaurantName
                itemBinding.tvOrderCodeAndDate.text = "${order.orderId} · ${order.date}"
                itemBinding.tvOrderTotalPrice.text = order.totalPrice
                itemBinding.tvOrderItemsList.text = order.items.joinToString("\n") { "· $it" }

                if (order.isCanceled) {
                    itemBinding.tvStatusBadge.text = getString(R.string.status_canceled)
                    itemBinding.tvStatusBadge.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#FEECEB"))
                    itemBinding.tvStatusBadge.setTextColor(Color.parseColor("#FF4D4D"))
                    itemBinding.btnRateOrder.visibility = View.GONE
                } else {
                    itemBinding.tvStatusBadge.text = getString(R.string.status_delivered)
                    itemBinding.tvStatusBadge.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#E6F9E6"))
                    itemBinding.tvStatusBadge.setTextColor(Color.parseColor("#00C853"))
                    itemBinding.btnRateOrder.visibility = View.VISIBLE
                }

                itemBinding.btnRateOrder.setOnClickListener {
                    val intent = Intent(this, RateOrderActivity::class.java).apply {
                        putExtra("ORDER_ID", order.orderId)
                        putExtra("RESTAURANT_NAME", order.restaurantName)
                        putExtra("ORDER_DATE", "Tu pedido del ${order.date}")
                    }
                    startActivity(intent)
                }

                itemBinding.btnReorder.setOnClickListener {
                    val intent = Intent(this, CartActivity::class.java).apply {
                        putExtra("HAS_ITEMS", true)
                        putExtra("PRODUCT_NAME", order.items.firstOrNull() ?: "Platillo")
                    }
                    startActivity(intent)
                }

                binding.containerOrdersList.addView(itemBinding.root)
            }
        } else {
            // Pestaña "En curso"
            val activeOrder = getActiveOrder()

            if (activeOrder != null) {
                binding.tvActiveOrdersFooter.visibility = View.VISIBLE

                val activeBinding = ItemOrderInProgressBinding.inflate(
                    inflater,
                    binding.containerOrdersList,
                    false
                )

                activeBinding.tvRestaurantName.text = activeOrder.restaurantName
                activeBinding.tvOrderCode.text = activeOrder.orderId
                activeBinding.progressDelivery.progress = activeOrder.progressPercent

                // Formateo del texto de llegada con tiempo en negrita roja
                val prefix = getString(R.string.eta_prefix)
                val fullText = "$prefix${activeOrder.etaRange}"
                val spannable = SpannableString(fullText)
                val start = prefix.length
                val end = fullText.length

                spannable.setSpan(
                    StyleSpan(android.graphics.Typeface.BOLD),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                activeBinding.tvEtaFormatted.text = spannable

                activeBinding.btnViewLiveTracking.setOnClickListener {
                    val intent = Intent(this, OrderTrackingActivity::class.java).apply {
                        putExtra("ORDER_NUMBER", activeOrder.orderId)
                    }
                    startActivity(intent)
                }

                binding.containerOrdersList.addView(activeBinding.root)
            } else {
                binding.tvActiveOrdersFooter.visibility = View.GONE
            }
        }
    }

    private fun getHistoryOrders(): List<HistoryOrderData> {
        return listOf(
            HistoryOrderData(
                orderId = "#PP-2847",
                date = "15 ago 2026",
                restaurantName = "El Rincón Paisa",
                items = listOf("Bandeja Paisa Completa", "Mazamorra con Bocadillo"),
                totalPrice = "$44.000"
            ),
            HistoryOrderData(
                orderId = "#PP-2831",
                date = "12 ago 2026",
                restaurantName = "Burger Bros",
                items = listOf("Smash Burger Clásica ×2", "Papas con Trufa"),
                totalPrice = "$56.000"
            ),
            HistoryOrderData(
                orderId = "#PP-2819",
                date = "8 ago 2026",
                restaurantName = "Sushi Nikkei",
                items = listOf("Nikkei Roll ×2", "Ceviche de Corvina"),
                totalPrice = "$75.000"
            ),
            HistoryOrderData(
                orderId = "#PP-2803",
                date = "5 ago 2026",
                restaurantName = "Green Garden",
                items = listOf("Buddha Bowl Energético", "Smoothie Verde Detox"),
                totalPrice = "$34.000",
                isCanceled = true
            )
        )
    }

    private fun getActiveOrder(): ActiveOrderData {
        return ActiveOrderData(
            orderId = "#PP-2848",
            restaurantName = "El Rincón Paisa",
            etaRange = "8–12 min",
            progressPercent = 70
        )
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_orders
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                    true
                }
                R.id.nav_orders -> true
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
}