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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Order
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderStatus
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.labelRes
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.OrdersViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.formatOrderDate
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop

/** "Mis pedidos": pestañas En curso e Historial. */
@Composable
fun OrdersScreen(
    ordersViewModel: OrdersViewModel,
    onBack: () -> Unit,
    onOpenOrder: (String) -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    val state by ordersViewModel.state.collectAsStateWithLifecycle()
    var showHistory by rememberSaveable { mutableStateOf(false) }

    val list = if (showHistory) state.history else state.active

    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        AppTopBar(title = stringResource(R.string.orders_title), onBack = onBack)

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PidePuesChip(stringResource(R.string.orders_tab_active), !showHistory, { showHistory = false })
            PidePuesChip(stringResource(R.string.orders_tab_history), showHistory, { showHistory = true })
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (state.isLoading && state.orders.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
            } else if (state.loadError && state.orders.isEmpty()) {
                item { CenteredMessage(stringResource(R.string.orders_load_error)) }
            } else if (list.isEmpty()) {
                item {
                    CenteredMessage(
                        stringResource(if (showHistory) R.string.orders_empty_history else R.string.orders_empty_active)
                    )
                }
            } else {
                items(list, key = { it.id }) { order ->
                    OrderCard(
                        order = order,
                        onClick = { onOpenOrder(order.id) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                if (!showHistory && list.size == 1) {
                    item {
                        Text(
                            text = stringResource(R.string.orders_footer),
                            color = Muted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CenteredMessage(text: String) {
    Text(
        text = text,
        color = Muted,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(32.dp)
    )
}

@Composable
private fun OrderCard(order: Order, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val resources = LocalContext.current.resources
    val dishes = resources.getQuantityString(R.plurals.order_dishes, order.itemCount, order.itemCount)
    AppCard(modifier = modifier.fillMaxWidth(), onClick = onClick, cornerRadius = 16.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmojiTile(
                emoji = order.restaurantEmoji,
                background = foodColor(order.restaurantId),
                size = 52.dp,
                fontSize = 26.sp,
                cornerRadius = 14.dp
            )
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(order.restaurantName, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = order.shortCode + " · " + formatOrderDate(order.createdAtMillis),
                    color = Muted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = dishes + " · " + order.total.toCop(),
                    color = InkSoft,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            StatusChip(order.status)
        }
    }
}

@Composable
private fun StatusChip(status: OrderStatus) {
    val (background, content) = when (status) {
        OrderStatus.RECEIVED -> TileBlue to IconBlue
        OrderStatus.PREPARING -> TileAmber to Primary
        OrderStatus.ON_THE_WAY -> TileOrange to Primary
        OrderStatus.DELIVERED -> TileGreen to IconGreen
    }
    Text(
        text = stringResource(status.labelRes),
        color = content,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
