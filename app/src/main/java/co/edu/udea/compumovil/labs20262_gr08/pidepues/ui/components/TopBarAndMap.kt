package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components

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

/** Barra superior blanca con flecha atrás, título centrado y subtítulo opcional. */
@Composable
fun AppTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp)
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        CircleIconButton(
            icon = R.drawable.ic_arrow_back,
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart),
            contentDescription = stringResource(R.string.cd_back)
        )
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = Ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (subtitle != null) {
                Text(text = subtitle, color = Muted, fontSize = 12.sp)
            }
        }
    }
}

/** Mapa decorativo (calles dibujadas) sobre el que se colocan pines y etiquetas. */
@Composable
fun MapCard(
    modifier: Modifier = Modifier,
    height: Dp = 180.dp,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .background(MapPeach)
    ) {
        Canvas(Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val road = 10.dp.toPx()
            drawLine(Color.White, Offset(0f, h * 0.35f), Offset(w, h * 0.55f), strokeWidth = road)
            drawLine(Color.White, Offset(w * 0.30f, 0f), Offset(w * 0.45f, h), strokeWidth = road)
            drawLine(Color.White, Offset(w * 0.75f, 0f), Offset(w * 0.65f, h), strokeWidth = road * 0.7f)
            drawLine(MapRing, Offset(0f, h * 0.82f), Offset(w, h * 0.70f), strokeWidth = road * 0.6f)
        }
        content()
    }
}

/** Pin naranja con icono de ubicación (centro del mapa). */
@Composable
fun MapPin(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(72.dp), contentAlignment = Alignment.Center) {
        Box(Modifier.size(72.dp).clip(CircleShape).background(PinHalo))
        Box(
            modifier = Modifier
                .size(44.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_location),
                contentDescription = stringResource(R.string.cd_map_illustration),
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/** Barra inferior fija con sombra y respeto de la barra de navegación. */
@Composable
fun BottomActionBar(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp)
            .background(Color.White)
            .navigationBarsPadding()
            .padding(16.dp),
        content = content
    )
}
