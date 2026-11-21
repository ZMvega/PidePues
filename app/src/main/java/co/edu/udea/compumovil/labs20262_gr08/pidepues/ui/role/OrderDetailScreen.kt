package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
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
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.PrimaryButton
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.StatusBarAppearance
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop

/**
 * Pantalla "Detalles del pedido" para el rol Restaurante.
 * Muestra el estado operativo en tiempo real, stepper de 5 pasos, productos solicitados,
 * nota del cliente, desglose de totales, datos del cliente y del repartidor.
 */
@Composable
fun OrderDetailScreen(
    order: RestaurantOrder,
    onBack: () -> Unit,
    onAcceptOrder: () -> Unit,
    onAdvanceToPrepared: () -> Unit,
    onAdvanceToEnCamino: () -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarAppearance(darkIcons = true)
    BackHandler(onBack = onBack)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ScreenBg,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            OrderDetailHeader(order = order, onBack = onBack)
        },
        bottomBar = {
            OrderDetailBottomBar(
                order = order,
                onAcceptOrder = onAcceptOrder,
                onAdvanceToPrepared = onAdvanceToPrepared,
                onAdvanceToEnCamino = onAdvanceToEnCamino
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            OrderStatusStepper(currentStepIndex = order.stepIndex)
            RequestedProductsSection(order = order)
            CustomerDataSection(order = order)
            CourierSection(order = order)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun OrderDetailHeader(
    order: RestaurantOrder,
    onBack: () -> Unit
) {
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
                text = stringResource(R.string.orders_number_prefix, order.orderNumber),
                color = Ink,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = order.formattedDateTime,
                color = Muted,
                fontSize = 13.sp
            )
        }

        val (bgColor, textColor) = when (order.status) {
            OrderStatus.SOLICITADO -> TileOrange to Primary
            OrderStatus.TOMADO -> Color(0xFFFEF3C7) to Color(0xFF92400E)
            OrderStatus.PREPARADO -> Color(0xFFEDE9FE) to Color(0xFF6D28D9)
            OrderStatus.EN_CAMINO -> Color(0xFFDBEAFE) to Color(0xFF1D4ED8)
            OrderStatus.ENTREGADO -> Color(0xFFDCFCE7) to Color(0xFF15803D)
        }

        Surface(
            shape = RoundedCornerShape(50),
            color = bgColor
        ) {
            Text(
                text = stringResource(order.status.labelRes()),
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
            )
        }
    }
}

@Composable
private fun OrderStatusStepper(
    currentStepIndex: Int,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        1 to R.string.orders_status_solicitado,
        2 to R.string.orders_status_tomado,
        3 to R.string.orders_status_preparado,
        4 to R.string.orders_status_en_camino,
        5 to R.string.orders_status_entregado
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        steps.forEachIndexed { index, (stepNum, labelRes) ->
            val isActive = stepNum <= currentStepIndex

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (index > 0) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 2.dp,
                            color = if (stepNum <= currentStepIndex) Primary else OutlineGray
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isActive) Primary else CircleGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stepNum.toString(),
                            color = if (isActive) Color.White else Hint,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (index < steps.size - 1) {
                        val nextStepNum = steps[index + 1].first
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 2.dp,
                            color = if (nextStepNum <= currentStepIndex) Primary else OutlineGray
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = stringResource(labelRes),
                    color = if (isActive) Ink else Hint,
                    fontSize = 10.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RequestedProductsSection(
    order: RestaurantOrder
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.order_detail_requested_products),
            color = Ink,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, OutlineGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Lista de items
                order.items.forEachIndexed { idx, item ->
                    if (idx > 0) Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TileOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.quantity.toString(),
                                color = Primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = item.name,
                            color = Ink,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = item.total.toCop(),
                            color = Ink,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Nota del cliente condicional
                if (order.customerNote.isNotBlank()) {
                    Spacer(Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.order_detail_customer_note_prefix, order.customerNote),
                            color = Color(0xFF92400E),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Línea divisoria punteada
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                ) {
                    drawLine(
                        color = OutlineGray,
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Filas de resumen de precios
                SummaryRow(
                    label = stringResource(R.string.summary_subtotal),
                    value = order.subtotalCop.toCop(),
                    isTotal = false
                )
                Spacer(Modifier.height(6.dp))
                SummaryRow(
                    label = stringResource(R.string.summary_delivery),
                    value = order.deliveryFeeCop.toCop(),
                    isTotal = false
                )
                Spacer(Modifier.height(8.dp))
                SummaryRow(
                    label = stringResource(R.string.summary_total),
                    value = order.totalCop.toCop(),
                    isTotal = true
                )

                Spacer(Modifier.height(12.dp))

                // Método de pago
                val paymentText = order.paymentMethod.ifBlank { stringResource(R.string.order_detail_payment_not_specified) }
                Text(
                    text = stringResource(R.string.order_detail_payment_prefix, paymentText),
                    color = Hint,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isTotal: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = if (isTotal) Ink else Muted,
            fontSize = if (isTotal) 16.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            color = Ink,
            fontSize = if (isTotal) 18.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun CustomerDataSection(
    order: RestaurantOrder
) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.order_detail_customer_data),
            color = Ink,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(1.dp, OutlineGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = order.customerInitial,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = order.customerName.ifBlank { stringResource(R.string.order_detail_customer_fallback) },
                            color = Ink,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (order.customerPhone.isNotBlank()) {
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = order.customerPhone,
                                color = Muted,
                                fontSize = 13.sp
                            )
                        }
                    }

                    if (order.customerPhone.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val phone = order.customerPhone.trim()
                                if (phone.isBlank()) {
                                    Toast.makeText(context, R.string.error_phone_empty, Toast.LENGTH_SHORT).show()
                                    return@IconButton
                                }
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                runCatching {
                                    context.startActivity(intent)
                                }.onFailure {
                                    Toast.makeText(context, R.string.error_call_unavailable, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7))
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_phone),
                                contentDescription = stringResource(R.string.label_phone),
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if (order.customerAddress.isNotBlank()) {
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_location),
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = order.customerAddress,
                            color = InkSoft,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CourierSection(
    order: RestaurantOrder
) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.order_detail_courier),
            color = Ink,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        if (order.courierName.isBlank()) {
            val textRes = when (order.status) {
                OrderStatus.SOLICITADO, OrderStatus.TOMADO -> R.string.order_detail_courier_info_solicitado
                OrderStatus.PREPARADO -> R.string.order_detail_courier_info_preparado
                else -> R.string.order_detail_courier_unassigned
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = ScreenBg
            ) {
                Text(
                    text = stringResource(textRes),
                    color = Muted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = BorderStroke(1.dp, OutlineGray)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3B82F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_person),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = order.courierName,
                            color = Ink,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (order.courierPhone.isNotBlank()) {
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = order.courierPhone,
                                color = Muted,
                                fontSize = 13.sp
                            )
                        }
                    }

                    if (order.courierPhone.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val phone = order.courierPhone.trim()
                                if (phone.isBlank()) {
                                    Toast.makeText(context, R.string.error_phone_empty, Toast.LENGTH_SHORT).show()
                                    return@IconButton
                                }
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                runCatching {
                                    context.startActivity(intent)
                                }.onFailure {
                                    Toast.makeText(context, R.string.error_call_unavailable, Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7))
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_phone),
                                contentDescription = stringResource(R.string.label_phone),
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderDetailBottomBar(
    order: RestaurantOrder,
    onAcceptOrder: () -> Unit,
    onAdvanceToPrepared: () -> Unit,
    onAdvanceToEnCamino: () -> Unit
) {
    val (buttonTextRes, action) = when (order.status) {
        OrderStatus.SOLICITADO -> R.string.orders_action_accept to onAcceptOrder
        OrderStatus.TOMADO -> R.string.orders_action_mark_prepared to onAdvanceToPrepared
        OrderStatus.PREPARADO -> R.string.orders_action_mark_en_camino to onAdvanceToEnCamino
        OrderStatus.EN_CAMINO, OrderStatus.ENTREGADO -> null to null
    }

    if (buttonTextRes != null && action != null) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                PrimaryButton(
                    text = stringResource(buttonTextRes),
                    onClick = action,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
