package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.navigation.AppNavigation
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.PidePuesTheme

/** Única Activity de la app: toda la interfaz es Jetpack Compose. */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // De borde a borde; iconos oscuros en las barras (la app es siempre clara).
        // Cada pantalla ajusta el color de los iconos de la barra de estado según su fondo.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        setContent {
            PidePuesTheme {
                AppNavigation()
            }
        }
    }
}
