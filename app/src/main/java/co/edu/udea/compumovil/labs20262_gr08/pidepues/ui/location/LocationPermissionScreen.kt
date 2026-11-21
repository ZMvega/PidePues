package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.location

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
import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import co.edu.udea.compumovil.labs20262_gr08.pidepues.location.LocationProvider

/**
 * "¿Dónde estás?": pide el permiso de ubicación. Si ya estaba concedido, avanza solo.
 * [onDone] recibe true si se debe usar la ubicación del dispositivo.
 */
@Composable
fun LocationPermissionScreen(onDone: (useDeviceLocation: Boolean) -> Unit) {
    StatusBarAppearance(darkIcons = true)
    val context = LocalContext.current
    val alreadyGranted = remember { LocationProvider.hasPermission(context) }
    val currentOnDone by rememberUpdatedState(onDone)
    val deniedHint = stringResource(R.string.location_denied_hint)
    val manualHint = stringResource(R.string.location_manual_hint)

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            currentOnDone(true)
        } else {
            Toast.makeText(context, deniedHint, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(alreadyGranted) {
        if (alreadyGranted) currentOnDone(true)
    }
    if (alreadyGranted) {
        Box(Modifier.fillMaxSize().background(Color.White))
        return
    }

    // "cercanía y calificación" resaltado en naranja
    val prefix = stringResource(R.string.location_description_prefix)
    val highlight = stringResource(R.string.location_description_highlight)
    val suffix = stringResource(R.string.location_description_suffix)
    val description = buildAnnotatedString {
        append(prefix)
        withStyle(SpanStyle(color = Primary, fontWeight = FontWeight.Bold)) { append(highlight) }
        append(suffix)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MapIllustration(Modifier.padding(top = 16.dp))

            Text(
                text = stringResource(R.string.location_title),
                color = Ink,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 32.dp)
            )
            Text(
                text = description,
                color = Muted,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )

            // Aviso de privacidad (pilar de seguridad del proyecto)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Peach)
                    .border(1.dp, PeachStroke, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(TileOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_shield),
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(Modifier.padding(start = 12.dp)) {
                    Text(
                        text = stringResource(R.string.location_privacy_title),
                        color = Ink,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.location_privacy_subtitle),
                        color = Muted,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PrimaryButton(
                text = stringResource(R.string.location_allow),
                onClick = {
                    launcher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            )
            // Sin permiso se usa la ubicación por defecto (El Poblado). Más adelante: buscador de direcciones.
            Text(
                text = stringResource(R.string.location_manual),
                color = Muted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        Toast.makeText(context, manualHint, Toast.LENGTH_LONG).show()
                        currentOnDone(false)
                    }
                    .padding(12.dp)
            )
        }
    }
}

/** Ilustración: mapa con restaurantes cercanos y pin de ubicación. */
@Composable
private fun MapIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.fillMaxSize().clip(CircleShape).background(MapPeach))
        Box(Modifier.size(170.dp).border(2.dp, MapRing, CircleShape))

        EmojiTileFloating("🍔", Modifier.align(Alignment.TopStart).offset(x = 44.dp, y = 46.dp))
        EmojiTileFloating("🍕", Modifier.align(Alignment.TopEnd).offset(x = (-40).dp, y = 34.dp))
        EmojiTileFloating("🥗", Modifier.align(Alignment.BottomStart).offset(x = 36.dp, y = (-60).dp))

        Box(Modifier.size(110.dp).clip(CircleShape).background(PinHalo))
        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_location),
                contentDescription = stringResource(R.string.cd_map),
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun EmojiTileFloating(emoji: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(44.dp)
            .shadow(4.dp, RoundedCornerShape(14.dp))
            .background(Color.White, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = 22.sp)
    }
}
