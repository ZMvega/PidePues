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
import androidx.annotation.DrawableRes
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*

/** Chip de selección: naranja cuando está elegido, blanco con borde cuando no. */
@Composable
fun PidePuesChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Primary else Color.White,
        contentColor = if (selected) Color.White else InkSoft,
        border = BorderStroke(1.dp, if (selected) Primary else OutlineGray)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

/** Tarjeta blanca de esquinas redondeadas con sombra suave. */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cornerRadius: Dp = 20.dp,
    elevation: Dp = 2.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    val colors = CardDefaults.cardColors(containerColor = Color.White)
    val cardElevation = CardDefaults.cardElevation(defaultElevation = elevation)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, shape = shape, colors = colors, elevation = cardElevation, content = content)
    } else {
        Card(modifier = modifier, shape = shape, colors = colors, elevation = cardElevation, content = content)
    }
}

/** Cuadro de color con un emoji dentro. */
@Composable
fun EmojiTile(
    emoji: String,
    background: Color,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    fontSize: TextUnit = 22.sp,
    cornerRadius: Dp = 12.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(emoji, fontSize = fontSize)
    }
}

/** Cuadro de color con un icono vectorial dentro. */
@Composable
fun IconTile(
    @DrawableRes icon: Int,
    iconTint: Color,
    background: Color,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
    }
}

/** Selector  −  1  +  para las cantidades. */
@Composable
fun QuantityStepper(
    quantity: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        CircleIconButton(
            icon = R.drawable.ic_remove,
            onClick = onMinus,
            size = 32.dp,
            iconPadding = 8.dp,
            contentDescription = stringResource(R.string.cd_remove)
        )
        Text(
            text = quantity.toString(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(28.dp)
        )
        CircleIconButton(
            icon = R.drawable.ic_add,
            onClick = onPlus,
            containerColor = Primary,
            tint = Color.White,
            size = 32.dp,
            iconPadding = 8.dp,
            contentDescription = stringResource(R.string.cd_add)
        )
    }
}

/** Fila con el título de una sección ("MI CUENTA"). */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Hint,
        letterSpacing = 1.sp,
        modifier = modifier.padding(start = 4.dp, top = 20.dp, bottom = 8.dp)
    )
}
