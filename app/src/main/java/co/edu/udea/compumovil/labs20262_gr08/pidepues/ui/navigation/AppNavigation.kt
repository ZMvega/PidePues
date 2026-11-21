package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.navigation

import android.content.pm.ApplicationInfo
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import android.widget.Toast
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.ReviewRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.rating.RateOrderScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.formatOrderDate
import kotlinx.coroutines.launch
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.AuthRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserRole
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.auth.LoginScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.auth.RegisterScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.checkout.DeliveryAddressScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.checkout.PaymentScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.detail.RestaurantDetailScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.filters.FiltersScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.location.LocationPermissionScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.AuthViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.CartViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.CheckoutViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.MainScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.MainTab
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.OrdersViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.RestaurantsViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.SessionViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.orders.OrderConfirmationScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.orders.OrderTrackingScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.orders.OrdersScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role.RoleHomeScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.splash.SplashScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.detail.RestaurantProfileScreen

/** Rutas de la app (una sola Activity con un NavHost). */
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val LOCATION = "location"
    const val MAIN = "main"
    const val FILTERS = "filters"
    const val RESTAURANT = "restaurant/{id}"
    const val ROLE = "role/{role}"

    // Compra y pedidos (semana 8)
    const val ADDRESS = "checkout/address"
    const val PAYMENT = "checkout/payment"
    const val CONFIRMATION = "order/confirmation/{id}"
    const val TRACKING = "order/tracking/{id}"
    const val ORDERS = "orders"
    const val RATE = "order/rate/{id}"
    const val RESTAURANT_MENU = "restaurant/{id}/menu"
    fun restaurantMenu(id: Int): String = "restaurant/$id/menu"
    fun rate(orderId: String): String = "order/rate/$orderId"

    fun restaurant(id: Int): String = "restaurant/" + id
    fun role(role: UserRole): String = "role/" + role.key
    fun confirmation(orderId: String): String = "order/confirmation/" + orderId
    fun tracking(orderId: String): String = "order/tracking/" + orderId
}

private fun isDebuggable(context: android.content.Context): Boolean =
    (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val isDebug = remember { isDebuggable(context) }

    // ViewModels compartidos por toda la app (ámbito de la Activity)
    val restaurantsViewModel: RestaurantsViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val sessionViewModel: SessionViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val ordersViewModel: OrdersViewModel = viewModel()
    val checkoutViewModel: CheckoutViewModel = viewModel()

    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }

    val ordersState by ordersViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(ordersState.orders) {
        restaurantsViewModel.updateOrderHistory(ordersState.orders)
    }

    /** Navega a [route] y vacía la pila (no se vuelve atrás al login/splash). */
    fun goClearingBackStack(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    /** Después de iniciar sesión o registrarse: destino según el rol. */
    fun goAfterAuth(profile: UserProfile) {
        sessionViewModel.set(profile)
        if (profile.role == UserRole.CLIENT) {
            // En depuración, si Firestore está vacío se cargan los datos de ejemplo
            restaurantsViewModel.loadRestaurants(seed = isDebug)
            // Pedidos (tiempo real) y direcciones guardadas del cliente
            ordersViewModel.start(profile.uid)
            checkoutViewModel.start(profile.uid)
            selectedTab = MainTab.HOME
            goClearingBackStack(Routes.LOCATION)
        } else {
            goClearingBackStack(Routes.role(profile.role))
        }
    }

    fun logout() {
        // Primero se cancelan los oyentes de Firestore, luego se cierra la sesión
        ordersViewModel.stop()
        checkoutViewModel.stop()
        AuthRepository.logout()
        cartViewModel.clear()
        sessionViewModel.clear()
        goClearingBackStack(Routes.LOGIN)
    }

    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onFinished = { profile ->
                    if (profile != null) goAfterAuth(profile) else goClearingBackStack(Routes.LOGIN)
                }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoggedIn = { goAfterAuth(it) },
                onRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() },
                onRegistered = { goAfterAuth(it) }
            )
        }
        composable(Routes.LOCATION) {
            LocationPermissionScreen(
                onDone = { useDeviceLocation ->
                    if (useDeviceLocation) restaurantsViewModel.loadDeviceLocation(context)
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOCATION) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MAIN) {
            MainScreen(
                tab = selectedTab,
                onTabChange = { selectedTab = it },
                restaurantsViewModel = restaurantsViewModel,
                cartViewModel = cartViewModel,
                sessionViewModel = sessionViewModel,
                ordersViewModel = ordersViewModel,
                onOpenFilters = { navController.navigate(Routes.FILTERS) },
                onOpenRestaurant = { id -> navController.navigate(Routes.restaurant(id)) },
                onCheckout = { navController.navigate(Routes.ADDRESS) },
                onOpenOrders = { navController.navigate(Routes.ORDERS) },
                onOpenOrderTracking = { orderId -> navController.navigate(Routes.tracking(orderId)) },
                onLogout = { logout() }
            )
        }
        composable(Routes.FILTERS) {
            FiltersScreen(
                restaurantsViewModel = restaurantsViewModel,
                onClose = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.RESTAURANT,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("id") ?: 0
            RestaurantProfileScreen(
                restaurantId = id,
                restaurantsViewModel = restaurantsViewModel,
                onBack = { navController.popBackStack() },
                onViewMenu = { navController.navigate(Routes.restaurantMenu(id)) }
            )
        }
        composable(
            route = Routes.RESTAURANT_MENU,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            RestaurantDetailScreen(
                restaurantId = entry.arguments?.getInt("id") ?: 0,
                restaurantsViewModel = restaurantsViewModel,
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() },
                onViewCart = {
                    selectedTab = MainTab.CART
                    navController.popBackStack(Routes.MAIN, false)
                }
            )
        }
        composable(
            route = Routes.ROLE,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { entry ->
            RoleHomeScreen(
                role = UserRole.fromKey(entry.arguments?.getString("role")),
                sessionViewModel = sessionViewModel,
                onLogout = { logout() }
            )
        }

        // ----- Compra: dirección → pago → confirmación → seguimiento -----
        composable(Routes.ADDRESS) {
            DeliveryAddressScreen(
                checkoutViewModel = checkoutViewModel,
                restaurantsViewModel = restaurantsViewModel,
                sessionViewModel = sessionViewModel,
                onBack = { navController.popBackStack() },
                onConfirm = { navController.navigate(Routes.PAYMENT) }
            )
        }
        composable(Routes.PAYMENT) {
            PaymentScreen(
                cartViewModel = cartViewModel,
                checkoutViewModel = checkoutViewModel,
                restaurantsViewModel = restaurantsViewModel,
                sessionViewModel = sessionViewModel,
                onBack = { navController.popBackStack() },
                onPlaced = { orderId ->
                    cartViewModel.clear()
                    // Se quitan dirección y pago de la pila: "atrás" desde la confirmación vuelve a la app
                    navController.navigate(Routes.confirmation(orderId)) {
                        popUpTo(Routes.MAIN)
                    }
                }
            )
        }
        composable(
            route = Routes.CONFIRMATION,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val orderId = entry.arguments?.getString("id").orEmpty()
            OrderConfirmationScreen(
                orderId = orderId,
                ordersViewModel = ordersViewModel,
                onTrack = {
                    navController.navigate(Routes.tracking(orderId)) { popUpTo(Routes.MAIN) }
                },
                onHome = {
                    selectedTab = MainTab.HOME
                    navController.popBackStack(Routes.MAIN, false)
                }
            )
        }
        composable(
            route = Routes.TRACKING,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val orderId = entry.arguments?.getString("id").orEmpty()
            OrderTrackingScreen(
                orderId = orderId,
                ordersViewModel = ordersViewModel,
                isDebug = isDebug,
                onBack = { navController.popBackStack() },
                onRate = { navController.navigate(Routes.rate(orderId)) }
            )
        }
        composable(
            route = Routes.RATE,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val orderId = entry.arguments?.getString("id").orEmpty()
            val ordersState by ordersViewModel.state.collectAsStateWithLifecycle()
            val profile by sessionViewModel.profile.collectAsStateWithLifecycle()
            val order = ordersState.find(orderId)
            val scope = rememberCoroutineScope()
            val reviewRepo = remember { ReviewRepository() }
            var isSubmitting by remember { mutableStateOf(false) }

            if (order != null) {
                RateOrderScreen(
                    restaurantName = order.restaurantName,
                    restaurantEmoji = order.restaurantEmoji,
                    orderDateLabel = formatOrderDate(order.createdAtMillis),
                    orderShortCode = order.shortCode,
                    isSubmitting = isSubmitting,
                    onBack = { navController.popBackStack() },
                    onSubmit = { stars, comment ->
                        scope.launch {
                            isSubmitting = true
                            val result = reviewRepo.submitReview(
                                orderId = order.id,
                                orderShortCode = order.shortCode,
                                restaurantId = order.restaurantId.toString(),
                                restaurantName = order.restaurantName,
                                customerName = profile?.fullName.orEmpty(),
                                stars = stars,
                                comment = comment
                            )
                            isSubmitting = false
                            if (result.isSuccess) {
                                Toast.makeText(context, "¡Gracias por tu calificación!", Toast.LENGTH_SHORT).show()
                                restaurantsViewModel.loadRestaurants()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "No pudimos enviar tu calificación", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }
        }
        composable(Routes.ORDERS) {
            OrdersScreen(
                ordersViewModel = ordersViewModel,
                onBack = { navController.popBackStack() },
                onOpenOrder = { orderId -> navController.navigate(Routes.tracking(orderId)) }
            )
        }
    }
}
