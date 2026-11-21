package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.RestaurantRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.domain.AddResult
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuCategory
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuItem
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.CartViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.RestaurantsViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop

/** Pantalla 10: Menú del restaurante con pestañas de categorías y acceso directo al carrito. */
@Composable
fun RestaurantDetailScreen(
    restaurantId: Int,
    restaurantsViewModel: RestaurantsViewModel,
    cartViewModel: CartViewModel,
    onBack: () -> Unit,
    onViewCart: () -> Unit
) {
    StatusBarAppearance(darkIcons = true)
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
    var allProductsUnavailable by remember(restaurantId) { mutableStateOf(false) }
    var pendingReplace by remember { mutableStateOf<MenuItem?>(null) }
    var selectedCategoryKey by rememberSaveable(restaurantId) { mutableStateOf<String?>(null) }

    DisposableEffect(restaurantId) {
        isLoading = true
        val registration = RestaurantRepository.listenMenu(restaurantId.toString()) { result ->
            val items = result.getOrNull()
            if (items != null) {
                menu = items.filter { it.isAvailable }
                allProductsUnavailable = items.isNotEmpty() && menu.isEmpty()
                loadFailed = false
            } else {
                loadFailed = true
            }
            isLoading = false
        }
        onDispose { registration.remove() }
    }

    val filteredMenu = remember(menu, selectedCategoryKey) {
        if (selectedCategoryKey == null) menu
        else menu.filter { it.category.key == selectedCategoryKey }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        // ----- Barra superior: atrás | nombre + contador | botón carrito -----
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp)
                .background(Color.White)
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                CircleIconButton(
                    icon = R.drawable.ic_arrow_back,
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart),
                    contentDescription = stringResource(R.string.cd_back)
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 52.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = restaurant.name,
                        color = Ink,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${menu.size} platillos",
                        color = Muted,
                        fontSize = 12.sp
                    )
                }

                // Botón carrito con badge de cantidad
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(TileOrange)
                            .clickable(onClick = onViewCart),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_shopping_bag),
                            contentDescription = stringResource(R.string.nav_cart),
                            tint = Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (cart.itemCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cart.itemCount.toString(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ----- Pestañas horizontales de categorías -----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PidePuesChip(
                    text = "Todos",
                    selected = selectedCategoryKey == null,
                    onClick = { selectedCategoryKey = null }
                )
                MenuCategory.entries.forEach { category ->
                    PidePuesChip(
                        text = category.label,
                        selected = selectedCategoryKey == category.key,
                        onClick = { selectedCategoryKey = category.key }
                    )
                }
            }
        }

        // ----- Lista de platillos -----
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
                loadFailed || menu.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(
                                when {
                                    loadFailed -> R.string.error_loading_menu
                                    allProductsUnavailable -> R.string.menu_all_products_unavailable
                                    else -> R.string.menu_empty
                                }
                            ),
                            color = Muted,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                filteredMenu.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay platillos en esta categoría",
                            color = Muted,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(filteredMenu, key = { _, dish -> dish.id }) { index, dish ->
                            MenuDishCard(
                                dish = dish,
                                isPopular = index == 0,
                                quantity = cart.quantityOf(dish.id),
                                onAdd = {
                                    if (cartViewModel.add(restaurant, dish) == AddResult.DIFFERENT_RESTAURANT) {
                                        pendingReplace = dish
                                    }
                                },
                                onPlus = { cartViewModel.increment(dish.id) },
                                onMinus = { cartViewModel.decrement(dish.id) }
                            )
                        }
                    }
                }
            }
        }

        // ----- Barra inferior fija: Ver carrito -----
        if (!cart.isEmpty) {
            BottomActionBar {
                PrimaryButton(
                    text = stringResource(R.string.view_cart_format, cart.itemCount, cart.total.toCop()),
                    onClick = onViewCart
                )
            }
        }
    }

    // Diálogo si el carrito ya tiene platos de otro restaurante
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
private fun MenuDishCard(
    dish: MenuItem,
    isPopular: Boolean,
    quantity: Int,
    onAdd: () -> Unit,
    onPlus: () -> Unit,
    onMinus: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 18.dp,
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen/emoji cuadrado con etiqueta "Popular"
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(TileOrange),
                contentAlignment = Alignment.Center
            ) {
                Text(text = dish.emoji, fontSize = 38.sp)
                if (isPopular) {
                    Text(
                        text = "Popular",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Gold)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = dish.name,
                    color = Ink,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dish.description,
                    color = Muted,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = dish.price.toCop(),
                        color = Primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (quantity == 0) {
                        CircleIconButton(
                            icon = R.drawable.ic_add,
                            onClick = onAdd,
                            containerColor = Primary,
                            tint = Color.White,
                            size = 34.dp,
                            iconPadding = 8.dp,
                            contentDescription = stringResource(R.string.cd_add)
                        )
                    } else {
                        QuantityStepper(
                            quantity = quantity,
                            onMinus = onMinus,
                            onPlus = onPlus
                        )
                    }
                }
            }
        }
    }
}