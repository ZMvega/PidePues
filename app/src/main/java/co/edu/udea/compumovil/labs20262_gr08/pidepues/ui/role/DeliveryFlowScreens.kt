package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.DeliveryHistoryFilter
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.DeliveryOrder
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop
import java.util.Locale

/** Pantalla de ruta en vivo con datos del cliente y botón Finalizar entrega. */
@Composable
fun DeliveryRouteScreen(
    order: DeliveryOrder,
    positionIndex: Int,
    totalActive: Int,
    gpsEnabled: Boolean,
    onEnableGps: () -> Unit,
    onBack: () -> Unit,
    onFinishDelivery: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current

    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        // Mapa superior con ruta punteada
        Box(Modifier.fillMaxWidth().weight(1f)) {
            MapCard(modifier = Modifier.fillMaxSize(), height = 400.dp) {
                Canvas(Modifier.fillMaxSize()) {
                    drawLine(
                        color = Primary,
                        start = Offset(size.width * 0.1f, size.height * 0.72f),
                        end = Offset(size.width * 0.88f, size.height * 0.25f),
                        strokeWidth = 10f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f), 0f)
                    )
                }
                MapPin(Modifier.align(Alignment.Center))
            }

            // Barra flotante: atrás + estado GPS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(icon = R.drawable.ic_arrow_back, onClick = onBack, containerColor = Color.White)
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .clickable { if (!gpsEnabled) onEnableGps() }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = if (gpsEnabled) "● GPS activo · el cliente ve tu ruta" else "● GPS apagado · el cliente no te ve",
                        color = if (gpsEnabled) IconGreen else Danger,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Coordenadas y ETA
            Text(
                text = "6.20860, -75.56790",
                color = InkSoft,
                fontSize = 11.sp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(start = 16.dp, top = 74.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(end = 16.dp, top = 70.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Llegada", color = Muted, fontSize = 10.sp)
                Text("${order.etaMinutes} min", color = Primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Panel inferior con detalles y botón Finalizar entrega
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(Modifier.navigationBarsPadding().padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Pedido #${order.orderNumber}", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Entrega $positionIndex de $totalActive · ${String.format(Locale.US, "%.1f", order.distanceKm)} km",
                            color = Muted,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = "En camino",
                        color = IconBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(TileBlue).padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (!gpsEnabled) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TileRed)
                            .clickable(onClick = onEnableGps)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Activar GPS para que el cliente te siga", color = Danger, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFD97706)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(order.restaurantInitial, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.padding(start = 12.dp)) {
                        Text("Recogido en", color = Muted, fontSize = 11.sp)
                        Text("${order.restaurantName} · ${order.pickupAddress}", color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile(R.drawable.ic_location, Primary, TileOrange, size = 38.dp)
                    Column(Modifier.padding(start = 12.dp)) {
                        Text("Dirección de entrega", color = Muted, fontSize = 11.sp)
                        Text(order.deliveryAddress, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(38.dp).clip(CircleShape).background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(order.customerInitial, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(order.customerName, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(order.customerPhone.ifBlank { "Sin teléfono" }, color = Muted, fontSize = 12.sp)
                    }
                    if (order.customerPhone.isNotBlank()) {
                        CircleIconButton(
                            icon = R.drawable.ic_phone,
                            onClick = {
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.customerPhone}")))
                                }.onFailure {
                                    Toast.makeText(context, "No se pudo abrir llamadas", Toast.LENGTH_SHORT).show()
                                }
                            },
                            containerColor = TileGreen,
                            tint = IconGreen
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))
                PrimaryButton(text = "✓ Finalizar entrega", onClick = onFinishDelivery)
            }
        }
    }
}

/** Pantalla Confirmar entrega con foto obligatoria y nota opcional. */
@Composable
fun ConfirmDeliveryScreen(
    order: DeliveryOrder,
    onBack: () -> Unit,
    onComplete: (proofUri: String, note: String) -> Unit
) {
    BackHandler(onBack = onBack)
    var proofUri by remember { mutableStateOf<String?>(null) }
    var note by remember { mutableStateOf("") }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) proofUri = uri.toString() }

    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        AppTopBar(
            title = "Confirmar entrega",
            subtitle = "Pedido #${order.orderNumber} · ${order.customerName}",
            onBack = onBack
        )

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppCard(Modifier.fillMaxWidth(), cornerRadius = 16.dp, elevation = 1.dp) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(42.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFD97706)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(order.restaurantInitial, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                        Text(order.restaurantName, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(order.deliveryAddress, color = Muted, fontSize = 12.sp)
                    }
                    Text(
                        text = "En camino",
                        color = IconBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(TileBlue).padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Foto de respaldo", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(
                    text = "Obligatoria",
                    color = Primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(TileOrange).padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, if (proofUri != null) IconGreen else OutlineGray)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconTile(
                        icon = R.drawable.ic_camera,
                        iconTint = if (proofUri != null) IconGreen else Primary,
                        background = if (proofUri != null) TileGreen else TileOrange,
                        size = 52.dp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = if (proofUri != null) "Foto adjuntada correctamente" else "Agrega una foto de la entrega",
                        color = Ink,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pedido en la puerta, portería o con el cliente",
                        color = Muted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier.weight(1f).height(44.dp).clip(RoundedCornerShape(12.dp))
                                .background(Primary)
                                .clickable { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📷 Tomar foto", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Box(
                            modifier = Modifier.weight(1f).height(44.dp).clip(RoundedCornerShape(12.dp))
                                .background(CircleGray)
                                .clickable { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🖼 Adjuntar", color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Usar foto de ejemplo (demo)",
                        color = Muted,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { proofUri = "demo://delivery_proof_${order.id}" }.padding(4.dp)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Nota de entrega", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("Opcional", color = Muted, fontSize = 12.sp)
            }
            AppTextField(
                label = "",
                value = note,
                onValueChange = { if (it.length <= 200) note = it },
                placeholder = "Ej. Entregado en portería a nombre de Valentina.",
                singleLine = false,
                minLines = 3
            )
            Text(
                text = "${note.length}/200",
                color = Muted,
                fontSize = 11.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }

        BottomActionBar {
            PrimaryButton(
                text = "✓ Completar entrega",
                enabled = proofUri != null,
                onClick = { proofUri?.let { onComplete(it, note) } }
            )
            if (proofUri == null) {
                Text(
                    text = "Adjunta una foto para poder completar el pedido.",
                    color = Muted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                )
            }
            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Volver a la ruta", color = Muted, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/** Pestaña Historial de entregas con filtros, resumen de ganancias y paginación. */
@Composable
fun DeliveryHistoryScreen(
    state: DeliveryUiState,
    onSelectFilter: (DeliveryHistoryFilter) -> Unit,
    onChangePage: (Int) -> Unit,
    onOpenDetail: (String) -> Unit
) {
    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text("Historial de entregas", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Tus pedidos completados", color = Muted, fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DeliveryHistoryFilter.entries.forEach { filter ->
                    PidePuesChip(filter.label, state.historyFilter == filter, { onSelectFilter(filter) })
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Brush.horizontalGradient(listOf(HeaderStart, HeaderEnd)))
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Entregas", color = White80, fontSize = 12.sp)
                        Text(state.filteredHistory.size.toString(), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Ganancias del período", color = White80, fontSize = 12.sp)
                        Text(state.periodEarningsCop.toCop(), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (state.pagedHistory.isEmpty()) {
                item {
                    Text(
                        text = "No tienes entregas completadas en este período.",
                        color = Muted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(32.dp)
                    )
                }
            } else {
                items(state.pagedHistory, key = { it.id }) { order ->
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onOpenDetail(order.id) },
                        cornerRadius = 16.dp,
                        elevation = 1.dp
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFD97706)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(order.restaurantInitial, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                                Text("#${order.orderNumber} · ${order.restaurantName}", color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(order.formattedFullDate, color = Muted, fontSize = 11.sp)
                                Text(order.deliveryAddress, color = Muted, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(order.earningCop.toCop(), color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Icon(painterResource(R.drawable.ic_chevron_right), null, tint = Hint, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Paginación
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { onChangePage(state.historyPage - 1) },
                enabled = state.historyPage > 1
            ) { Text("‹ Anterior", color = if (state.historyPage > 1) Ink else Hint) }

            Text("Página ${state.historyPage} de ${state.totalPages}", color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (state.historyPage < state.totalPages) Primary else CircleGray)
                    .clickable(enabled = state.historyPage < state.totalPages) { onChangePage(state.historyPage + 1) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Siguiente ›",
                    color = if (state.historyPage < state.totalPages) Color.White else Hint,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/** Pantalla Detalle de entrega completada. */
@Composable
fun DeliveryDetailScreen(
    order: DeliveryOrder,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).statusBarsPadding().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(icon = R.drawable.ic_arrow_back, onClick = onBack)
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text("Pedido #${order.orderNumber}", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Detalle de entrega", color = Muted, fontSize = 12.sp)
            }
            Text(
                text = "Entregado",
                color = IconGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(TileGreen).padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailRow("Identificador", order.shortCode)
                    HorizontalDivider(color = DividerGray)
                    DetailRow("Restaurante", order.restaurantName)
                    HorizontalDivider(color = DividerGray)
                    DetailRow("Fecha y hora", order.formattedFullDate)
                    HorizontalDivider(color = DividerGray)
                    DetailRow("Dirección de entrega", order.deliveryAddress)
                    HorizontalDivider(color = DividerGray)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Estado final", color = Muted, fontSize = 12.sp)
                            Text("✓ Entregado", color = IconGreen, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Ganancia", color = Muted, fontSize = 12.sp)
                            Text(order.earningCop.toCop(), color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Text("Nota de entrega", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = Color.White) {
                Text(
                    text = order.deliveryNote.ifBlank { "Sin nota adjunta." },
                    color = Muted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Text("Imagen de respaldo", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE7DFD5)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛍️", fontSize = 48.sp)
                    Text(
                        text = if (order.proofImageUri != null) "Comprobante registrado" else "Sin imagen de respaldo",
                        color = InkSoft,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, color = Muted, fontSize = 12.sp)
        Text(value, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
    }
}

/** Pestaña Perfil del repartidor. */
@Composable
fun DeliveryProfileScreen(
    profile: UserProfile?,
    state: DeliveryUiState,
    onToggleGps: (Boolean) -> Unit,
    onOpenAffiliations: () -> Unit,
    onLogout: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).statusBarsPadding().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mi perfil", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            DeliveryGpsBadge(enabled = state.gpsEnabled, onClick = { onToggleGps(!state.gpsEnabled) })
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppCard(Modifier.fillMaxWidth(), cornerRadius = 18.dp, elevation = 1.dp) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(16.dp)).background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(profile?.initials ?: "R", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(Modifier.padding(start = 14.dp)) {
                        Text(profile?.fullName ?: "Repartidor", color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Repartidor · ${state.historyOrders.size} entregas", color = Muted, fontSize = 13.sp)
                    }
                }
            }

            Text("Datos personales", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            AppCard(Modifier.fillMaxWidth(), cornerRadius = 18.dp, elevation = 1.dp) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProfileKeyValue("Documento", "${profile?.documentType ?: "CC"} ${profile?.documentNumber?.ifBlank { "No registrado" } ?: "No registrado"}")
                    HorizontalDivider(color = DividerGray)
                    ProfileKeyValue("Transporte", profile?.vehicleType ?: "Moto")
                    HorizontalDivider(color = DividerGray)
                    ProfileKeyValue("Placa", profile?.vehiclePlate?.ifBlank { "N/A" } ?: "N/A")
                    HorizontalDivider(color = DividerGray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Número vinculado", color = Muted, fontSize = 12.sp)
                            Text(profile?.phone ?: "", color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Verificado",
                            color = IconGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(TileGreen).padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Text("Servicio GPS", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = if (state.gpsEnabled) TileGreen else TileRed
            ) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconTile(R.drawable.ic_location, Color.White, if (state.gpsEnabled) IconGreen else Danger, size = 40.dp)
                    Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                        Text(
                            text = if (state.gpsEnabled) "GPS activo" else "GPS apagado",
                            color = Ink,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (state.gpsEnabled) "Listo para tomar pedidos" else "No podrás tomar pedidos",
                            color = Muted,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = state.gpsEnabled,
                        onCheckedChange = onToggleGps,
                        colors = SwitchDefaults.colors(checkedTrackColor = IconGreen)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Locales afiliados", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                LinkText(
                    text = "${state.pendingInvitations.size} invitaciones",
                    onClick = onOpenAffiliations
                )
            }
            AppCard(Modifier.fillMaxWidth(), cornerRadius = 18.dp, elevation = 1.dp) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.acceptedAffiliations.forEachIndexed { idx, aff ->
                        if (idx > 0) HorizontalDivider(color = DividerGray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFD97706)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(aff.initial, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Column(Modifier.padding(start = 12.dp)) {
                                Text(aff.restaurantName, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(aff.restaurantAddress, color = Muted, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            AppCard(Modifier.fillMaxWidth(), onClick = onLogout) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconTile(R.drawable.ic_logout, Danger, TileRed)
                    Text("Cerrar sesión", color = Danger, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f).padding(start = 12.dp))
                    Icon(painterResource(R.drawable.ic_chevron_right), null, tint = Danger, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileKeyValue(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Muted, fontSize = 14.sp)
        Text(value, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}