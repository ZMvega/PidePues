package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityLoginBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.profile.ProfileActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.applySystemBarsInsets

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginContent.applySystemBarsInsets()

        // Solo navegación para poder recorrer las pantallas todo: autenticación real
        binding.btnLogin.setOnClickListener {
            startActivity(ProfileActivity.newIntent(this))
        }
        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    companion object {
        /** Abre el login limpiando la pila (se usa al cerrar sesión). */
        fun newIntent(context: Context): Intent =
            Intent(context, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
}
