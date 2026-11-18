package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.auth

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRegisterBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.profile.ProfileActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.applySystemBarsInsets

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerContent.applySystemBarsInsets()

        //Terminos de servicio Politica de privacidad
        binding.cbTerms.text = HtmlCompat.fromHtml(
            getString(R.string.register_terms),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )

        //botón activo al aceptar los términos
        binding.cbTerms.setOnCheckedChangeListener { _, isChecked ->
            binding.btnRegister.isEnabled = isChecked
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.tvGoLogin.setOnClickListener { finish() }

        //navegación todo: registro real
        binding.btnRegister.setOnClickListener {
            startActivity(ProfileActivity.newIntent(this))
        }
    }
}
