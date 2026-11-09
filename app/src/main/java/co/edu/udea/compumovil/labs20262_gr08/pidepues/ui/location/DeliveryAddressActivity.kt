package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.location

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityDeliveryAddressBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.payment.PaymentMethodActivity

class DeliveryAddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeliveryAddressBinding
    private var selectedAddressType = "Casa"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAddressSelection()
        setupListeners()
    }

    private fun setupAddressSelection() {
        binding.optAddressHome.setOnClickListener {
            selectedAddressType = "Casa"
            binding.rbAddressHome.isChecked = true
            binding.rbAddressOffice.isChecked = false
        }

        binding.optAddressOffice.setOnClickListener {
            selectedAddressType = "Oficina"
            binding.rbAddressHome.isChecked = false
            binding.rbAddressOffice.isChecked = true
        }

        binding.btnAddNewAddress.setOnClickListener {
            Toast.makeText(this, "Agregar nueva dirección", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnZoomIn.setOnClickListener {
            Toast.makeText(this, "Acercar mapa", Toast.LENGTH_SHORT).show()
        }

        binding.btnZoomOut.setOnClickListener {
            Toast.makeText(this, "Alejar mapa", Toast.LENGTH_SHORT).show()
        }

        // Navegación hacia la pantalla 14: Método de pago
        binding.btnConfirmAddress.setOnClickListener {
            startActivity(Intent(this, PaymentMethodActivity::class.java))
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}