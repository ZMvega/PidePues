package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.payment

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityOrderTrackingBinding

class OrderTrackingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderTrackingBinding
    private var isDeliveredState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDataFromIntent()
        setupListeners()
    }

    private fun setupDataFromIntent() {
        val orderNumber = intent.getStringExtra("ORDER_NUMBER")
            ?: getString(R.string.default_order_number)
        val driverName = intent.getStringExtra("DRIVER_NAME")
            ?: getString(R.string.default_driver_full_name)

        binding.tvHeaderOrderNumber.text = orderNumber
        binding.tvDriverName.text = driverName

        // Alternar visualmente entre "En camino" y "Entregado" al tocar la tarjeta de estado
        updateUIState(isDelivered = false)
    }

    private fun updateUIState(isDelivered: Boolean) {
        isDeliveredState = isDelivered

        if (!isDelivered) {
            // Estado: En camino
            binding.cardStatusBanner.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.primary)
            )
            binding.ivBannerIcon.setImageResource(R.drawable.ic_scooter)
            binding.tvBannerTitle.text = getString(R.string.tracking_status_on_the_way)
            binding.tvBannerSubtitle.text = getString(R.string.tracking_status_on_the_way_sub)

            // Timeline
            binding.step3IconContainer.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.primary)
            )
            binding.ivStep3Icon.setImageResource(R.drawable.ic_scooter)
            binding.tvStep3Status.visibility = View.VISIBLE

            binding.lineStep3To4.setBackgroundColor(Color.parseColor("#E0E0E0"))
            binding.step4IconContainer.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor("#F0F1F5")
            )
            binding.ivStep4Icon.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.muted)
            )
            binding.tvStep4Title.setTextColor(ContextCompat.getColor(this, R.color.muted))
            binding.tvStep4Desc.setTextColor(ContextCompat.getColor(this, R.color.muted))
            binding.tvStep4Status.visibility = View.GONE
        } else {
            // Estado: Entregado (Verde absoluto)
            binding.cardStatusBanner.setCardBackgroundColor(Color.parseColor("#00C853"))
            binding.ivBannerIcon.setImageResource(R.drawable.ic_location)
            binding.tvBannerTitle.text = getString(R.string.tracking_status_delivered)
            binding.tvBannerSubtitle.text = getString(R.string.tracking_status_delivered_sub)

            // Timeline
            binding.step3IconContainer.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor("#00C853")
            )
            binding.ivStep3Icon.setImageResource(R.drawable.ic_check)
            binding.tvStep3Status.visibility = View.GONE

            binding.lineStep3To4.setBackgroundColor(Color.parseColor("#00C853"))
            binding.step4IconContainer.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor("#FF5722")
            )
            binding.ivStep4Icon.setImageResource(R.drawable.ic_location)
            binding.ivStep4Icon.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.surface)
            )
            binding.tvStep4Title.setTextColor(ContextCompat.getColor(this, R.color.ink))
            binding.tvStep4Desc.setTextColor(ContextCompat.getColor(this, R.color.muted))
            binding.tvStep4Status.visibility = View.VISIBLE
            binding.tvStep4Status.text = getString(R.string.tracking_in_progress)
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        // Permite cambiar dinámicamente de estado para testing/demostración
        binding.cardStatusBanner.setOnClickListener {
            updateUIState(!isDeliveredState)
        }

        binding.btnCallDriver.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+573124458821")
            }
            startActivity(intent)
        }

        binding.btnChatDriver.setOnClickListener {
            Toast.makeText(this, "Abriendo chat con el repartidor...", Toast.LENGTH_SHORT).show()
        }

        binding.btnRateOrder.setOnClickListener {
            Toast.makeText(this, "¡Gracias por calificar tu pedido!", Toast.LENGTH_SHORT).show()
        }
    }
}