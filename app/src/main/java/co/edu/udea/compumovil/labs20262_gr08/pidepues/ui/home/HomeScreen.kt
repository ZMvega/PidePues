package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.home

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
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Order
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.SortOption
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.emoji
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.infoRes
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.labelRes
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.listTitleRes
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.RestaurantsViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.SessionViewModel

@Composable
fun HomeScreen(
    restaurantsViewModel: RestaurantsViewModel,
    sessionViewModel: SessionViewModel,
    onSearchClick: () -> Unit,
    onFiltersClick: () -> Unit,
    onRestaurantClick: (Int) -> Unit,
    activeOrder: Order?,
    onActiveOrderClick: (String) -> Unit
) {
    StatusBarAppearance(darkIcons = false)
    val state by restaurantsViewModel.state.collectAsStateWithLifecycle()
    val profile by sessionViewModel.profile.collectAsStateWithLifecycle()

    val greeting = profile?.let { stringResource(R.string.home_greeting_name, it.firstName) }
        ?: stringResource(R.string.home_greeting_default)
    val locationLabel = stringResource(
        if (state.location.isFromDevice) R.string.location_device_label else R.string.location_demo_label
    )
    val hasItems = state.nearby.isNotEmpty()
    val isRetry = state.loadError && !hasItems

    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        HomeHeader(
            greeting = greeting,
            locationLabel = locationLabel,
            onSearchClick = onSearchClick,
            onFiltersClick = onFiltersClick
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (activeOrder != null) {
                item {
                    ActiveOrderBanner(
                        order = activeOrder,
                        onClick = { onActiveOrderClick(activeOrder.id) },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
            item { PromoBanner(onApply = { restaurantsViewModel.applyTopRatedPromo() }) }
            item {
                SortChips(
                    selected = state.filters.sort,
                    onSelect = { restaurantsViewModel.setSort(it) }
                )
            }
            item {
                Text(
                    text = stringResource(state.filters.sort.infoRes),
                    color = InkSoft,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, OutlineGray, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(state.filters.sort.listTitleRes),
                        color = Ink,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    LinkText(text = stringResource(R.string.see_all), onClick = onSearchClick)
                }
            }

            if (state.isLoading && !hasItems) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
            } else if (!hasItems) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(
                                if (isRetry) R.string.error_loading_restaurants else R.string.empty_results
                            ),
                            color = Muted,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        LinkText(
                            text = stringResource(if (isRetry) R.string.retry else R.string.empty_clear),
                            onClick = {
                                if (isRetry) restaurantsViewModel.loadRestaurants()
                                else restaurantsViewModel.clearFilters()
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                items(state.nearby, key = { it.restaurant.id }) { item ->
                    RestaurantCard(
                        item = item,
                        onClick = { onRestaurantClick(item.restaurant.id) },
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}

/** Cabecera naranja fija: saludo, ubicación, campana y barra de búsqueda. */
@Composable
private fun HomeHeader(
    greeting: String,
    locationLabel: String,
    onSearchClick: () -> Unit,
    onFiltersClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Brush.verticalGradient(listOf(HeaderStart, HeaderEnd)))
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(text = greeting, color = White80, fontSize = 13.sp)
                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_location),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = locationLabel,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_expand_more),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            // Campana con punto de notificación
            Box(Modifier.size(44.dp)) {
                Box(
                    modifier = Modifier.fillMaxSize().clip(CircleShape).background(White20),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_notifications),
                        contentDescription = stringResource(R.string.cd_notifications),
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = 8.dp)
                        .clip(CircleShape)
                        .background(GoldDot)
                        .border(2.dp, HeaderStart, CircleShape)
                )
            }
        }

        // Barra de búsqueda: al tocarla se abre la pestaña Buscar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable(onClick = onSearchClick)
                .padding(start = 14.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = Hint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(R.string.home_search_hint),
                color = Hint,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f).padding(start = 10.dp)
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(TileOrange)
                    .clickable(onClick = onFiltersClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_tune),
                    contentDescription = stringResource(R.string.cd_filters),
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/** Banner "Envío gratis hoy": su botón filtra a restaurantes con 4.5+. */
@Composable
private fun PromoBanner(onApply: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(PromoStart, PromoEnd)))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.promo_title),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.promo_subtitle),
                color = White80,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Text(
            text = stringResource(R.string.promo_apply),
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(White20)
                .clickable(onClick = onApply)
                .padding(horizontal = 18.dp, vertical = 10.dp)
        )
    }
}

/** Chips: Todos (recomendados), Cercanos, Mejor nota, Rápido. */
@Composable
private fun SortChips(selected: SortOption, onSelect: (SortOption) -> Unit) {
    val options = listOf(
        SortOption.RECOMMENDED to R.string.chip_all,
        SortOption.NEAREST to R.string.chip_nearby,
        SortOption.BEST_RATED to R.string.chip_best,
        SortOption.FASTEST to R.string.chip_fast
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { (option, label) ->
            PidePuesChip(
                text = stringResource(label),
                selected = option == selected,
                onClick = { onSelect(option) }
            )
        }
    }
}

/** Aviso en Inicio cuando hay un pedido en curso: lleva al seguimiento. */
@Composable
private fun ActiveOrderBanner(order: Order, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Peach)
            .border(1.dp, PeachStroke, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EmojiTile(emoji = order.status.emoji, background = Color.White, size = 44.dp)
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(
                text = stringResource(R.string.home_active_order_title),
                color = Ink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(order.status.labelRes) + " · " + order.restaurantName,
                color = Muted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        LinkText(text = stringResource(R.string.home_active_order_cta), onClick = onClick)
    }
}

