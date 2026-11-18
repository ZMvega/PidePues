package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta de PidePues (la misma que tenían los recursos XML)
val Primary = Color(0xFFF04E23)
val PrimaryDark = Color(0xFFD63F16)
val PrimaryDisabled = Color(0xFFF8B9A6)
val SplashStart = Color(0xFFF2541F)
val SplashEnd = Color(0xFFA92F0F)
val HeaderStart = Color(0xFFF2552A)
val HeaderEnd = Color(0xFFCF3F17)
val PromoStart = Color(0xFFFFB547)
val PromoEnd = Color(0xFFF5622A)

val Ink = Color(0xFF0F172A)
val InkSoft = Color(0xFF374151)
val Muted = Color(0xFF6B7280)
val Hint = Color(0xFF9CA3AF)

val ScreenBg = Color(0xFFF4F5F7)
val FieldBg = Color(0xFFF8F9FB)
val FieldStroke = Color(0xFFEEF0F4)
val DividerGray = Color(0xFFF1F2F4)
val OutlineGray = Color(0xFFE5E7EB)
val CircleGray = Color(0xFFF3F4F6)

val Gold = Color(0xFFFFB020)
val GoldDot = Color(0xFFFFC83D)
val Danger = Color(0xFFEF4444)

val TileOrange = Color(0xFFFEEDE7)
val TileBlue = Color(0xFFE8F0FE)
val TilePink = Color(0xFFFDE8EE)
val TileAmber = Color(0xFFFFF3E0)
val TileGreen = Color(0xFFE7F8EE)
val TileGray = Color(0xFFF1F2F4)
val TileRed = Color(0xFFFDECEC)
val IconBlue = Color(0xFF3B82F6)
val IconPink = Color(0xFFF43F5E)
val IconGreen = Color(0xFF22C55E)

val Peach = Color(0xFFFFF6F3)
val PeachStroke = Color(0xFFFFE0D5)
val MapPeach = Color(0xFFFFE3D9)
val MapRing = Color(0xFFFFD0BF)
val PinHalo = Color(0x33F04E23)

val White12 = Color(0x1FFFFFFF)
val White20 = Color(0x33FFFFFF)
val White80 = Color(0xCCFFFFFF)
val White90 = Color(0xE6FFFFFF)
val Scrim55 = Color(0x8C000000)

// Colores pastel de las "fotos" de los restaurantes (hasta tener imágenes reales)
private val FoodColors = listOf(
    Color(0xFFFFE4D6), Color(0xFFE3F4E1), Color(0xFFFFF1C9),
    Color(0xFFE6EEFF), Color(0xFFFCE4EC), Color(0xFFEDE7F6)
)

fun foodColor(restaurantId: Int): Color = FoodColors[restaurantId % FoodColors.size]
