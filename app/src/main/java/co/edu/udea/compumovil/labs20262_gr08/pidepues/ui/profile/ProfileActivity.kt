package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityProfileBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.auth.LoginActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.home.HomeActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.location.DeliveryAddressActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.orders.OrdersActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.payment.PaymentMethodActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.restaurantportal.RestaurantSplashActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.search.SearchActivity

data class UserProfileData(
    val fullName: String,
    val email: String,
    val savedAddressesCount: Int,
    val paymentCardLast4: String,
    val favoritesCount: Int,
    val ordersCount: Int
)

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserProfileData()
        setupListeners()
        setupBottomNavigation()
    }

    private fun loadUserProfileData() {
        val user = UserProfileData(
            fullName = getString(R.string.profile_name),
            email = getString(R.string.profile_email),
            savedAddressesCount = 2,
            paymentCardLast4 = "4521",
            favoritesCount = 4,
            ordersCount = 12
        )

        val initials = user.fullName
            .split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .uppercase()

        binding.tvUserInitials.text = if (initials.isNotEmpty()) initials else "CR"
        binding.tvUserName.text = user.fullName
        binding.tvUserEmail.text = user.email

        binding.tvHistorySubtitle.text = "${user.ordersCount} pedidos"
        binding.tvAddressesSubtitle.text = "${user.savedAddressesCount} guardadas"
        binding.tvPaymentsSubtitle.text = "Visa •••• ${user.paymentCardLast4}"
        binding.tvFavoritesSubtitle.text = "${user.favoritesCount} restaurantes"
    }

    private fun setupListeners() {
        binding.btnEditProfile.setOnClickListener {
            Toast.makeText(this, "Editar perfil", Toast.LENGTH_SHORT).show()
        }

        binding.btnChangePhoto.setOnClickListener {
            Toast.makeText(this, "Cambiar foto de perfil", Toast.LENGTH_SHORT).show()
        }

        binding.cardSwitchToRestaurant.setOnClickListener {
            val intent = Intent(this, RestaurantSplashActivity::class.java)
            startActivity(intent)
        }

        binding.cardGold.setOnClickListener {
            Toast.makeText(this, "PidePues Gold - Tienes 60 puntos (40 pts para el próximo beneficio)", Toast.LENGTH_LONG).show()
        }

        // Abrir la pantalla de Historial de Pedidos desde la opción de la lista
        binding.optOrderHistory.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }

        binding.optAddresses.setOnClickListener {
            startActivity(Intent(this, DeliveryAddressActivity::class.java))
        }

        binding.optPaymentMethods.setOnClickListener {
            startActivity(Intent(this, PaymentMethodActivity::class.java))
        }

        binding.optFavorites.setOnClickListener {
            Toast.makeText(this, "Restaurantes favoritos", Toast.LENGTH_SHORT).show()
        }

        binding.optNotifications.setOnClickListener {
            Toast.makeText(this, "Notificaciones activadas", Toast.LENGTH_SHORT).show()
        }

        binding.optSettings.setOnClickListener {
            Toast.makeText(this, "Configuración de idioma y privacidad", Toast.LENGTH_SHORT).show()
        }

        binding.optHelp.setOnClickListener {
            Toast.makeText(this, "Centro de ayuda y FAQ", Toast.LENGTH_SHORT).show()
        }

        binding.optTerms.setOnClickListener {
            Toast.makeText(this, "Términos y privacidad v2.1.0", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.nav_profile
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                    true
                }
                R.id.nav_orders -> {
                    startActivity(Intent(this, OrdersActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    })
                    finish()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }
}