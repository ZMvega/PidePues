package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityCartBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.home.HomeActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.location.DeliveryAddressActivity
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private var itemUnitPrice: Int = 32000
    private var itemQuantity: Int = 1
    private val deliveryFee: Int = 3000
    private var hasItems: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupIntentData()
        setupListeners()
        updateCartState()
    }

    private fun setupIntentData() {
        hasItems = intent.getBooleanExtra("HAS_ITEMS", true)
        val name = intent.getStringExtra("PRODUCT_NAME") ?: "Bandeja Paisa Completa"
        itemUnitPrice = intent.getIntExtra("PRODUCT_PRICE", 32000)
        itemQuantity = intent.getIntExtra("PRODUCT_QUANTITY", 1)
        val options = intent.getStringExtra("PRODUCT_OPTIONS") ?: "Sin cebolla, Extra queso"

        binding.cartItem1.tvCartItemName.text = name
        binding.cartItem1.tvCartItemOptions.text = options
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnExploreRestaurants.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }

        binding.cartItem1.btnCartIncrease.setOnClickListener {
            itemQuantity++
            updateCartState()
        }

        binding.cartItem1.btnCartDecrease.setOnClickListener {
            if (itemQuantity > 1) {
                itemQuantity--
                updateCartState()
            } else {
                removeItem()
            }
        }

        binding.cartItem1.btnDeleteCartItem.setOnClickListener {
            removeItem()
        }

        // Navegación hacia la pantalla 13: Dirección de entrega
        binding.btnCheckout.setOnClickListener {
            startActivity(Intent(this, DeliveryAddressActivity::class.java))
        }
    }

    private fun removeItem() {
        hasItems = false
        updateCartState()
    }

    private fun updateCartState() {
        if (!hasItems || itemQuantity <= 0) {
            binding.layoutCartContent.visibility = View.GONE
            binding.layoutEmptyCart.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyCart.visibility = View.GONE
            binding.layoutCartContent.visibility = View.VISIBLE

            binding.cartItem1.tvCartItemQuantity.text = itemQuantity.toString()

            val subtotal = itemUnitPrice * itemQuantity
            val total = subtotal + deliveryFee

            binding.cartItem1.tvCartItemPrice.text = formatPrice(subtotal)
            binding.tvSubtotalPrice.text = formatPrice(subtotal)
            binding.tvDeliveryPrice.text = formatPrice(deliveryFee)
            binding.tvTotalPrice.text = formatPrice(total)
            binding.btnCheckout.text = "Continuar al pago (${formatPrice(total)})"
        }
    }

    private fun formatPrice(price: Int): String {
        val formatter = NumberFormat.getNumberInstance(Locale("es", "CO"))
        return "$${formatter.format(price)}"
    }
}