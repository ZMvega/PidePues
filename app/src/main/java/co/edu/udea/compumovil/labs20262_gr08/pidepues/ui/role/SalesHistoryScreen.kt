package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.StatusBarAppearance
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class SalesPeriodFilter(val labelRes: Int) {
    HOY(R.string.sales_filter_today),
    AYER(R.string.sales_filter_yesterday),
    SIETE_DIAS(R.string.sales_filter_7_days),
    TREINTA_DIAS(R.string.sales_filter_30_days)
}

data class DayGroupedOrders(
    val dayLabel: String,
    val dayTotalCop: Int,
    val orders: List<RestaurantOrder>
)

/**
 * Pantalla "Historial de ventas" para el rol Restaurante.
 * Muestra el resumen dinámico de ventas (Ventas, Pedidos, Ticket prom.) y la lista de pedidos
 * entregados agrupados por día con filtros por período (Hoy, Ayer, 7 días, 30 días).
 */
@Composable
fun SalesHistoryScreen(
    allOrders: List<RestaurantOrder>,
    onBack: () -> Unit,
    onOrderClick: (RestaurantOrder) -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarAppearance(darkIcons = true)
    BackHandler(onBack = onBack)

    var selectedFilter by rememberSaveable { mutableStateOf(SalesPeriodFilter.SIETE_DIAS) }

    val filteredOrders = remember(allOrders, selectedFilter) {
        filterDeliveredOrders(allOrders, selectedFilter)
    }

    val totalSalesCop = remember(filteredOrders) {
        filteredOrders.sumOf { it.totalCop }
    }

    val ordersCount = filteredOrders.size

    val averageTicketCop = remember(totalSalesCop, ordersCount) {
        if (ordersCount > 0) totalSalesCop / ordersCount else 0
    }

    val todayLabel = stringResource(R.string.sales_filter_today)
    val yesterdayLabel = stringResource(R.string.sales_filter_yesterday)
    val daysAgoFormat = stringResource(R.string.sales_days_ago_format)

    val groupedOrders = remember(filteredOrders, todayLabel, yesterdayLabel, daysAgoFormat) {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val yesterdayStart = Calendar.getInstance().apply {
            timeInMillis = todayStart
            add(Calendar.DAY_OF_MONTH, -1)
        }.timeInMillis

        groupOrdersByDay(
            orders = filteredOrders,
            todayStart = todayStart,
            yesterdayStart = yesterdayStart,
            todayLabel = todayLabel,
            yesterdayLabel = yesterdayLabel,
            daysAgoFormat = daysAgoFormat
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ScreenBg,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            SalesHistoryHeader(onBack = onBack)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Barra de filtros por período
            item {
                PeriodFilterRow(
                    selectedFilter = selectedFilter,
                    onSelectFilter = { selectedFilter = it }
                )
            }

            // Tarjeta de resumen de ventas del período
            item {
                SalesSummaryCard(
                    totalSalesCop = totalSalesCop,
                    ordersCount = ordersCount,
                    averageTicketCop = averageTicketCop
                )
            }

            // Estado vacío o lista agrupada por día
            if (groupedOrders.isEmpty()) {
                item {
                    EmptySalesHistoryState()
                }
            } else {
                items(groupedOrders.size, key = { groupedOrders[it].dayLabel }) { idx ->
                    DayGroupSection(
                        dayGroup = groupedOrders[idx],
                        onOrderClick = onOrderClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SalesHistoryHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CircleGray)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.cd_back),
                tint = Ink,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.sales_history_title),
                color = Ink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.sales_history_subtitle),
                color = Muted,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun PeriodFilterRow(
    selectedFilter: SalesPeriodFilter,
    onSelectFilter: (SalesPeriodFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SalesPeriodFilter.entries.forEach { filter ->
            val isSelected = filter == selectedFilter
            Surface(
                onClick = { onSelectFilter(filter) },
                shape = RoundedCornerShape(50),
                color = if (isSelected) Primary else Color.White,
                border = if (isSelected) null else BorderStroke(1.dp, OutlineGray),
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(filter.labelRes),
                        color = if (isSelected) Color.White else Ink,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SalesSummaryCard(
    totalSalesCop: Int,
    ordersCount: Int,
    averageTicketCop: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Primary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Izquierda: Ventas del período
            Column(modifier = Modifier.weight(1.3f)) {
                Text(
                    text = stringResource(R.string.sales_period_total),
                    color = White90,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = totalSalesCop.toCop(),
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Centro: Pedidos
            Column(
                modifier = Modifier.weight(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.sales_period_orders),
                    color = White90,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = ordersCount.toString(),
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Derecha: Ticket prom.
            Column(
                modifier = Modifier.weight(1.1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(R.string.sales_period_average_ticket),
                    color = White90,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = averageTicketCop.toCop(),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DayGroupSection(
    dayGroup: DayGroupedOrders,
    onOrderClick: (RestaurantOrder) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Encabezado del grupo por día
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dayGroup.dayLabel,
                color = Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dayGroup.dayTotalCop.toCop(),
                color = Muted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Tarjeta contenedora de las filas del día
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, OutlineGray)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                dayGroup.orders.forEachIndexed { index, order ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = DividerGray,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                    }
                    SalesOrderRow(
                        order = order,
                        onClick = { onOrderClick(order) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SalesOrderRow(
    order: RestaurantOrder,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cuadrado redondeado verde con ícono de check
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFDCFCE7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = Color(0xFF15803D),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        // Datos del pedido (código, cliente, hora, cantidad de productos)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "#${order.orderNumber} · ${order.customerName}",
                color = Ink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${order.formattedTime} · ${stringResource(R.string.dashboard_order_item_count, order.itemCount)}",
                color = Muted,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(8.dp))

        // Total en COP
        Text(
            text = order.totalCop.toCop(),
            color = Ink,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptySalesHistoryState() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                icon = R.drawable.ic_history,
                iconTint = Primary,
                background = TileOrange,
                size = 52.dp
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.sales_history_empty_title),
                color = Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.sales_history_empty_message),
                color = Muted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun filterDeliveredOrders(
    allOrders: List<RestaurantOrder>,
    filter: SalesPeriodFilter
): List<RestaurantOrder> {
    val deliveredOrders = allOrders.filter { it.status == OrderStatus.ENTREGADO }

    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val yesterdayStart = Calendar.getInstance().apply {
        timeInMillis = todayStart
        add(Calendar.DAY_OF_MONTH, -1)
    }.timeInMillis

    val sevenDaysStart = Calendar.getInstance().apply {
        timeInMillis = todayStart
        add(Calendar.DAY_OF_MONTH, -6)
    }.timeInMillis

    val thirtyDaysStart = Calendar.getInstance().apply {
        timeInMillis = todayStart
        add(Calendar.DAY_OF_MONTH, -29)
    }.timeInMillis

    return when (filter) {
        SalesPeriodFilter.HOY -> deliveredOrders.filter { it.createdAtMillis >= todayStart }
        SalesPeriodFilter.AYER -> deliveredOrders.filter {
            it.createdAtMillis >= yesterdayStart && it.createdAtMillis < todayStart
        }
        SalesPeriodFilter.SIETE_DIAS -> deliveredOrders.filter { it.createdAtMillis >= sevenDaysStart }
        SalesPeriodFilter.TREINTA_DIAS -> deliveredOrders.filter { it.createdAtMillis >= thirtyDaysStart }
    }
}

private fun groupOrdersByDay(
    orders: List<RestaurantOrder>,
    todayStart: Long,
    yesterdayStart: Long,
    todayLabel: String,
    yesterdayLabel: String,
    daysAgoFormat: String
): List<DayGroupedOrders> {
    if (orders.isEmpty()) return emptyList()

    val map = orders.groupBy { order ->
        Calendar.getInstance().apply {
            timeInMillis = order.createdAtMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val sortedDayStarts = map.keys.sortedDescending()

    return sortedDayStarts.map { dayStart ->
        val dayOrders = map.getValue(dayStart).sortedByDescending { it.createdAtMillis }
        val dayTotal = dayOrders.sumOf { it.totalCop }

        val label = when (dayStart) {
            todayStart -> todayLabel
            yesterdayStart -> yesterdayLabel
            else -> {
                val diffDays = ((todayStart - dayStart) / (24 * 60 * 60 * 1000)).toInt()
                if (diffDays in 2..7) {
                    String.format(daysAgoFormat, diffDays)
                } else {
                    SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(dayStart))
                }
            }
        }

        DayGroupedOrders(
            dayLabel = label,
            dayTotalCop = dayTotal,
            orders = dayOrders
        )
    }
}
