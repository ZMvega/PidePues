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
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.AuthRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserRole
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.auth.LoginScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.auth.RegisterScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.detail.RestaurantDetailScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.filters.FiltersScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.location.LocationPermissionScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.AuthViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.CartViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.MainScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.MainTab
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.RestaurantsViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.SessionViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role.RoleHomeScreen
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.splash.SplashScreen

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

    fun restaurant(id: Int): String = "restaurant/" + id
    fun role(role: UserRole): String = "role/" + role.key
}

private fun isDebuggable(context: android.content.Context): Boolean =
    (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // ViewModels compartidos por toda la app (ámbito de la Activity)
    val restaurantsViewModel: RestaurantsViewModel = viewModel()
    val cartViewModel: CartViewModel = viewModel()
    val sessionViewModel: SessionViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }

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
            restaurantsViewModel.loadRestaurants(seed = isDebuggable(context))
            selectedTab = MainTab.HOME
            goClearingBackStack(Routes.LOCATION)
        } else {
            goClearingBackStack(Routes.role(profile.role))
        }
    }

    fun logout() {
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
                onOpenFilters = { navController.navigate(Routes.FILTERS) },
                onOpenRestaurant = { id -> navController.navigate(Routes.restaurant(id)) },
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
            RestaurantDetailScreen(
                restaurantId = entry.arguments?.getInt("id") ?: 0,
                restaurantsViewModel = restaurantsViewModel,
                cartViewModel = cartViewModel,
                onBack = { navController.popBackStack() },
                onViewCart = {
                    selectedTab = MainTab.CART
                    navController.popBackStack()
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
    }
}
