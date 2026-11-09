package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurantportal

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRestaurantSalesHistoryBinding

class RestaurantSalesHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantSalesHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantSalesHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupTimeFilterChips()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupTimeFilterChips() {
        val chips = listOf(
            binding.chipHistoryToday,
            binding.chipHistoryYesterday,
            binding.chipHistory7Days,
            binding.chipHistory30Days
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
                    R.id.chipHistoryToday -> {
                        binding.tvPeriodSales.text = "$ 228.000"
                        binding.tvPeriodOrders.text = "4"
                        binding.tvPeriodAverage.text = "$ 57.000"
                    }
                    R.id.chipHistoryYesterday -> {
                        binding.tvPeriodSales.text = "$ 233.500"
                        binding.tvPeriodOrders.text = "3"
                        binding.tvPeriodAverage.text = "$ 77.833"
                    }
                    R.id.chipHistory7Days -> {
                        binding.tvPeriodSales.text = "$ 642.000"
                        binding.tvPeriodOrders.text = "10"
                        binding.tvPeriodAverage.text = "$ 64.200"
                    }
                    R.id.chipHistory30Days -> {
                        binding.tvPeriodSales.text = "$ 2.450.000"
                        binding.tvPeriodOrders.text = "38"
                        binding.tvPeriodAverage.text = "$ 64.473"
                    }
                }
                Toast.makeText(this, "Filtro temporal: ${chip.text}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
