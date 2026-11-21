package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main

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
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.cart.CartScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.home.HomeScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.profile.ProfileScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.search.SearchScreen

/** Pestañas de la barra inferior. */
enum class MainTab(@StringRes val label: Int, @DrawableRes val icon: Int) {
    HOME(R.string.nav_home, R.drawable.ic_home),
    SEARCH(R.string.nav_search, R.drawable.ic_search),
    CART(R.string.nav_cart, R.drawable.ic_shopping_bag),
    PROFILE(R.string.nav_profile, R.drawable.ic_person)
}

/**
 * Contenedor principal: barra inferior con Inicio, Buscar, Carrito (con globo de cantidad) y Perfil.
 * La pestaña actual vive en [tab] para poder cambiarla desde otras pantallas.
 */
@Composable
fun MainScreen(
    tab: MainTab,
    onTabChange: (MainTab) -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    cartViewModel: CartViewModel,
    sessionViewModel: SessionViewModel,
    ordersViewModel: OrdersViewModel,
    onOpenFilters: () -> Unit,
    onOpenRestaurant: (Int) -> Unit,
    onCheckout: () -> Unit,
    onOpenOrders: () -> Unit,
    onOpenOrderTracking: (String) -> Unit,
    onLogout: () -> Unit
) {
    // Estando en otra pestaña, "atrás" vuelve a Inicio antes de salir de la app
    BackHandler(enabled = tab != MainTab.HOME) { onTabChange(MainTab.HOME) }
    val cart by cartViewModel.state.collectAsStateWithLifecycle()
    val orders by ordersViewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = ScreenBg,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                MainTab.values().forEach { item ->
                    NavigationBarItem(
                        selected = tab == item,
                        onClick = { onTabChange(item) },
                        icon = {
                            if (item == MainTab.CART && cart.itemCount > 0) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = Primary, contentColor = Color.White) {
                                            Text(cart.itemCount.toString())
                                        }
                                    }
                                ) {
                                    Icon(painterResource(item.icon), contentDescription = null)
                                }
                            } else {
                                Icon(painterResource(item.icon), contentDescription = null)
                            }
                        },
                        label = { Text(stringResource(item.label), fontSize = 11.sp) },
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
            when (tab) {
                MainTab.HOME -> HomeScreen(
                    restaurantsViewModel = restaurantsViewModel,
                    sessionViewModel = sessionViewModel,
                    onSearchClick = { onTabChange(MainTab.SEARCH) },
                    onFiltersClick = onOpenFilters,
                    onRestaurantClick = onOpenRestaurant,
                    activeOrder = orders.active.firstOrNull(),
                    onActiveOrderClick = onOpenOrderTracking
                )
                MainTab.SEARCH -> SearchScreen(
                    restaurantsViewModel = restaurantsViewModel,
                    onFiltersClick = onOpenFilters,
                    onRestaurantClick = onOpenRestaurant
                )
                MainTab.CART -> CartScreen(
                    cartViewModel = cartViewModel,
                    onExplore = { onTabChange(MainTab.HOME) },
                    onCheckout = onCheckout
                )
                MainTab.PROFILE -> ProfileScreen(
                    sessionViewModel = sessionViewModel,
                    onOpenOrders = onOpenOrders,
                    onLogout = onLogout
                )
            }
        }
    }
}
