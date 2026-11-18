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
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserRole
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.AuthViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onRegistered: (UserProfile) -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    val context = LocalContext.current
    val state by authViewModel.state.collectAsStateWithLifecycle()

    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var role by rememberSaveable { mutableStateOf(UserRole.CLIENT) }
    var acceptedTerms by rememberSaveable { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val errorName = stringResource(R.string.error_name)
    val errorEmail = stringResource(R.string.error_email)
    val errorPhone = stringResource(R.string.error_phone)
    val errorPassword = stringResource(R.string.error_password)

    LaunchedEffect(state.messageRes) {
        val message = state.messageRes
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            authViewModel.clearMessage()
        }
    }

    fun submit() {
        nameError = if (Validators.isValidFullName(fullName)) null else errorName
        emailError = if (Validators.isValidEmail(email)) null else errorEmail
        phoneError = if (Validators.isValidPhone(phone)) null else errorPhone
        passwordError = if (Validators.isValidPassword(password)) null else errorPassword
        val valid = nameError == null && emailError == null && phoneError == null && passwordError == null
        if (valid) {
            authViewModel.register(fullName, email, phone, password, role, onRegistered)
        }
    }

    // "Términos de servicio" y "Política de privacidad" resaltados en naranja
    val termsPrefix = stringResource(R.string.register_terms_prefix)
    val termsService = stringResource(R.string.register_terms_service)
    val termsAnd = stringResource(R.string.register_terms_and)
    val termsPrivacy = stringResource(R.string.register_terms_privacy)
    val termsText = buildAnnotatedString {
        append(termsPrefix)
        withStyle(SpanStyle(color = Primary, fontWeight = FontWeight.Bold)) { append(termsService) }
        append(termsAnd)
        withStyle(SpanStyle(color = Primary, fontWeight = FontWeight.Bold)) { append(termsPrivacy) }
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
        CircleIconButton(
            icon = R.drawable.ic_arrow_back,
            onClick = onBack,
            contentDescription = stringResource(R.string.cd_back)
        )

        Text(
            text = stringResource(R.string.register_title),
            color = Ink,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp)
        )
        Text(
            text = stringResource(R.string.register_subtitle),
            color = Muted,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Mayúscula inicial y sin sugerencias en el nombre
        AppTextField(
            label = stringResource(R.string.label_full_name),
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = stringResource(R.string.hint_full_name),
            error = nameError,
            capitalization = KeyboardCapitalization.Words,
            autoCorrect = false,
            modifier = Modifier.padding(top = 28.dp)
        )
        AppTextField(
            label = stringResource(R.string.label_email),
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(R.string.hint_email),
            error = emailError,
            keyboardType = KeyboardType.Email,
            autoCorrect = false,
            modifier = Modifier.padding(top = 16.dp)
        )
        AppTextField(
            label = stringResource(R.string.label_phone),
            value = phone,
            onValueChange = { phone = it },
            placeholder = stringResource(R.string.hint_phone),
            error = phoneError,
            keyboardType = KeyboardType.Phone,
            modifier = Modifier.padding(top = 16.dp)
        )
        AppTextField(
            label = stringResource(R.string.label_password),
            value = password,
            onValueChange = { password = it },
            placeholder = stringResource(R.string.hint_password_register),
            error = passwordError,
            keyboardType = KeyboardType.Password,
            autoCorrect = false,
            isPassword = true,
            imeAction = ImeAction.Done,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Tipo de cuenta
        Text(
            text = stringResource(R.string.role_label),
            color = InkSoft,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PidePuesChip(stringResource(R.string.role_client), role == UserRole.CLIENT, { role = UserRole.CLIENT })
            PidePuesChip(stringResource(R.string.role_restaurant), role == UserRole.RESTAURANT, { role = UserRole.RESTAURANT })
            PidePuesChip(stringResource(R.string.role_delivery), role == UserRole.DELIVERY, { role = UserRole.DELIVERY })
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = acceptedTerms,
                onCheckedChange = { acceptedTerms = it },
                colors = CheckboxDefaults.colors(checkedColor = Primary)
            )
            Text(text = termsText, color = InkSoft, fontSize = 14.sp)
        }

        // Deshabilitado (salmón) hasta aceptar los términos
        PrimaryButton(
            text = stringResource(if (state.isLoading) R.string.register_loading else R.string.register_button),
            onClick = { submit() },
            enabled = acceptedTerms && !state.isLoading,
            modifier = Modifier.padding(top = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.register_have_account), color = Muted, fontSize = 14.sp)
            LinkText(text = stringResource(R.string.register_login_link), onClick = onBack)
        }
    }
}
