package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.splash

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivitySplashBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.auth.LoginActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.applySystemBarsInsets

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())

    private val goToLogin = Runnable {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //fondo e iconos claros
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.splashDots.applySystemBarsInsets(top = false)

        //solo diseño: pasa al login después de 2 segundos
        handler.postDelayed(goToLogin, SPLASH_DELAY_MS)
    }

    override fun onDestroy() {
        handler.removeCallbacks(goToLogin)
        super.onDestroy()
    }

    companion object {
        private const val SPLASH_DELAY_MS = 2000L
    }
}
