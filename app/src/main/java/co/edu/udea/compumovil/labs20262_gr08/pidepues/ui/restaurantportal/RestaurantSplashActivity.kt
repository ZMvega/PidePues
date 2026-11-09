package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurantportal

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityRestaurantSplashBinding

class RestaurantSplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantSplashBinding
    private val handler = Handler(Looper.getMainLooper())

    private val goToLogin = Runnable {
        navigateToLogin()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantSplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rootSplash.setOnClickListener {
            handler.removeCallbacks(goToLogin)
            navigateToLogin()
        }

        handler.postDelayed(goToLogin, 2000L)
    }

    override fun onDestroy() {
        handler.removeCallbacks(goToLogin)
        super.onDestroy()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, RestaurantLoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
