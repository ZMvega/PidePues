package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserRole
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.SessionViewModel

/** Pantalla de espera para cuentas de domiciliario. */
@Composable
fun RoleHomeScreen(
    role: UserRole,
    sessionViewModel: SessionViewModel,
    onLogout: () -> Unit,
) {
    if (role == UserRole.RESTAURANT) {
        val dashboardViewModel: RestaurantDashboardViewModel = viewModel()
        RestaurantDashboardScreen(
            sessionViewModel = sessionViewModel,
            dashboardViewModel = dashboardViewModel,
            onLogout = onLogout,
        )
        return
    }

    StatusBarAppearance(darkIcons = true)
    val profile by sessionViewModel.profile.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "🛵", fontSize = 72.sp)
        Text(
            text = stringResource(R.string.role_home_delivery_title),
            color = Ink,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = profile?.fullName.orEmpty(),
            color = Primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(R.string.role_home_delivery_subtitle),
            color = Muted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
        OutlinedActionButton(
            text = stringResource(R.string.profile_logout),
            onClick = onLogout,
            modifier = Modifier.padding(top = 32.dp),
        )
    }
}
