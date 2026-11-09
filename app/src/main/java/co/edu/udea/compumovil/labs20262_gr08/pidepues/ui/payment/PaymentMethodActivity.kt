package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.payment

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityPaymentMethodBinding
import java.text.NumberFormat
import java.util.Locale

class PaymentMethodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentMethodBinding
    private var selectedPaymentMethod = "Tarjeta de crédito"
    private var subtotalAmount = 32000
    private var shippingAmount = 4000
    private var serviceFeeAmount = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSummaryValues()
        setupPaymentSelection()
        setupListeners()
    }

    private fun setupSummaryValues() {
        val totalAmount = subtotalAmount + shippingAmount + serviceFeeAmount

        binding.tvSummarySubtotal.text = formatPrice(subtotalAmount)
        binding.tvSummaryShipping.text = formatPrice(shippingAmount)
        binding.tvSummaryServiceFee.text = formatPrice(serviceFeeAmount)
        binding.tvSummaryTotal.text = formatPrice(totalAmount)
        binding.tvPayNowTotal.text = formatPrice(totalAmount)
    }

    private fun setupPaymentSelection() {
        val radioButtons = listOf(
            binding.rbCreditCard,
            binding.rbNequi,
            binding.rbDaviplata,
            binding.rbCash
        )

        binding.optCreditCard.setOnClickListener {
            selectMethod("Tarjeta de crédito", binding.rbCreditCard, radioButtons)
        }

        binding.optNequi.setOnClickListener {
            selectMethod("Nequi", binding.rbNequi, radioButtons)
        }

        binding.optDaviplata.setOnClickListener {
            selectMethod("Daviplata", binding.rbDaviplata, radioButtons)
        }

        binding.optCash.setOnClickListener {
            selectMethod("Efectivo", binding.rbCash, radioButtons)
        }
    }

    private fun selectMethod(
        methodName: String,
        targetRadio: RadioButton,
        allRadios: List<RadioButton>
    ) {
        selectedPaymentMethod = methodName
        allRadios.forEach { rb -> rb.isChecked = (rb == targetRadio) }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnAddCard.setOnClickListener {
            Toast.makeText(this, "Agregar nueva tarjeta", Toast.LENGTH_SHORT).show()
        }

        binding.btnPayNow.setOnClickListener {
            val intent = Intent(this, OrderConfirmationActivity::class.java).apply {
                putExtra("ORDER_NUMBER", "#PP-5477")
                putExtra("RESTAURANT_NAME", "Burger Bros")
                putExtra("ESTIMATED_TIME", "30–40 minutos")
                putExtra("DELIVERY_ADDRESS", "Cra 43 #1-15, El Poblado, Medellín")
            }
            startActivity(intent)
            finish()
        }
    }

    private fun formatPrice(price: Int): String {
        val locale = Locale.Builder().setLanguage("es").setRegion("CO").build()
        val formatter = NumberFormat.getNumberInstance(locale)
        return "$${formatter.format(price)}"
    }
}