package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.auth

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
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.domain.Validators
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoggedIn: (UserProfile) -> Unit,
    onRegister: () -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    val context = LocalContext.current
    val state by authViewModel.state.collectAsStateWithLifecycle()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val errorEmail = stringResource(R.string.error_email)
    val errorPasswordRequired = stringResource(R.string.error_password_required)
    val errorResetEmail = stringResource(R.string.reset_enter_email)
    val googleSoon = stringResource(R.string.google_soon)

    // Muestra los mensajes (errores o avisos) del ViewModel una sola vez
    LaunchedEffect(state.messageRes) {
        val message = state.messageRes
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            authViewModel.clearMessage()
        }
    }

    fun submit() {
        emailError = if (Validators.isValidEmail(email)) null else errorEmail
        passwordError = if (password.isEmpty()) errorPasswordRequired else null
        if (emailError == null && passwordError == null) {
            authViewModel.login(email.trim(), password, onLoggedIn)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_restaurant),
                    contentDescription = stringResource(R.string.cd_logo),
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = stringResource(R.string.app_name),
                color = Ink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Text(
            text = stringResource(R.string.login_title),
            color = Ink,
            fontSize = 30.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 32.dp)
        )
        Text(
            text = stringResource(R.string.login_subtitle),
            color = Muted,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        AppTextField(
            label = stringResource(R.string.label_email),
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(R.string.hint_email),
            error = emailError,
            keyboardType = KeyboardType.Email,
            autoCorrect = false,
            modifier = Modifier.padding(top = 32.dp)
        )
        AppTextField(
            label = stringResource(R.string.label_password),
            value = password,
            onValueChange = { password = it },
            placeholder = stringResource(R.string.hint_password_login),
            error = passwordError,
            keyboardType = KeyboardType.Password,
            autoCorrect = false,
            isPassword = true,
            imeAction = ImeAction.Done,
            onImeAction = { submit() },
            modifier = Modifier.padding(top = 16.dp)
        )

        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
            LinkText(
                text = stringResource(R.string.login_forgot),
                fontSize = 13.sp,
                onClick = {
                    if (Validators.isValidEmail(email)) {
                        authViewModel.sendPasswordReset(email.trim())
                    } else {
                        emailError = errorResetEmail
                    }
                }
            )
        }

        PrimaryButton(
            text = stringResource(if (state.isLoading) R.string.login_loading else R.string.login_button),
            onClick = { submit() },
            enabled = !state.isLoading,
            modifier = Modifier.padding(top = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineGray)
            Text(
                text = stringResource(R.string.login_or_continue),
                color = Hint,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineGray)
        }

        OutlinedActionButton(
            text = stringResource(R.string.login_google),
            onClick = { Toast.makeText(context, googleSoon, Toast.LENGTH_SHORT).show() },
            modifier = Modifier.padding(top = 20.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_google_g),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(22.dp)
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.login_no_account), color = Muted, fontSize = 14.sp)
            LinkText(text = stringResource(R.string.login_register_link), onClick = onRegister)
        }
    }
}
