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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.DeliveryAddress
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.CheckoutViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.RestaurantsViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.SessionViewModel

/** Paso 1 del pedido: elegir dónde se entrega (ubicación actual o una dirección guardada). */
@Composable
fun DeliveryAddressScreen(
    checkoutViewModel: CheckoutViewModel,
    restaurantsViewModel: RestaurantsViewModel,
    sessionViewModel: SessionViewModel,
    onBack: () -> Unit,
    onConfirm: () -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    val checkout by checkoutViewModel.state.collectAsStateWithLifecycle()
    val restaurants by restaurantsViewModel.state.collectAsStateWithLifecycle()
    val profile by sessionViewModel.profile.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        AppTopBar(title = stringResource(R.string.address_title), onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            MapCard(height = 160.dp) {
                MapPin(Modifier.align(Alignment.Center))
            }

            // ----- Ubicación actual -----
            Text(
                text = stringResource(R.string.address_current_title),
                color = Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )
            AddressOption(
                emoji = "📍",
                title = stringResource(R.string.address_current_title),
                subtitle = stringResource(
                    if (restaurants.location.isFromDevice) R.string.address_current_device
                    else R.string.address_current_default
                ),
                selected = checkout.useCurrentLocation,
                onClick = { checkoutViewModel.selectCurrentLocation() }
            )

            // ----- Direcciones guardadas -----
            Text(
                text = stringResource(R.string.address_saved_title),
                color = Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
            )
            if (checkout.addresses.isEmpty()) {
                Text(
                    text = stringResource(R.string.address_saved_empty),
                    color = Muted,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            checkout.addresses.forEach { address ->
                AddressOption(
                    emoji = address.emoji,
                    title = address.label,
                    subtitle = address.text,
                    selected = !checkout.useCurrentLocation && checkout.selectedAddressId == address.id,
                    onClick = { checkoutViewModel.selectSaved(address.id) },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Primary, RoundedCornerShape(14.dp))
                    .clickable { showAddDialog = true }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = stringResource(R.string.address_add),
                    color = Primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // ----- Instrucciones -----
            Row(
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.address_instructions_title),
                    color = Ink,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.address_optional),
                    color = Muted,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DividerGray)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            AppTextField(
                label = "",
                value = checkout.instructions,
                onValueChange = { checkoutViewModel.setInstructions(it) },
                placeholder = stringResource(R.string.address_instructions_hint),
                singleLine = false,
                minLines = 3
            )
        }

        BottomActionBar {
            PrimaryButton(
                text = stringResource(R.string.address_confirm),
                onClick = onConfirm
            )
        }
    }

    if (showAddDialog) {
        AddAddressDialog(
            onDismiss = { showAddDialog = false },
            onSave = { label, text ->
                val uid = profile?.uid
                if (uid != null) {
                    checkoutViewModel.addAddress(uid, label, emojiForLabel(label), text)
                }
                showAddDialog = false
            }
        )
    }
}

/** Casa → 🏠, Oficina/Trabajo → 🏢, otro → 📍. */
private fun emojiForLabel(label: String): String {
    val l = label.lowercase()
    return when {
        l.contains("casa") || l.contains("hogar") -> "🏠"
        l.contains("oficina") || l.contains("trabajo") -> "🏢"
        else -> "📍"
    }
}

@Composable
private fun AddressOption(
    emoji: String,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
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

@Composable
private fun AddAddressDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var label by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.address_add_title)) },
        text = {
            Column {
                AppTextField(
                    label = stringResource(R.string.address_name_label),
                    value = label,
                    onValueChange = { label = it },
                    placeholder = stringResource(R.string.address_name_hint),
                    capitalization = KeyboardCapitalization.Words
                )
                AppTextField(
                    label = stringResource(R.string.address_text_label),
                    value = text,
                    onValueChange = { text = it },
                    placeholder = stringResource(R.string.address_text_hint),
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                    modifier = Modifier.padding(top = 12.dp)
                )
                if (showError) {
                    Text(
                        text = stringResource(R.string.address_required),
                        color = Danger,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (label.isBlank() || text.isBlank()) showError = true
                    else onSave(label.trim(), text.trim())
                }
            ) { Text(stringResource(R.string.address_save), color = Primary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel), color = Muted) }
        }
    )
}
