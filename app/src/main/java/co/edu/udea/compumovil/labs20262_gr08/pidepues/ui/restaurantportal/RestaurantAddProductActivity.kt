package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurantportal

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRestaurantAddProductBinding

class RestaurantAddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantAddProductBinding
    private var isPhotoSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        setupCategoryChips()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.cardUploadPhoto.setOnClickListener {
            isPhotoSelected = true
            binding.tvPhotoStatus.text = "✓ Foto cargada correctamente"
            Toast.makeText(this, "Foto seleccionada con éxito", Toast.LENGTH_SHORT).show()
        }

        binding.btnSubmitProduct.setOnClickListener {
            val name = binding.etProductName.text.toString().trim()
            val price = binding.etProductPrice.text.toString().trim()

            if (name.isEmpty()) {
                binding.etProductName.error = "Ingresa el nombre del producto"
                binding.etProductName.requestFocus()
                return@setOnClickListener
            }

            if (price.isEmpty()) {
                binding.etProductPrice.error = "Ingresa el precio"
                binding.etProductPrice.requestFocus()
                return@setOnClickListener
            }

            Toast.makeText(this, "¡Producto '$name' creado con éxito!", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun setupCategoryChips() {
        val chips = listOf(
            binding.chipAddBurgers,
            binding.chipAddSides,
            binding.chipAddDrinks,
            binding.chipAddDesserts
        )

        chips.forEach { chip ->
            chip.setOnClickListener {
                chips.forEach { c ->
                    c.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                    c.setTextColor(Color.parseColor("#1A1A1A"))
                }
                chip.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#F04E23"))
                chip.setTextColor(Color.parseColor("#FFFFFF"))
                Toast.makeText(this, "Categoría seleccionada: ${chip.text}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
