package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurantportal

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRestaurantReviewsBinding

class RestaurantReviewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantReviewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupRatingFilterChips()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnReply1.setOnClickListener {
            Toast.makeText(this, "Respondiendo a la reseña de Daniel Vélez...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRatingFilterChips() {
        val chips = listOf(
            binding.chipReviewAll,
            binding.chipReview5,
            binding.chipReview4,
            binding.chipReview3,
            binding.chipReview2,
            binding.chipReview1
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
                    R.id.chipReviewAll, R.id.chipReview5 -> {
                        binding.cardReviewDaniel.visibility = View.VISIBLE
                        binding.cardReviewLaura.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.cardReviewDaniel.visibility = View.GONE
                        binding.cardReviewLaura.visibility = View.GONE
                    }
                }
                Toast.makeText(this, "Filtro de reseñas: ${chip.text}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
