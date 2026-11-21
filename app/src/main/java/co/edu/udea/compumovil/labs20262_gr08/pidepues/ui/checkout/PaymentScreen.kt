package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.checkout

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
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.PaymentMethod
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.CartViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.CheckoutViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.RestaurantsViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.SessionViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop

/** Paso 2 del pedido: resumen, método de pago y confirmación (se crea el pedido en Firestore). */
@Composable
fun PaymentScreen(
    cartViewModel: CartViewModel,
    checkoutViewModel: CheckoutViewModel,
    restaurantsViewModel: RestaurantsViewModel,
    sessionViewModel: SessionViewModel,
    onBack: () -> Unit,
    onPlaced: (orderId: String) -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    val context = LocalContext.current
    val cart by cartViewModel.state.collectAsStateWithLifecycle()
    val checkout by checkoutViewModel.state.collectAsStateWithLifecycle()
    val restaurants by restaurantsViewModel.state.collectAsStateWithLifecycle()
    val profile by sessionViewModel.profile.collectAsStateWithLifecycle()
    val currentLocationText = stringResource(R.string.address_current_text)

    // Muestra los errores al crear el pedido una sola vez
    LaunchedEffect(checkout.messageRes) {
        val message = checkout.messageRes
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            checkoutViewModel.clearMessage()
        }
    }

    val phone = profile?.phone.orEmpty()

    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        AppTopBar(title = stringResource(R.string.payment_title), onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // ----- Resumen -----
            AppCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.payment_summary_title),
                        color = Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    cart.restaurant?.let {
                        Text(
                            text = it.emoji + " " + it.name,
                            color = Muted,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )
                    }
                    cart.lines.forEach { line ->
                        SummaryLine(
                            label = line.quantity.toString() + " × " + line.item.name,
                            value = line.total.toCop()
                        )
                    }
                    HorizontalDivider(color = DividerGray, modifier = Modifier.padding(vertical = 10.dp))
                    SummaryLine(stringResource(R.string.summary_subtotal), cart.subtotal.toCop())
                    SummaryLine(
                        stringResource(R.string.summary_delivery),
                        if (cart.deliveryFee == 0) stringResource(R.string.delivery_free) else cart.deliveryFee.toCop()
                    )
                    SummaryLine(stringResource(R.string.summary_service), cart.serviceFee.toCop())
                    HorizontalDivider(color = DividerGray, modifier = Modifier.padding(vertical = 10.dp))
                    SummaryLine(
                        label = stringResource(R.string.summary_total),
                        value = cart.total.toCop(),
                        emphasized = true
                    )
                }
            }

            // ----- Métodos de pago -----
            Text(
                text = stringResource(R.string.payment_choose),
                color = Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )
            PaymentOption(
                emoji = "💳",
                title = stringResource(R.string.pay_card_title),
                subtitle = stringResource(R.string.pay_card_subtitle),
                selected = checkout.payment == PaymentMethod.CARD,
                onClick = { checkoutViewModel.setPayment(PaymentMethod.CARD) }
            )
            PaymentOption(
                emoji = "📱",
                title = stringResource(R.string.pay_nequi_title),
                subtitle = stringResource(R.string.pay_nequi_subtitle, phone),
                selected = checkout.payment == PaymentMethod.NEQUI,
                onClick = { checkoutViewModel.setPayment(PaymentMethod.NEQUI) }
            )
            PaymentOption(
                emoji = "📲",
                title = stringResource(R.string.pay_daviplata_title),
                subtitle = stringResource(R.string.pay_daviplata_subtitle, phone),
                selected = checkout.payment == PaymentMethod.DAVIPLATA,
                onClick = { checkoutViewModel.setPayment(PaymentMethod.DAVIPLATA) }
            )
            PaymentOption(
                emoji = "💵",
                title = stringResource(R.string.pay_cash_title),
                subtitle = stringResource(R.string.pay_cash_subtitle),
                selected = checkout.payment == PaymentMethod.CASH,
                onClick = { checkoutViewModel.setPayment(PaymentMethod.CASH) }
            )

            Text(
                text = stringResource(R.string.payment_note),
                color = Muted,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Peach)
                    .border(1.dp, PeachStroke, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            )
        }

        BottomActionBar {
            PrimaryButton(
                text = stringResource(R.string.payment_confirm_format, cart.total.toCop()),
                enabled = !cart.isEmpty && profile != null && !checkout.isPlacing,
                onClick = {
                    val customer = profile
                    if (customer != null) {
                        checkoutViewModel.placeOrder(
                            customer = customer,
                            cart = cart,
                            device = restaurants.location,
                            currentLocationText = currentLocationText,
                            onPlaced = onPlaced
                        )
                    }
                }
            )
            if (checkout.isPlacing) {
                Box(Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            }
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String, emphasized: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
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

@Composable
private fun PaymentOption(
    emoji: String,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) TileOrange else Color.White)
            .border(1.dp, if (selected) Primary else OutlineGray, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EmojiTile(emoji = emoji, background = if (selected) Color.White else TileGray, size = 44.dp)
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(title, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
        }
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = Primary, unselectedColor = OutlineGray)
        )
    }
}
