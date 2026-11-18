package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.splash

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
import androidx.compose.ui.geometry.Offset
import co.edu.udea.compumovil.labs20262_gr08.pidepues.data.AuthRepository
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import kotlinx.coroutines.delay

/**
 * Pantalla de arranque. Con sesión iniciada devuelve el perfil (para ir según el rol);
 * sin sesión devuelve null (ir al login).
 */
@Composable
fun SplashScreen(onFinished: (UserProfile?) -> Unit) {
    StatusBarAppearance(darkIcons = false)
    val currentOnFinished by rememberUpdatedState(onFinished)

    LaunchedEffect(Unit) {
        delay(1500)
        if (AuthRepository.isLoggedIn) {
            AuthRepository.loadProfile { result -> currentOnFinished(result.getOrNull()) }
        } else {
            currentOnFinished(null)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(SplashStart, SplashEnd),
                    start = Offset(0f, 0f),
                    end = Offset.Infinite
                )
            )
    ) {
        // Círculos decorativos translúcidos, parcialmente fuera de pantalla
        Box(
            Modifier.size(260.dp).align(Alignment.TopEnd).offset(x = 90.dp, y = (-90).dp)
                .clip(CircleShape).background(White12)
        )
        Box(
            Modifier.size(130.dp).align(Alignment.CenterEnd).offset(x = 50.dp, y = (-140).dp)
                .clip(CircleShape).background(White12)
        )
        Box(
            Modifier.size(300.dp).align(Alignment.BottomStart).offset(x = (-70).dp, y = 130.dp)
                .clip(CircleShape).background(White12)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(White20)
                    .border(1.dp, White20, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_restaurant),
                    contentDescription = stringResource(R.string.cd_logo),
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            Text(
                text = stringResource(R.string.app_name),
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp)
            )
            Text(
                text = stringResource(R.string.splash_tagline),
                color = White80,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) {
                Box(Modifier.size(8.dp).clip(CircleShape).background(White80))
            }
        }
    }
}
