package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * Color de los iconos de la barra de estado: [darkIcons] = true sobre fondos claros,
 * false sobre fondos oscuros o de color (ej. la cabecera naranja).
 */
@Composable
fun StatusBarAppearance(darkIcons: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkIcons
        }
    }
}
