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
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*
import androidx.compose.ui.platform.LocalFocusManager

/**
 * Campo de texto con etiqueta arriba: fondo gris redondeado, borde naranja al enfocar
 * y mensaje de error debajo.
 */
@Composable
fun AppTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    autoCorrect: Boolean = true,
    isPassword: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    Column(modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = InkSoft,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(placeholder, color = Hint, fontSize = 15.sp) },
            isError = error != null,
            shape = RoundedCornerShape(14.dp),
            textStyle = TextStyle(fontSize = 15.sp, color = Ink),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                capitalization = capitalization,
                autoCorrectEnabled = autoCorrect,
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onImeAction?.invoke()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = FieldBg,
                errorContainerColor = FieldBg,
                focusedBorderColor = Primary,
                unfocusedBorderColor = FieldStroke,
                errorBorderColor = Danger,
                cursorColor = Primary,
                focusedTextColor = Ink,
                unfocusedTextColor = Ink
            )
        )
        if (error != null) {
            Text(
                text = error,
                color = Danger,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}
