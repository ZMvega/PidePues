package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.OrderStatus
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantOrder
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.IconTile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.PrimaryButton
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop

/**
 * Pantalla "Pedidos en curso" para el rol Restaurante.
 * Conectada en tiempo real a Cloud Firestore y con filtrado por estado.
 */
@Composable
fun RestaurantOrdersScreen(
    dashboard: RestaurantDashboardUiState,
    onSelectFilter: (OrderStatus) -> Unit,
    onAcceptOrder: (RestaurantOrder) -> Unit,
    onAdvanceToPrepared: (RestaurantOrder) -> Unit,
    onAdvanceToEnCamino: (RestaurantOrder) -> Unit,
    modifier: Modifier = Modifier,
    onOrderClick: (RestaurantOrder) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        // Encabezado superior blanco con título, subtítulo dinámico y barra de filtros
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 14.dp)
        ) {
            Text(
                text = stringResource(R.string.orders_in_progress_title),
                color = Ink,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.orders_active_now_format, dashboard.activeOrdersCount),
                color = Muted,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(14.dp))
            OrderFilterTabs(
                selectedFilter = dashboard.selectedFilter,
                solicitadoCount = dashboard.solicitadoCount,
                tomadoCount = dashboard.tomadoCount,
                preparadoCount = dashboard.preparadoCount,
                enCaminoCount = dashboard.enCaminoCount,
                entregadoCount = dashboard.entregadoCount,
                onSelectFilter = onSelectFilter
            )
        }

        // Lista de pedidos del estado seleccionado o estado vacío
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (dashboard.filteredOrders.isEmpty()) {
                item {
                    EmptyOrdersState(status = dashboard.selectedFilter)
                }
            } else {
                items(dashboard.filteredOrders, key = { it.id }) { order ->
                    RestaurantOrderCard(
                        order = order,
                        onClick = { onOrderClick(order) },
                        onAcceptOrder = { onAcceptOrder(order) },
                        onAdvanceToPrepared = { onAdvanceToPrepared(order) },
                        onAdvanceToEnCamino = { onAdvanceToEnCamino(order) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderFilterTabs(
    selectedFilter: OrderStatus,
    solicitadoCount: Int,
    tomadoCount: Int,
    preparadoCount: Int,
    enCaminoCount: Int,
    entregadoCount: Int,
    onSelectFilter: (OrderStatus) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        OrderStatus.entries.forEach { status ->
            val count = when (status) {
                OrderStatus.SOLICITADO -> solicitadoCount
                OrderStatus.TOMADO -> tomadoCount
                OrderStatus.PREPARADO -> preparadoCount
                OrderStatus.EN_CAMINO -> enCaminoCount
                OrderStatus.ENTREGADO -> entregadoCount
            }
            val isSelected = status == selectedFilter

            val (bgColor, textColor) = when (status) {
                OrderStatus.SOLICITADO -> if (isSelected) {
                    Primary to Color.White
                } else {
                    TileOrange to Primary
                }
                OrderStatus.TOMADO -> if (isSelected) {
                    Color(0xFFEAB308) to Color.White
                } else {
                    Color(0xFFFEF3C7) to Color(0xFF92400E)
                }
                OrderStatus.PREPARADO -> if (isSelected) {
                    Color(0xFF8B5CF6) to Color.White
                } else {
                    Color(0xFFEDE9FE) to Color(0xFF6D28D9)
                }
                OrderStatus.EN_CAMINO -> if (isSelected) {
                    Color(0xFF3B82F6) to Color.White
                } else {
                    Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
                }
                OrderStatus.ENTREGADO -> if (isSelected) {
                    Color(0xFF10B981) to Color.White
                } else {
                    Color(0xFFDCFCE7) to Color(0xFF15803D)
                }
            }

            Surface(
                onClick = { onSelectFilter(status) },
                shape = RoundedCornerShape(12.dp),
                color = bgColor,
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 2.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = count.toString(),
                        color = textColor,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(status.labelRes()),
                        color = textColor,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun RestaurantOrderCard(
    order: RestaurantOrder,
    onClick: () -> Unit,
    onAcceptOrder: () -> Unit,
    onAdvanceToPrepared: () -> Unit,
    onAdvanceToEnCamino: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSolicitado = order.status == OrderStatus.SOLICITADO
    val borderColor = if (isSolicitado) Primary else OutlineGray
    val borderWidth = if (isSolicitado) 1.5.dp else 1.dp

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Fila superior: Pedido #XXX, NUEVO badge si aplica, reloj + hora
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.orders_number_prefix, order.orderNumber),
                    color = Ink,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isSolicitado) {
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Primary
                    ) {
                        Text(
                            text = stringResource(R.string.orders_badge_new),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Icon(
                    painter = painterResource(R.drawable.ic_schedule),
                    contentDescription = null,
                    tint = Hint,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = order.formattedTime,
                    color = Hint,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            // Cuerpo: Nombre del cliente y resumen de productos
            Text(
                text = order.customerName,
                color = Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = if (order.itemsSummary.isNotBlank()) order.itemsSummary
                else stringResource(R.string.dashboard_order_item_count, order.itemCount),
                color = Muted,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(12.dp))

            // Fila de totales: "X productos" a la izquierda, "$ XX.XXX" a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dashboard_order_item_count, order.itemCount),
                    color = Hint,
                    fontSize = 13.sp
                )
                Text(
                    text = order.totalCop.toCop(),
                    color = Ink,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Botón de acción inferior según estado
            when (order.status) {
                OrderStatus.SOLICITADO -> {
                    Spacer(Modifier.height(14.dp))
                    PrimaryButton(
                        text = stringResource(R.string.orders_action_accept),
                        onClick = onAcceptOrder,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OrderStatus.TOMADO -> {
                    Spacer(Modifier.height(14.dp))
                    PrimaryButton(
                        text = stringResource(R.string.orders_action_mark_prepared),
                        onClick = onAdvanceToPrepared,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OrderStatus.PREPARADO -> {
                    Spacer(Modifier.height(14.dp))
                    PrimaryButton(
                        text = stringResource(R.string.orders_action_mark_en_camino),
                        onClick = onAdvanceToEnCamino,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OrderStatus.EN_CAMINO -> {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFDBEAFE))
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.orders_status_on_the_way),
                            color = Color(0xFF1D4ED8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                OrderStatus.ENTREGADO -> {
                    Spacer(Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFDCFCE7))
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.orders_status_delivered),
                            color = Color(0xFF15803D),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyOrdersState(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconTile(
                icon = R.drawable.ic_shopping_bag,
                iconTint = Primary,
                background = TileOrange,
                size = 56.dp
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.orders_empty_status_title, stringResource(status.labelRes())),
                color = Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.orders_empty_status_message),
                color = Muted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
