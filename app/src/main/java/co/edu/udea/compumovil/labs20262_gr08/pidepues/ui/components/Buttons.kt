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
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*

/** Botón naranja principal: se oscurece al presionar, pasar el mouse o enfocar; salmón si está deshabilitado. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    fillWidth: Boolean = true
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val hovered by interaction.collectIsHoveredAsState()
    val focused by interaction.collectIsFocusedAsState()
    val container = when {
        !enabled -> PrimaryDisabled
        pressed || hovered || focused -> PrimaryDark
        else -> Primary
    }
    Button(
        onClick = onClick,
        modifier = (if (fillWidth) modifier.fillMaxWidth() else modifier).height(56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = Color.White,
            disabledContainerColor = PrimaryDisabled,
            disabledContentColor = Color.White
        ),
        interactionSource = interaction
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

/** Botón blanco con borde (ej. "Continuar con Google"). */
@Composable
fun OutlinedActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, OutlineGray),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Ink)
    ) {
        if (leadingIcon != null) {
            leadingIcon()
            Spacer(Modifier.width(12.dp))
        }
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

/** Texto naranja en negrita que se puede tocar (enlaces). */
@Composable
fun LinkText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    color: Color = Primary
) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(4.dp)
    )
}

/** Botón circular con un icono (atrás, más, menos...). */
@Composable
fun CircleIconButton(
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = CircleGray,
    tint: Color = Ink,
    size: Dp = 40.dp,
    iconPadding: Dp = 10.dp,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(size - iconPadding * 2)
        )
    }
}
