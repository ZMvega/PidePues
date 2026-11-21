package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.RestaurantsViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun RestaurantProfileScreen(
    restaurantId: Int,
    restaurantsViewModel: RestaurantsViewModel,
    onBack: () -> Unit,
    onViewMenu: () -> Unit
) {
    StatusBarAppearance(darkIcons = false)
    restaurantsViewModel.state.collectAsStateWithLifecycle()
    val found = restaurantsViewModel.findById(restaurantId)

    if (found == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }
    val restaurant = found.restaurant
    var isFavorite by remember(restaurantId) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // ----- Cabecera con fondo, estado, nombre y tarjeta flotante -----
            Box(Modifier.fillMaxWidth()) {
                // Fondo visual del restaurante
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(foodColor(restaurant.id))
                ) {
                    Text(
                        text = restaurant.emoji,
                        fontSize = 96.sp,
                        modifier = Modifier.align(Alignment.Center).padding(bottom = 24.dp)
                    )
                    // Degradado oscuro inferior para legibilidad del texto
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0x33000000), Color.Transparent, Color(0xCC000000))
                                )
                            )
                    )

                    // Botones superiores: Atrás y Favorito
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircleIconButton(
                            icon = R.drawable.ic_arrow_back,
                            onClick = onBack,
                            containerColor = Color(0x66000000),
                            tint = Color.White,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x66000000))
                                .clickable { isFavorite = !isFavorite },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border),
                                contentDescription = "Favorito",
                                tint = if (isFavorite) Color(0xFFFF4D6D) else Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    // Estado, etiquetas, nombre y cocina
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 20.dp, end = 20.dp, bottom = 36.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (restaurant.isOpen) "• Abierto" else "• Cerrado",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (restaurant.isOpen) IconGreen else Danger)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                            if (restaurant.dishTag.isNotBlank()) {
                                Text(
                                    text = restaurant.dishTag,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0x66000000))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = restaurant.category.label,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0x66000000))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = restaurant.name,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = restaurant.cuisine,
                            color = Color(0xFFE5E7EB),
                            fontSize = 14.sp
                        )
                    }
                }

                // Tarjeta flotante de calificación y métricas
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 236.dp),
                    elevation = 4.dp
                ) {
                    Column(Modifier.padding(16.dp)) {
                        // Fila de estrellas + promedio + total de reseñas
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val filledStars = restaurant.rating.roundToInt().coerceIn(0, 5)
                            for (i in 1..5) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_star),
                                    contentDescription = null,
                                    tint = if (i <= filledStars) Gold else OutlineGray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = String.format(Locale.US, "%.1f", restaurant.rating),
                                color = Ink,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "(${restaurant.reviewCount} reseñas)",
                                color = Muted,
                                fontSize = 13.sp
                            )
                        }

                        HorizontalDivider(
                            color = DividerGray,
                            modifier = Modifier.padding(vertical = 14.dp)
                        )

                        // 3 columnas: Entrega | Distancia | Envío
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MetricColumn(
                                icon = R.drawable.ic_schedule,
                                value = stringResource(R.string.delivery_minutes, restaurant.deliveryMinutes),
                                label = "Entrega",
                                modifier = Modifier.weight(1f)
                            )
                            MetricColumn(
                                icon = R.drawable.ic_location,
                                value = stringResource(
                                    R.string.delivery_distance,
                                    String.format(Locale.US, "%.1f", found.distanceKm)
                                ),
                                label = "Distancia",
                                modifier = Modifier.weight(1f)
                            )
                            MetricColumn(
                                icon = R.drawable.ic_shopping_bag,
                                value = if (restaurant.deliveryFee == 0) "Gratis" else restaurant.deliveryFee.toCop(),
                                label = "Envío",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ----- Sobre el restaurante -----
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Sobre el restaurante",
                        color = Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = restaurant.description.ifBlank {
                            "Disfruta lo mejor de ${restaurant.cuisine.lowercase()} preparado al momento con ingredientes frescos en ${restaurant.name}."
                        },
                        color = InkSoft,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_location),
                            contentDescription = null,
                            tint = Muted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = restaurant.address,
                            color = Muted,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // ----- Pedido mínimo -----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Peach)
                    .border(1.dp, PeachStroke, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🛒", fontSize = 18.sp)
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "Pedido mínimo: ",
                    color = InkSoft,
                    fontSize = 14.sp
                )
                Text(
                    text = restaurant.minOrder.toCop(),
                    color = Primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(24.dp))
        }

        // ----- Botón inferior fijo -----
        BottomActionBar {
            PrimaryButton(
                text = "Ver Menú Completo →",
                onClick = onViewMenu
            )
        }
    }
}

@Composable
private fun MetricColumn(
    icon: Int,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = value,
            color = Ink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Muted,
            fontSize = 12.sp
        )
    }
}