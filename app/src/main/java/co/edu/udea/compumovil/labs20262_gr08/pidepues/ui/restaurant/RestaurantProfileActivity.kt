package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurant

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRestaurantProfileBinding

class RestaurantProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantProfileBinding
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDataFromIntent()
        setupListeners()
    }

    private fun setupDataFromIntent() {
        val name = intent.getStringExtra("RESTAURANT_NAME") ?: "El Rincón Paisa"
        val cuisine = intent.getStringExtra("CUISINE_TYPE") ?: "Comida Colombiana"

        binding.tvRestaurantTitle.text = name
        binding.tvCuisineSubTitle.text = cuisine

        val minOrderHtml = "Pedido mínimo: <font color='#FF5722'><b>$15.000</b></font>"
        binding.tvMinOrder.text = Html.fromHtml(minOrderHtml, Html.FROM_HTML_MODE_LEGACY)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            if (isFavorite) {
                binding.ivFavoriteIcon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.primary)
                )
            } else {
                binding.ivFavoriteIcon.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.surface)
                )
            }
        }

        binding.btnViewMenu.setOnClickListener {
            val intent = Intent(this, RestaurantMenuActivity::class.java).apply {
                putExtra("RESTAURANT_NAME", binding.tvRestaurantTitle.text.toString())
            }
            startActivity(intent)
        }
    }
}