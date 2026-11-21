package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.orders

import androidx.compose.foundation.*
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
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Order
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderStatus
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.emoji
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.labelRes
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.OrdersViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.formatOrderTime
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop
import java.util.Locale

private enum class StepState { DONE, CURRENT, PENDING }

/** Seguimiento en vivo: el estado se actualiza solo cuando cambia el pedido en Firestore. */
@Composable
fun OrderTrackingScreen(
    orderId: String,
    ordersViewModel: OrdersViewModel,
    isDebug: Boolean,
    onBack: () -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    val context = LocalContext.current
    val orders by ordersViewModel.state.collectAsStateWithLifecycle()
    val order = orders.find(orderId)
    val contactSoon = stringResource(R.string.contact_soon)
    val rateSoon = stringResource(R.string.rate_soon)
    val simulateError = stringResource(R.string.simulate_error)

    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        AppTopBar(
            title = stringResource(R.string.tracking_title),
            subtitle = order?.shortCode,
            onBack = onBack
        )

        if (order == null) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ----- Mapa con el tiempo restante -----
            MapCard(height = 190.dp) {
                MapPin(Modifier.align(Alignment.Center))
                if (order.status == OrderStatus.ON_THE_WAY) {
                    Text(
                        text = "🛵",
                        fontSize = 30.sp,
                        modifier = Modifier.align(Alignment.CenterStart).padding(start = 40.dp, top = 40.dp)
                    )
                }
                Text(
                    text = if (order.status.isActive) {
                        stringResource(R.string.tracking_arrives_in, order.remainingMinutes())
                    } else {
                        stringResource(R.string.status_delivered)
                    },
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Primary)
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            // ----- Estado actual -----
            AppCard(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmojiTile(
                        emoji = order.status.emoji,
                        background = TileOrange,
                        size = 52.dp,
                        fontSize = 26.sp,
                        cornerRadius = 16.dp
                    )
                    Column(Modifier.padding(start = 12.dp)) {
                        Text(
                            text = stringResource(order.status.labelRes),
                            color = Ink,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(subtitleFor(order.status)),
                            color = Muted,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // ----- Repartidor (cuando ya fue asignado) -----
            val driver = order.driverName
            if (driver != null) {
                AppCard(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(TileOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(driver.take(1).uppercase(), color = Primary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            Text(driver, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = stringResource(
                                    R.string.tracking_driver_subtitle,
                                    String.format(Locale.US, "%.1f", order.driverRating ?: 5.0)
                                ),
                                color = Muted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        EmojiTile(
                            emoji = "📞",
                            background = TileGreen,
                            size = 40.dp,
                            fontSize = 18.sp,
                            modifier = Modifier.clickable {
                                Toast.makeText(context, contactSoon, Toast.LENGTH_SHORT).show()
                            }
                        )
                        EmojiTile(
                            emoji = "💬",
                            background = TileBlue,
                            size = 40.dp,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 8.dp).clickable {
                                Toast.makeText(context, contactSoon, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }

            // ----- Progreso del pedido -----
            AppCard(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.tracking_progress_title),
                        color = Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OrderStatus.values().forEachIndexed { index, step ->
                        val state = when {
                            order.status == OrderStatus.DELIVERED || order.status.ordinal > index -> StepState.DONE
                            order.status.ordinal == index -> StepState.CURRENT
                            else -> StepState.PENDING
                        }
                        val driverName = order.driverName
                        val description = if (step == OrderStatus.ON_THE_WAY && driverName != null) {
                            stringResource(R.string.step_on_the_way_desc_named, driverName)
                        } else {
                            stringResource(descriptionFor(step))
                        }
                        TimelineStep(
                            title = stringResource(step.labelRes),
                            description = description,
                            time = if (step == OrderStatus.RECEIVED) formatOrderTime(order.createdAtMillis) else null,
                            state = state,
                            isLast = index == OrderStatus.values().lastIndex
                        )
                    }
                }
            }

            // ----- Resumen del pedido -----
            AppCard(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = order.restaurantEmoji + " " + order.restaurantName,
                        color = Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.order_summary_title),
                        color = Muted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                    )
                    order.lines.forEach { line ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                            Text(
                                text = line.quantity.toString() + " × " + line.name,
                                color = Muted,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(text = line.total.toCop(), color = Ink, fontSize = 14.sp)
                        }
                    }
                    HorizontalDivider(color = DividerGray, modifier = Modifier.padding(vertical = 10.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.summary_total),
                            color = Ink,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = order.total.toCop(),
                            color = Primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Acciones fijas abajo
        val canSimulate = isDebug && order.status.next() != null
        val delivered = order.status == OrderStatus.DELIVERED
        if (canSimulate || delivered) {
            BottomActionBar {
                if (delivered) {
                    PrimaryButton(
                        text = stringResource(R.string.btn_rate),
                        onClick = { Toast.makeText(context, rateSoon, Toast.LENGTH_SHORT).show() }
                    )
                }
                if (canSimulate) {
                    OutlinedActionButton(
                        text = stringResource(R.string.debug_next_status),
                        onClick = {
                            ordersViewModel.simulateNextStatus(order) {
                                Toast.makeText(context, simulateError, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun subtitleFor(status: OrderStatus): Int = when (status) {
    OrderStatus.RECEIVED -> R.string.tracking_sub_received
    OrderStatus.PREPARING -> R.string.tracking_sub_preparing
    OrderStatus.ON_THE_WAY -> R.string.tracking_sub_on_the_way
    OrderStatus.DELIVERED -> R.string.tracking_sub_delivered
}

private fun descriptionFor(status: OrderStatus): Int = when (status) {
    OrderStatus.RECEIVED -> R.string.step_received_desc
    OrderStatus.PREPARING -> R.string.step_preparing_desc
    OrderStatus.ON_THE_WAY -> R.string.step_on_the_way_desc
    OrderStatus.DELIVERED -> R.string.step_delivered_desc
}

/** Un paso de la línea de tiempo: círculo (hecho / actual / pendiente), línea y textos. */
@Composable
private fun TimelineStep(
    title: String,
    description: String,
    time: String?,
    state: StepState,
    isLast: Boolean
) {
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(28.dp)) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when (state) {
                            StepState.DONE -> IconGreen
                            StepState.CURRENT -> TileOrange
                            StepState.PENDING -> DividerGray
                        }
                    )
                    .then(
                        if (state == StepState.CURRENT) Modifier.border(2.dp, Primary, CircleShape) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    StepState.DONE -> Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    StepState.CURRENT -> Box(Modifier.size(8.dp).clip(CircleShape).background(Primary))
                    StepState.PENDING -> Unit
                }
            }
            if (!isLast) {
                Box(
                    Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(if (state == StepState.DONE) IconGreen else OutlineGray)
                )
            }
        }
        Column(Modifier.weight(1f).padding(start = 12.dp, bottom = if (isLast) 0.dp else 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = if (state == StepState.PENDING) Hint else Ink,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (time != null) {
                    Text(text = time, color = Muted, fontSize = 12.sp)
                }
            }
            Text(
                text = if (state == StepState.CURRENT) stringResource(R.string.tracking_in_progress) else description,
                color = if (state == StepState.CURRENT) Primary else Muted,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
