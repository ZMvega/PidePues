package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurantportal

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRestaurantOrderDetailBinding

class RestaurantOrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantOrderDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val orderNumber = intent.getStringExtra("ORDER_NUMBER") ?: "#1048"
        binding.tvOrderTitleDetail.text = "Pedido $orderNumber"

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnCallClient.setOnClickListener {
            Toast.makeText(this, "Llamando a Valentina Ríos... +57 300 236 5624", Toast.LENGTH_SHORT).show()
        }

        binding.btnAcceptAndPrepare.setOnClickListener {
            val selectedDriver = when {
                binding.radioDriver1.isChecked -> "Carlos Mejía (0.8 km)"
                binding.radioDriver2.isChecked -> "Laura Rincón (1.4 km)"
                binding.radioDriver3.isChecked -> "Andrés Pérez (2.1 km)"
                else -> "Carlos Mejía (0.8 km)"
            }
            Toast.makeText(this, "¡Pedido aceptado! Asignado a $selectedDriver 🛵", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
