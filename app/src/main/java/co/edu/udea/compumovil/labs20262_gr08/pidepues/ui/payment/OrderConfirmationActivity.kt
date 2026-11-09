package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.payment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityOrderConfirmationBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.home.HomeActivity

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDataFromIntent()
        setupListeners()
        handleBackNavigation()
    }

    private fun setupDataFromIntent() {
        val orderNumber = intent.getStringExtra("ORDER_NUMBER")
            ?: getString(R.string.default_order_number)
        val restaurantName = intent.getStringExtra("RESTAURANT_NAME")
            ?: getString(R.string.default_restaurant_name)
        val estimatedTime = intent.getStringExtra("ESTIMATED_TIME")
            ?: getString(R.string.default_estimated_time)
        val deliveryAddress = intent.getStringExtra("DELIVERY_ADDRESS")
            ?: getString(R.string.default_delivery_address)

        binding.tvOrderNumber.text = orderNumber
        binding.tvOrderRestaurantName.text = restaurantName
        binding.tvOrderEstimatedTime.text = estimatedTime
        binding.tvOrderDeliveryAddress.text = deliveryAddress
        binding.tvDriverName.text = getString(R.string.default_driver_name)
        binding.tvDriverRating.text = getString(R.string.default_driver_rating)
    }

    private fun setupListeners() {
        binding.btnCallDriver.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+573124458821")
            }
            startActivity(intent)
        }

        // Conexión directa hacia la Pantalla 16: Seguimiento del Pedido
        binding.btnTrackOrder.setOnClickListener {
            val intent = Intent(this, OrderTrackingActivity::class.java).apply {
                putExtra("ORDER_NUMBER", binding.tvOrderNumber.text.toString())
                putExtra("DRIVER_NAME", getString(R.string.default_driver_full_name))
            }
            startActivity(intent)
        }

        binding.btnGoHome.setOnClickListener {
            navigateToHome()
        }
    }

    private fun handleBackNavigation() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToHome()
            }
        })
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
        finish()
    }
}