package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurant

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityProductDetailBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.cart.CartActivity
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private var basePrice: Int = 32000
    private var quantity: Int = 1
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDataFromIntent()
        setupListeners()
        updatePriceAndQuantityUI()
    }

    private fun setupDataFromIntent() {
        val name = intent.getStringExtra("PRODUCT_NAME") ?: "Bandeja Paisa Completa"
        val description = intent.getStringExtra("PRODUCT_DESCRIPTION")
            ?: "Frijoles, arroz blanco, chicharrón, carne molida, chorizo paisa, huevo frito, aguacate, arepa y tajadas"
        val category = intent.getStringExtra("PRODUCT_CATEGORY") ?: "Platos Fuertes"
        basePrice = intent.getIntExtra("PRODUCT_PRICE", 32000)
        val isPopular = intent.getBooleanExtra("IS_POPULAR", true)

        binding.tvProductName.text = name
        binding.tvProductDescription.text = description
        binding.tvProductCategory.text = category
        binding.tvTagPopular.visibility = if (isPopular) View.VISIBLE else View.GONE
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

        binding.btnIncrease.setOnClickListener {
            quantity++
            updatePriceAndQuantityUI()
        }

        binding.btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updatePriceAndQuantityUI()
            }
        }

        binding.cardOptCebolla.setOnClickListener { binding.cbSinCebolla.toggle() }
        binding.cardOptQueso.setOnClickListener { binding.cbExtraQueso.toggle() }
        binding.cardOptPicante.setOnClickListener { binding.cbSinPicante.toggle() }
        binding.cardOptSalsa.setOnClickListener { binding.cbExtraSalsa.toggle() }
        binding.cardOptPepinillos.setOnClickListener { binding.cbSinPepinillos.toggle() }

        binding.btnAddToCart.setOnClickListener {
            val selectedOptions = mutableListOf<String>()
            if (binding.cbSinCebolla.isChecked) selectedOptions.add("Sin cebolla")
            if (binding.cbExtraQueso.isChecked) selectedOptions.add("Extra queso")
            if (binding.cbSinPicante.isChecked) selectedOptions.add("Sin picante")
            if (binding.cbExtraSalsa.isChecked) selectedOptions.add("Extra salsa")
            if (binding.cbSinPepinillos.isChecked) selectedOptions.add("Sin pepinillos")

            val optionsString = if (selectedOptions.isNotEmpty()) {
                selectedOptions.joinToString(", ")
            } else {
                "Sin observaciones"
            }

            val intent = Intent(this, CartActivity::class.java).apply {
                putExtra("HAS_ITEMS", true)
                putExtra("PRODUCT_NAME", binding.tvProductName.text.toString())
                putExtra("PRODUCT_PRICE", basePrice)
                putExtra("PRODUCT_QUANTITY", quantity)
                putExtra("PRODUCT_OPTIONS", optionsString)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun updatePriceAndQuantityUI() {
        binding.tvQuantity.text = quantity.toString()
        val total = basePrice * quantity
        val formattedPrice = formatPrice(total)

        binding.tvProductPrice.text = formatPrice(basePrice)
        binding.btnAddToCart.text = "Agregar al carrito          $formattedPrice"
    }

    private fun formatPrice(price: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("es", "CO"))
        return "$${formatter.format(price)}"
    }
}