package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantWithDistance
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCopNumber
import java.util.Locale

/** Tarjeta de restaurante (Inicio y resultados de Buscar). */
@Composable
fun RestaurantCard(
    item: RestaurantWithDistance,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val restaurant = item.restaurant
    AppCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        // "Foto": color pastel + emoji (se cambia por una imagen real más adelante)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(168.dp)
                .background(foodColor(restaurant.id))
        ) {
            Text(
                text = restaurant.emoji,
                fontSize = 72.sp,
                modifier = Modifier.align(Alignment.Center)
            )
            if (!restaurant.isOpen) {
                Box(Modifier.matchParentSize().background(Scrim55))
                Text(
                    text = stringResource(R.string.restaurant_closed),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(White90)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_star),
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = String.format(Locale.US, "%.1f", restaurant.rating),
                    color = Ink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Text(
                text = restaurant.dishTag,
                color = Ink,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(White90)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
        Column(Modifier.padding(16.dp)) {
            Text(restaurant.name, color = Ink, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(
                text = restaurant.cuisine,
                color = Muted,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoItem(R.drawable.ic_schedule, stringResource(R.string.delivery_minutes, restaurant.deliveryMinutes))
                Spacer(Modifier.width(14.dp))
                InfoItem(
                    R.drawable.ic_location,
                    stringResource(R.string.delivery_distance, String.format(Locale.US, "%.1f", item.distanceKm))
                )
                Spacer(Modifier.width(14.dp))
                Text(
                    text = if (restaurant.deliveryFee == 0) {
                        stringResource(R.string.delivery_free)
                    } else {
                        stringResource(R.string.delivery_fee, restaurant.deliveryFee.toCopNumber())
                    },
                    color = Muted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun InfoItem(icon: Int, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = Muted,
            modifier = Modifier.size(14.dp)
        )
        Text(text = text, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
    }
}
