package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.FragmentProfileBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ItemProfileOptionBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.auth.LoginActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.applySystemBarsInsets

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Arriba: "Mi perfil" baja del notch. Abajo no hace falta: la barra inferior ya lo cubre.
        binding.topBar.applySystemBarsInsets(bottom = false)

        configurarOpciones()

        binding.rowLogout.setOnClickListener {
            startActivity(LoginActivity.newIntent(requireContext()))
        }
    }

    private fun configurarOpciones() {
        // MI CUENTA
        binding.optAddresses.setup(R.string.opt_addresses_title, R.string.opt_addresses_subtitle, R.drawable.ic_location, R.color.primary, R.color.tile_orange)
        binding.optPayments.setup(R.string.opt_payments_title, R.string.opt_payments_subtitle, R.drawable.ic_credit_card, R.color.icon_blue, R.color.tile_blue)
        binding.optFavorites.setup(R.string.opt_favorites_title, R.string.opt_favorites_subtitle, R.drawable.ic_favorite_border, R.color.icon_pink, R.color.tile_pink)
        binding.optHistory.setup(R.string.opt_history_title, R.string.opt_history_subtitle, R.drawable.ic_history, R.color.gold, R.color.tile_amber)
        // PREFERENCIAS
        binding.optNotifications.setup(R.string.opt_notifications_title, R.string.opt_notifications_subtitle, R.drawable.ic_notifications, R.color.icon_green, R.color.tile_green)
        binding.optSettings.setup(R.string.opt_settings_title, R.string.opt_settings_subtitle, R.drawable.ic_settings, R.color.muted, R.color.tile_gray)
        // SOPORTE
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
        val ctx = root.context
        tvTitle.setText(title)
        tvSubtitle.setText(subtitle)
        ivIcon.setImageResource(icon)
        ivIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, iconColor))
        ivIcon.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(ctx, tileColor))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
