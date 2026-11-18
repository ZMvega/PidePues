package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.profile

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityProfileBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ItemProfileOptionBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.auth.LoginActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.applySystemBarsInsets

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Arriba: "Mi perfil" baja del notch Abajo: el contenido sube de la barra de navegación.
        binding.topBar.applySystemBarsInsets(bottom = false)
        binding.profileContent.applySystemBarsInsets(top = false)

        configurarOpciones()

        binding.rowLogout.setOnClickListener {
            startActivity(LoginActivity.newIntent(this))
        }
    }

    private fun configurarOpciones() {
        // MICUENTA
        binding.optAddresses.setup(R.string.opt_addresses_title, R.string.opt_addresses_subtitle, R.drawable.ic_location, R.color.primary, R.color.tile_orange)
        binding.optPayments.setup(R.string.opt_payments_title, R.string.opt_payments_subtitle, R.drawable.ic_credit_card, R.color.icon_blue, R.color.tile_blue)
        binding.optFavorites.setup(R.string.opt_favorites_title, R.string.opt_favorites_subtitle, R.drawable.ic_favorite_border, R.color.icon_pink, R.color.tile_pink)
        binding.optHistory.setup(R.string.opt_history_title, R.string.opt_history_subtitle, R.drawable.ic_history, R.color.gold, R.color.tile_amber)
        //PREFERENCIAS
        binding.optNotifications.setup(R.string.opt_notifications_title, R.string.opt_notifications_subtitle, R.drawable.ic_notifications, R.color.icon_green, R.color.tile_green)
        binding.optSettings.setup(R.string.opt_settings_title, R.string.opt_settings_subtitle, R.drawable.ic_settings, R.color.muted, R.color.tile_gray)
        //SOPORTE
        binding.optHelp.setup(R.string.opt_help_title, R.string.opt_help_subtitle, R.drawable.ic_edit, R.color.icon_blue, R.color.tile_blue)
        binding.optTerms.setup(R.string.opt_terms_title, R.string.opt_terms_subtitle, R.drawable.ic_description, R.color.muted, R.color.tile_gray)
    }

    /** Llena una fila reutilizable con su texto, icono y colores. */
    private fun ItemProfileOptionBinding.setup(
        @StringRes title: Int,
        @StringRes subtitle: Int,
        @DrawableRes icon: Int,
        @ColorRes iconColor: Int,
        @ColorRes tileColor: Int
    ) {
        tvTitle.setText(title)
        tvSubtitle.setText(subtitle)
        ivIcon.setImageResource(icon)
        ivIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ProfileActivity, iconColor))
        ivIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ProfileActivity, tileColor))
    }

    companion object {
        /** Abre el perfil limpiando la pila */
        fun newIntent(context: Context): Intent =
            Intent(context, ProfileActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }
}
