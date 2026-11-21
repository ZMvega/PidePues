package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.cart

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
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.CartViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop

/** Carrito: platos, cantidades y resumen del pedido (la confirmación llega en la semana 8). */
@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onExplore: () -> Unit,
    onCheckout: () -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    val cart by cartViewModel.state.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp)
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.cart_screen_title),
                color = Ink,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            if (!cart.isEmpty) {
                LinkText(text = stringResource(R.string.cart_clear), onClick = { cartViewModel.clear() })
            }
        }

        if (cart.isEmpty) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(R.string.cart_emoji), fontSize = 64.sp)
                Text(
                    text = stringResource(R.string.cart_title),
                    color = Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = stringResource(R.string.cart_subtitle),
                    color = Muted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
                PrimaryButton(
                    text = stringResource(R.string.cart_explore),
                    onClick = onExplore,
                    fillWidth = false,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.cart_from, cart.restaurant?.name.orEmpty()),
                        color = Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    AppCard(Modifier.fillMaxWidth().padding(top = 12.dp)) {
                        Column(Modifier.padding(horizontal = 16.dp)) {
                            cart.lines.forEachIndexed { index, line ->
                                if (index > 0) HorizontalDivider(color = DividerGray)
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    EmojiTile(
                                        emoji = line.item.emoji,
                                        background = TileOrange,
                                        size = 48.dp,
                                        fontSize = 24.sp
                                    )
                                    Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                                        Text(line.item.name, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = stringResource(R.string.cart_unit_price, line.item.price.toCop()),
                                            color = Muted,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                        Text(
                                            text = line.total.toCop(),
                                            color = Primary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    QuantityStepper(
                                        quantity = line.quantity,
                                        onMinus = { cartViewModel.decrement(line.item.id) },
                                        onPlus = { cartViewModel.increment(line.item.id) }
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    AppCard(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            SummaryRow(stringResource(R.string.summary_subtotal), cart.subtotal.toCop())
                            SummaryRow(
                                label = stringResource(R.string.summary_delivery),
                                value = if (cart.deliveryFee == 0) stringResource(R.string.delivery_free) else cart.deliveryFee.toCop(),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            SummaryRow(
                                label = stringResource(R.string.summary_service),
                                value = cart.serviceFee.toCop(),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            HorizontalDivider(color = DividerGray, modifier = Modifier.padding(vertical = 12.dp))
                            SummaryRow(
                                label = stringResource(R.string.summary_total),
                                value = cart.total.toCop(),
                                emphasized = true
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp)
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                PrimaryButton(
                    text = stringResource(R.string.checkout_format, cart.total.toCop()),
                    onClick = onCheckout
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = if (emphasized) Ink else Muted,
            fontSize = if (emphasized) 16.sp else 14.sp,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = if (emphasized) Primary else Ink,
            fontSize = if (emphasized) 18.sp else 14.sp,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal
        )
    }
}
