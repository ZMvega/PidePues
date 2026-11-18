package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.detail

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
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.RestaurantRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.domain.AddResult
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuItem
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.CartViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.RestaurantsViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCopNumber
import java.util.Locale

/** Detalle de un restaurante: datos, menú (Firestore) y agregar platos al carrito. */
@Composable
fun RestaurantDetailScreen(
    restaurantId: Int,
    restaurantsViewModel: RestaurantsViewModel,
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onViewCart: () -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    // Se lee el estado para recomponer si cambian los restaurantes o la ubicación
    restaurantsViewModel.state.collectAsStateWithLifecycle()
    val cart by cartViewModel.state.collectAsStateWithLifecycle()
    val found = restaurantsViewModel.findById(restaurantId)

    if (found == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }
    val restaurant = found.restaurant

    var menu by remember(restaurantId) { mutableStateOf<List<MenuItem>>(emptyList()) }
    var isLoading by remember(restaurantId) { mutableStateOf(true) }
    var loadFailed by remember(restaurantId) { mutableStateOf(false) }
    var pendingReplace by remember { mutableStateOf<MenuItem?>(null) }

    LaunchedEffect(restaurantId) {
        isLoading = true
        RestaurantRepository.fetchMenu(restaurantId) { result ->
            val items = result.getOrNull()
            if (items != null) {
                menu = items
                loadFailed = false
            } else {
                loadFailed = true
            }
            isLoading = false
        }
    }

    Box(Modifier.fillMaxSize().background(ScreenBg)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 112.dp)
        ) {
            // "Foto" + tarjeta con los datos que sube sobre ella
            item {
                Box(Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(foodColor(restaurant.id)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = restaurant.emoji, fontSize = 96.sp)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 196.dp)
                            .background(ScreenBg, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 8.dp)
                    ) {
                        Text(restaurant.name, color = Ink, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = restaurant.cuisine,
                            color = Muted,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Row(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_star),
                                contentDescription = null,
                                tint = Gold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f", restaurant.rating),
                                color = Ink,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            Icon(
                                painter = painterResource(R.drawable.ic_schedule),
                                contentDescription = null,
                                tint = Muted,
                                modifier = Modifier.padding(start = 16.dp).size(14.dp)
                            )
                            Text(
                                text = stringResource(R.string.delivery_minutes, restaurant.deliveryMinutes),
                                color = Muted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                            Icon(
                                painter = painterResource(R.drawable.ic_location),
                                contentDescription = null,
                                tint = Muted,
                                modifier = Modifier.padding(start = 16.dp).size(14.dp)
                            )
                            Text(
                                text = stringResource(
                                    R.string.delivery_distance,
                                    String.format(Locale.US, "%.1f", found.distanceKm)
                                ),
                                color = Muted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Text(
                            text = if (restaurant.deliveryFee == 0) {
                                stringResource(R.string.delivery_free)
                            } else {
                                stringResource(R.string.delivery_fee, restaurant.deliveryFee.toCopNumber())
                            },
                            color = Muted,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        if (!restaurant.isOpen) {
                            Text(
                                text = stringResource(R.string.restaurant_closed),
                                color = Danger,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .border(1.dp, OutlineGray, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(R.string.menu_title),
                    color = Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 12.dp)
                )
            }

            if (isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
            } else if (loadFailed || menu.isEmpty()) {
                item {
                    Text(
                        text = stringResource(if (loadFailed) R.string.error_loading_menu else R.string.menu_empty),
                        color = Muted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                }
            } else {
                items(menu, key = { it.id }) { dish ->
                    MenuRow(
                        dish = dish,
                        quantity = cart.quantityOf(dish.id),
                        onAdd = {
                            if (cartViewModel.add(restaurant, dish) == AddResult.DIFFERENT_RESTAURANT) {
                                pendingReplace = dish
                            }
                        },
                        onPlus = { cartViewModel.increment(dish.id) },
                        onMinus = { cartViewModel.decrement(dish.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // Botón atrás fijo arriba
        CircleIconButton(
            icon = R.drawable.ic_arrow_back,
            onClick = onBack,
            containerColor = Color.White,
            modifier = Modifier.statusBarsPadding().padding(16.dp),
            contentDescription = stringResource(R.string.cd_back)
        )

        // Barra inferior: ver carrito (solo si hay platos)
        if (!cart.isEmpty) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .shadow(8.dp)
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                PrimaryButton(
                    text = stringResource(R.string.view_cart_format, cart.itemCount, cart.total.toCop()),
                    onClick = onViewCart
                )
            }
        }
    }

    // El carrito es de otro restaurante: ¿empezar uno nuevo?
    val replacing = pendingReplace
    if (replacing != null) {
        AlertDialog(
            onDismissRequest = { pendingReplace = null },
            title = { Text(stringResource(R.string.cart_replace_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.cart_replace_message,
                        cart.restaurant?.name.orEmpty(),
                        restaurant.name
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        cartViewModel.replaceWith(restaurant, replacing)
                        pendingReplace = null
                    }
                ) { Text(stringResource(R.string.cart_replace_confirm), color = Primary) }
            },
            dismissButton = {
                TextButton(onClick = { pendingReplace = null }) {
                    Text(stringResource(R.string.cancel), color = Muted)
                }
            }
        )
    }
}

@Composable
private fun MenuRow(
    dish: MenuItem,
    quantity: Int,
    onAdd: () -> Unit,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier.fillMaxWidth(), cornerRadius = 16.dp, elevation = 1.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmojiTile(emoji = dish.emoji, background = TileOrange, size = 56.dp, fontSize = 28.sp)
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(dish.name, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = dish.description,
                    color = Muted,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = dish.price.toCop(),
                    color = Primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            // Cantidad 0: botón "+". Cantidad > 0: selector  −  1  +
            if (quantity == 0) {
                CircleIconButton(
                    icon = R.drawable.ic_add,
                    onClick = onAdd,
                    containerColor = Primary,
                    tint = Color.White,
                    size = 36.dp,
                    iconPadding = 8.dp,
                    contentDescription = stringResource(R.string.cd_add)
                )
            } else {
                QuantityStepper(quantity = quantity, onMinus = onMinus, onPlus = onPlus)
            }
        }
    }
}
