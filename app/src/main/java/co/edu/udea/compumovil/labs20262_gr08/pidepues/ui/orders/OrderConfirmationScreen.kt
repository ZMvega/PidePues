package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.orders

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Order
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.labelRes
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.OrdersViewModel

/** "¡Pedido confirmado!": número de pedido, datos principales y acceso al seguimiento. */
@Composable
fun OrderConfirmationScreen(
    orderId: String,
    ordersViewModel: OrdersViewModel,
    onTrack: () -> Unit,
    onHome: () -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    val orders by ordersViewModel.state.collectAsStateWithLifecycle()
    val order = orders.find(orderId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (order == null) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(TileGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(IconGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = stringResource(R.string.confirmed_title),
                    color = Ink,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Text(
                    text = stringResource(R.string.confirmed_subtitle),
                    color = Muted,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )

                // Número de pedido
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Peach)
                        .border(1.dp, PeachStroke, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.order_number_label),
                        color = Muted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = order.shortCode,
                        color = Primary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = stringResource(order.status.labelRes),
                        color = IconGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(TileGreen)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                AppCard(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        InfoRow("🍽️", stringResource(R.string.label_restaurant), order.restaurantName)
                        InfoRow("⏱️", stringResource(R.string.label_eta), stringResource(R.string.eta_minutes_format, order.etaMinutes))
                        InfoRow("📍", stringResource(R.string.label_address), order.address)
                        InfoRow("💳", stringResource(R.string.label_payment), stringResource(order.paymentMethod.labelRes))
                    }
                }
            }

            Column(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                PrimaryButton(text = stringResource(R.string.btn_track), onClick = onTrack)
                OutlinedActionButton(
                    text = stringResource(R.string.btn_home),
                    onClick = onHome,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoRow(emoji: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(text = emoji, fontSize = 18.sp, modifier = Modifier.width(32.dp))
        Column(Modifier.weight(1f)) {
            Text(text = label, color = Muted, fontSize = 12.sp)
            Text(
                text = value,
                color = Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
