package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.orders

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRateOrderBinding

class RateOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRateOrderBinding
    private var selectedRating = 0
    private lateinit var starViews: List<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val orderId = intent.getStringExtra("ORDER_ID") ?: "#PP-2848"
        val restaurantName = intent.getStringExtra("RESTAURANT_NAME") ?: "El Rincón Paisa"
        val orderDate = intent.getStringExtra("ORDER_DATE") ?: "Tu pedido del 15 ago"

        binding.tvOrderCode.text = orderId
        binding.tvRestaurantName.text = restaurantName
        binding.tvOrderDate.text = orderDate

        starViews = listOf(
            binding.ivStar1,
            binding.ivStar2,
            binding.ivStar3,
            binding.ivStar4,
            binding.ivStar5
        )

        setupStarListeners()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSubmitRating.setOnClickListener {
            if (selectedRating == 0) {
                Toast.makeText(this, "Por favor selecciona al menos una estrella", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "¡Gracias por calificar tu pedido! ($selectedRating/5)", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupStarListeners() {
        starViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                selectedRating = index + 1
                updateStarsUI()
            }
        }
    }

    private fun updateStarsUI() {
        starViews.forEachIndexed { index, imageView ->
            if (index < selectedRating) {
                imageView.setImageResource(R.drawable.ic_star)
            } else {
                imageView.setImageResource(R.drawable.ic_star_outline)
            }
        }
    }
}
