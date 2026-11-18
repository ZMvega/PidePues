package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common

import android.content.Context
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserRole

/** Lleva al usuario a su pantalla de inicio según el rol (después de iniciar sesión o registrarse). */
object Navigator {
    fun afterAuth(context: Context, role: UserRole) {
        val intent = when (role) {
            UserRole.CLIENT -> LocationPermissionActivity.newIntent(context)
            UserRole.RESTAURANT, UserRole.DELIVERY -> RoleHomeActivity.newIntent(context, role)
        }
        context.startActivity(intent)
    }
}
