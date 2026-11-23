package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.DeliveryOrder
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantAffiliation
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.SessionViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop
import java.util.Locale

private enum class DeliveryTab(val label: String, val icon: Int) {
    AVAILABLE("Disponibles", R.drawable.ic_shopping_bag),
    ACTIVE("En curso", R.drawable.ic_location),
    HISTORY("Historial", R.drawable.ic_history),
    PROFILE("Perfil", R.drawable.ic_person)
}

@Composable
fun DeliveryDashboardScreen(
    sessionViewModel: SessionViewModel,
    deliveryViewModel: DeliveryDashboardViewModel,
    onLogout: () -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    val context = LocalContext.current
    val profile by sessionViewModel.profile.collectAsStateWithLifecycle()
    val state by deliveryViewModel.state.collectAsStateWithLifecycle()

    var currentTab by rememberSaveable { mutableStateOf(DeliveryTab.AVAILABLE) }
    var showAffiliations by rememberSaveable { mutableStateOf(false) }
    var routeOrderId by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmOrderId by rememberSaveable { mutableStateOf<String?>(null) }
    var detailOrderId by rememberSaveable { mutableStateOf<String?>(null) }

    val gpsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        deliveryViewModel.setGpsEnabled(granted)
    }

    fun requestGps() {
        gpsLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    DisposableEffect(profile?.uid) {
        val uid = profile?.uid
        if (uid != null) deliveryViewModel.observe(uid)
        onDispose { deliveryViewModel.stop() }
    }

    LaunchedEffect(state.message) {
        val msg = state.message
        if (msg != null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            deliveryViewModel.clearMessage()
        }
    }

    // 1) Pantalla de activación de GPS inicial
    if (!state.gpsOnboardingSeen) {
        DeliveryGpsOnboardingScreen(
            onAllowGps = { requestGps() },
            onSkip = { deliveryViewModel.skipGpsOnboarding() }
        )
        return
    }

    // 2) Subpantallas de pantalla completa
    val confirmOrder = state.activeOrders.firstOrNull { it.id == confirmOrderId }
    if (confirmOrder != null) {
        ConfirmDeliveryScreen(
            order = confirmOrder,
            onBack = { confirmOrderId = null },
            onComplete = { proofUri, note ->
                deliveryViewModel.completeDelivery(confirmOrder.id, proofUri, note) { ok ->
                    if (ok) {
                        confirmOrderId = null
                        routeOrderId = null
                    }
                }
            }
        )
        return
    }

    val routeOrder = state.activeOrders.firstOrNull { it.id == routeOrderId }
    if (routeOrder != null) {
        val index = state.activeOrders.indexOfFirst { it.id == routeOrder.id } + 1
        DeliveryRouteScreen(
            order = routeOrder,
            positionIndex = index,
            totalActive = state.activeOrders.size,
            gpsEnabled = state.gpsEnabled,
            onEnableGps = { requestGps() },
            onBack = { routeOrderId = null },
            onFinishDelivery = { confirmOrderId = routeOrder.id }
        )
        return
    }

    val detailOrder = state.historyOrders.firstOrNull { it.id == detailOrderId }
    if (detailOrder != null) {
        DeliveryDetailScreen(
            order = detailOrder,
            onBack = { detailOrderId = null }
        )
        return
    }

    if (showAffiliations) {
        AffiliationsScreen(
            pending = state.pendingInvitations,
            accepted = state.acceptedAffiliations,
            onBack = { showAffiliations = false },
            onAccept = deliveryViewModel::acceptInvitation,
            onRejectOrRemove = deliveryViewModel::rejectOrRemoveAffiliation
        )
        return
    }

    BackHandler(enabled = currentTab != DeliveryTab.AVAILABLE) {
        currentTab = DeliveryTab.AVAILABLE
    }

    // 3) Contenedor principal con barra inferior de 4 pestañas
    Scaffold(
        containerColor = ScreenBg,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                DeliveryTab.entries.forEach { tab ->
                    val showBadge = tab == DeliveryTab.ACTIVE && state.activeOrders.isNotEmpty()
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = {
                            if (showBadge) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = Primary, contentColor = Color.White) {
                                            Text(state.activeOrders.size.toString())
                                        }
                                    }
                                ) {
                                    Icon(painterResource(tab.icon), contentDescription = null)
                                }
                            } else {
                                Icon(painterResource(tab.icon), contentDescription = null)
                            }
                        },
                        label = { Text(tab.label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            indicatorColor = TileOrange,
                            unselectedIconColor = Hint,
                            unselectedTextColor = Hint
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding).fillMaxSize()) {
            when (currentTab) {
                DeliveryTab.AVAILABLE -> AvailableOrdersTab(
                    state = state,
                    onToggleGps = { if (state.gpsEnabled) deliveryViewModel.setGpsEnabled(false) else requestGps() },
                    onOpenAffiliations = { showAffiliations = true },
                    onTakeOrder = { order ->
                        profile?.let { deliveryViewModel.takeOrder(order, it) }
                    }
                )
                DeliveryTab.ACTIVE -> ActiveOrdersTab(
                    state = state,
                    onToggleGps = { if (state.gpsEnabled) deliveryViewModel.setGpsEnabled(false) else requestGps() },
                    onMoveUp = { deliveryViewModel.moveOrderPriority(it, -1) },
                    onMoveDown = { deliveryViewModel.moveOrderPriority(it, 1) },
                    onDeliverFirst = deliveryViewModel::deliverFirst,
                    onOpenRoute = { routeOrderId = it }
                )
                DeliveryTab.HISTORY -> DeliveryHistoryScreen(
                    state = state,
                    onSelectFilter = deliveryViewModel::selectHistoryFilter,
                    onChangePage = deliveryViewModel::setHistoryPage,
                    onOpenDetail = { detailOrderId = it }
                )
                DeliveryTab.PROFILE -> DeliveryProfileScreen(
                    profile = profile,
                    state = state,
                    onToggleGps = { enabled ->
                        if (enabled) requestGps() else deliveryViewModel.setGpsEnabled(false)
                    },
                    onOpenAffiliations = { showAffiliations = true },
                    onLogout = {
                        deliveryViewModel.stop()
                        onLogout()
                    }
                )
            }
        }
    }
}

@Composable
private fun DeliveryGpsOnboardingScreen(
    onAllowGps: () -> Unit,
    onSkip: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(36.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
            Box(Modifier.size(160.dp).border(1.dp, PeachStroke, CircleShape))
            Box(Modifier.size(120.dp).border(1.dp, PeachStroke, CircleShape))
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
        Spacer(Modifier.height(28.dp))
        Text(
            text = "Activa tu ubicación en\ntiempo real",
            color = Ink,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Necesitamos el GPS para que tus clientes vean dónde está su pedido durante la entrega.",
            color = Muted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        listOf(
            "Seguimiento en vivo para el cliente",
            "Solo activo con pedidos \"En camino\"",
            "Obligatorio para tomar pedidos"
        ).forEach { benefit ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(26.dp).clip(CircleShape).background(TileOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = Primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Text(benefit, color = InkSoft, fontSize = 14.sp)
            }
        }
        Spacer(Modifier.weight(1f))
        PrimaryButton(text = "Permitir acceso al GPS", onClick = onAllowGps)
        Spacer(Modifier.height(10.dp))
        TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("Ahora no", color = InkSoft, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun DeliveryGpsBadge(enabled: Boolean, onClick: () -> Unit) {
    val bg = if (enabled) TileGreen else TileRed
    val fg = if (enabled) IconGreen else Danger
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(fg))
        Spacer(Modifier.width(6.dp))
        Text(
            text = if (enabled) "GPS activo" else "GPS apagado",
            color = fg,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CapacityProgressCard(assigned: Int, max: Int) {
    val free = (max - assigned).coerceAtLeast(0)
    AppCard(Modifier.fillMaxWidth(), cornerRadius = 16.dp, elevation = 1.dp) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Text("$assigned de $max ", color = Primary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text("pedidos asignados", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "$free libres",
                    color = IconGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(TileGreen)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                for (i in 1..max) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (i <= assigned) Primary else OutlineGray)
                    )
                }
            }
        }
    }
}

@Composable
private fun AvailableOrdersTab(
    state: DeliveryUiState,
    onToggleGps: () -> Unit,
    onOpenAffiliations: () -> Unit,
    onTakeOrder: (DeliveryOrder) -> Unit
) {
    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Pedidos disponibles", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Preparados y listos para recoger", color = Muted, fontSize = 13.sp)
            }
            DeliveryGpsBadge(enabled = state.gpsEnabled, onClick = onToggleGps)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { CapacityProgressCard(state.activeOrders.size, state.maxActiveOrders) }

            if (!state.gpsEnabled) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(TileRed)
                            .clickable(onClick = onToggleGps)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconTile(R.drawable.ic_location, Color.White, Danger, size = 40.dp)
                        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            Text("GPS apagado.", color = Danger, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Actívalo para poder tomar pedidos y que el cliente te siga.", color = Danger, fontSize = 12.sp)
                        }
                        Icon(painterResource(R.drawable.ic_chevron_right), null, tint = Danger, modifier = Modifier.size(18.dp))
                    }
                }
            }

            if (state.pendingInvitations.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Peach)
                            .clickable(onClick = onOpenAffiliations)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(state.pendingInvitations.size.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Invitaciones pendientes de restaurantes",
                            color = PrimaryDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                        )
                        Icon(painterResource(R.drawable.ic_chevron_right), null, tint = Primary, modifier = Modifier.size(18.dp))
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Bandeja de salida · ${state.availableOrders.size}",
                        color = Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("● En vivo", color = IconGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (state.availableOrders.isEmpty()) {
                item {
                    Text(
                        text = "No hay pedidos preparados por ahora. Cuando un restaurante marque un pedido como Preparado aparecerá aquí.",
                        color = Muted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            } else {
                itemsIndexed(state.availableOrders, key = { _, o -> o.id }) { idx, order ->
                    AvailableOrderCard(
                        order = order,
                        isNew = idx == 0,
                        canTake = state.canTakeMoreOrders,
                        gpsEnabled = state.gpsEnabled,
                        onTake = { onTakeOrder(order) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AvailableOrderCard(
    order: DeliveryOrder,
    isNew: Boolean,
    canTake: Boolean,
    gpsEnabled: Boolean,
    onTake: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, if (isNew) Primary else OutlineGray)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFD97706)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(order.restaurantInitial, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text(order.restaurantName, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Pedido #${order.orderNumber} · ${order.formattedTime}", color = Muted, fontSize = 12.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    if (isNew) {
                        Text(
                            text = "NUEVO",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(Primary).padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                    Text(
                        text = "Preparado",
                        color = Color(0xFF6D28D9),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(Color(0xFFEDE9FE)).padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Recoger en", color = Muted, fontSize = 11.sp)
            Text(order.pickupAddress, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            Text("Entregar en", color = Muted, fontSize = 11.sp)
            Text(order.deliveryAddress, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${order.itemCount} productos · ${String.format(Locale.US, "%.1f", order.distanceKm)} km",
                    color = Muted,
                    fontSize = 12.sp
                )
                Text(order.earningCop.toCop(), color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            PrimaryButton(
                text = when {
                    !gpsEnabled -> "Activa el GPS para tomar"
                    !canTake -> "Cupo máximo alcanzado (5/5)"
                    else -> "Tomar pedido"
                },
                enabled = canTake,
                onClick = onTake
            )
        }
    }
}

@Composable
private fun ActiveOrdersTab(
    state: DeliveryUiState,
    onToggleGps: () -> Unit,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit,
    onDeliverFirst: (String) -> Unit,
    onOpenRoute: (String) -> Unit
) {
    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Mis entregas activas", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Ordena tu ruta como te convenga", color = Muted, fontSize = 13.sp)
            }
            DeliveryGpsBadge(enabled = state.gpsEnabled, onClick = onToggleGps)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { CapacityProgressCard(state.activeOrders.size, state.maxActiveOrders) }
            item {
                Text(
                    text = "Usa las flechas o \"Entregar primero\" para cambiar la prioridad. La entrega #1 es la siguiente en tu ruta.",
                    color = Muted,
                    fontSize = 12.sp
                )
            }
            if (state.activeOrders.isEmpty()) {
                item {
                    Text(
                        text = "No tienes entregas activas. Toma un pedido en la pestaña Disponibles.",
                        color = Muted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    )
                }
            } else {
                itemsIndexed(state.activeOrders, key = { _, o -> o.id }) { idx, order ->
                    ActiveOrderCard(
                        order = order,
                        rank = idx + 1,
                        isFirst = idx == 0,
                        isLast = idx == state.activeOrders.lastIndex,
                        onMoveUp = { onMoveUp(order.id) },
                        onMoveDown = { onMoveDown(order.id) },
                        onDeliverFirst = { onDeliverFirst(order.id) },
                        onOpenRoute = { onOpenRoute(order.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveOrderCard(
    order: DeliveryOrder,
    rank: Int,
    isFirst: Boolean,
    isLast: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDeliverFirst: () -> Unit,
    onOpenRoute: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        border = BorderStroke(1.dp, if (isFirst) Primary else OutlineGray)
    ) {
        Column {
            if (isFirst) {
                Box(
                    modifier = Modifier.fillMaxWidth().background(Primary).padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text("➤ SIGUIENTE ENTREGA", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(32.dp).clip(CircleShape).background(if (isFirst) Primary else CircleGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(rank.toString(), color = if (isFirst) Color.White else Ink, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "↑",
                            color = if (isFirst) OutlineGray else Ink,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(CircleGray)
                                .clickable(enabled = !isFirst, onClick = onMoveUp).padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "↓",
                            color = if (isLast) OutlineGray else Ink,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(CircleGray)
                                .clickable(enabled = !isLast, onClick = onMoveDown).padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#${order.orderNumber} · ${order.customerName}",
                                color = Ink,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "En camino",
                                color = IconBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(TileBlue).padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Text(order.restaurantName, color = Muted, fontSize = 13.sp)
                        Spacer(Modifier.height(6.dp))
                        Text("📍 ${order.deliveryAddress}", color = InkSoft, fontSize = 13.sp)
                        Text(
                            text = "⏱ ${String.format(Locale.US, "%.1f", order.distanceKm)} km · ~${order.etaMinutes} min",
                            color = Muted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                if (isFirst) {
                    PrimaryButton(text = "Iniciar ruta", onClick = onOpenRoute)
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(TileOrange)
                                .clickable(onClick = onDeliverFirst),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Entregar primero", color = Primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CircleGray)
                                .clickable(onClick = onOpenRoute),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Ver ruta", color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AffiliationsScreen(
    pending: List<RestaurantAffiliation>,
    accepted: List<RestaurantAffiliation>,
    onBack: () -> Unit,
    onAccept: (String) -> Unit,
    onRejectOrRemove: (String) -> Unit
) {
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        AppTopBar(title = "Afiliaciones", subtitle = "Restaurantes con los que trabajas", onBack = onBack)
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Invitaciones pendientes", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (pending.isNotEmpty()) {
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape).background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(pending.size.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (pending.isEmpty()) {
                Text("No tienes invitaciones pendientes.", color = Muted, fontSize = 13.sp)
            } else {
                pending.forEach { inv ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Peach,
                        border = BorderStroke(1.dp, PeachStroke)
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(IconGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(inv.initial, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                                    Text(inv.restaurantName, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text(inv.restaurantAddress, color = Muted, fontSize = 12.sp)
                                }
                                Text(inv.timeLabel, color = Muted, fontSize = 11.sp)
                            }
                            Text(
                                text = "Te invitó a unirte como repartidor afiliado.",
                                color = InkSoft,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier.weight(1f).height(44.dp).clip(RoundedCornerShape(12.dp))
                                        .background(Color.White).clickable { onRejectOrRemove(inv.id) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Rechazar", color = Ink, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier.weight(1f).height(44.dp).clip(RoundedCornerShape(12.dp))
                                        .background(Primary).clickable { onAccept(inv.id) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Mis afiliaciones (${accepted.size})", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            accepted.forEach { aff ->
                AppCard(Modifier.fillMaxWidth(), cornerRadius = 16.dp, elevation = 1.dp) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFD97706)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(aff.initial, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            Text(aff.restaurantName, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(aff.restaurantAddress, color = Muted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Text(
                            text = "Afiliado",
                            color = IconGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(TileGreen).padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier.size(30.dp).clip(CircleShape).background(CircleGray)
                                .clickable { onRejectOrRemove(aff.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("×", color = Muted, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}