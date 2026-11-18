package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PidePuesColors = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    secondary = Primary,
    background = Color.White,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    error = Danger
)

/** Tema de la app: siempre claro, igual que los mockups. */
@Composable
fun PidePuesTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PidePuesColors, content = content)
}
