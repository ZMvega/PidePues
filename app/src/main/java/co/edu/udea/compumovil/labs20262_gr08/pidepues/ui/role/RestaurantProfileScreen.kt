package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.domain.Validators
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantProfile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.AppTextField
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

@Composable
fun RestaurantProfileScreen(
    profile: UserProfile?,
    dashboard: RestaurantDashboardUiState,
    onNavigateToHistory: () -> Unit,
    onNavigateToReviews: () -> Unit,
    onLogout: () -> Unit,
    onSaveProfile: (RestaurantProfile) -> Unit,
    onInviteCourier: (String, (Boolean) -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    onClearMessage: () -> Unit = {}
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val firestoreProfile = dashboard.restaurantProfile

    var name by rememberSaveable(firestoreProfile.name) {
        mutableStateOf(firestoreProfile.name.ifBlank { profile?.fullName.orEmpty() })
    }
    var address by rememberSaveable(firestoreProfile.address) {
        mutableStateOf(firestoreProfile.address)
    }
    var phone by rememberSaveable(firestoreProfile.phone) {
        mutableStateOf(firestoreProfile.phone.ifBlank { profile?.phone.orEmpty() })
    }
    var email by rememberSaveable(firestoreProfile.email) {
        mutableStateOf(firestoreProfile.email.ifBlank { profile?.email.orEmpty() })
    }
    var bankName by rememberSaveable(firestoreProfile.bankName) {
        mutableStateOf(firestoreProfile.bankName)
    }
    var accountType by rememberSaveable(firestoreProfile.accountType) {
        mutableStateOf(firestoreProfile.accountType)
    }
    var accountNumber by rememberSaveable(firestoreProfile.accountNumber) {
        mutableStateOf(firestoreProfile.accountNumber)
    }
    var accountHolder by rememberSaveable(firestoreProfile.accountHolder) {
        mutableStateOf(firestoreProfile.accountHolder)
    }
    var logoUrl by rememberSaveable(firestoreProfile.logoUrl) {
        mutableStateOf(firestoreProfile.logoUrl)
    }

    var notifyNewOrders by rememberSaveable(firestoreProfile.notifyNewOrders) {
        mutableStateOf(firestoreProfile.notifyNewOrders)
    }
    var notifyNewReviews by rememberSaveable(firestoreProfile.notifyNewReviews) {
        mutableStateOf(firestoreProfile.notifyNewReviews)
    }
    var notifyAlertSound by rememberSaveable(firestoreProfile.notifyAlertSound) {
        mutableStateOf(firestoreProfile.notifyAlertSound)
    }
    var notifyDailySummary by rememberSaveable(firestoreProfile.notifyDailySummary) {
        mutableStateOf(firestoreProfile.notifyDailySummary)
    }

    val initialName = firestoreProfile.name.ifBlank { profile?.fullName.orEmpty() }
    val initialPhone = firestoreProfile.phone.ifBlank { profile?.phone.orEmpty() }
    val initialEmail = firestoreProfile.email.ifBlank { profile?.email.orEmpty() }

    val hasChanges = remember(
        name, address, phone, email, logoUrl, bankName, accountType,
        accountNumber, accountHolder, notifyNewOrders, notifyNewReviews,
        notifyAlertSound, notifyDailySummary, firestoreProfile, profile
    ) {
        name.trim() != initialName.trim() ||
            address.trim() != firestoreProfile.address.trim() ||
            phone.trim() != initialPhone.trim() ||
            email.trim() != initialEmail.trim() ||
            bankName.trim() != firestoreProfile.bankName.trim() ||
            accountType != firestoreProfile.accountType ||
            accountNumber.trim() != firestoreProfile.accountNumber.trim() ||
            accountHolder.trim() != firestoreProfile.accountHolder.trim() ||
            logoUrl != firestoreProfile.logoUrl ||
            notifyNewOrders != firestoreProfile.notifyNewOrders ||
            notifyNewReviews != firestoreProfile.notifyNewReviews ||
            notifyAlertSound != firestoreProfile.notifyAlertSound ||
            notifyDailySummary != firestoreProfile.notifyDailySummary
    }

    var showInviteDialog by rememberSaveable { mutableStateOf(false) }
    var courierPhone by rememberSaveable { mutableStateOf("") }
    var inviteError by rememberSaveable { mutableStateOf<String?>(null) }
    var isInviting by rememberSaveable { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            logoUrl = uri.toString()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(dashboard.profileMessageRes) {
        val res = dashboard.profileMessageRes
        if (res != null) {
            snackbarHostState.showSnackbar(appContext.getString(res))
            onClearMessage()
        }
    }
    LaunchedEffect(dashboard.courierInviteMessageRes) {
        val res = dashboard.courierInviteMessageRes
        if (res != null) {
            snackbarHostState.showSnackbar(appContext.getString(res))
            onClearMessage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        // 1. Encabezado superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.profile_config_title),
                color = Ink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 2. Tarjeta principal del restaurante
                RestaurantHeaderCard(
                    name = name.ifBlank {
                        profile?.fullName.orEmpty().ifBlank { stringResource(R.string.dashboard_restaurant_fallback) }
                    },
                    initial = name.trim().firstOrNull()?.uppercase()
                        ?: profile?.initials?.firstOrNull()?.toString()
                        ?: "R",
                    logoUrl = logoUrl,
                    rating = dashboard.formattedRating,
                    onPickPhoto = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onNavigateToReviews = onNavigateToReviews
                )

                // 3. Fila de 2 botones de acceso rápido
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        text = stringResource(R.string.profile_action_sales_history),
                        onClick = onNavigateToHistory,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        text = stringResource(R.string.profile_action_reviews),
                        onClick = onNavigateToReviews,
                        modifier = Modifier.weight(1f)
                    )
                }

                // 4. Sección "Equipo de reparto"
                SectionTitle(stringResource(R.string.profile_delivery_team_title))
                DeliveryTeamCard(onInviteClick = { showInviteDialog = true })

                // 5. Sección "Datos del negocio"
                SectionTitle(stringResource(R.string.profile_business_data_title))
                BusinessDataCard(
                    name = name,
                    onNameChange = { name = it },
                    address = address,
                    onAddressChange = { address = it },
                    phone = phone,
                    onPhoneChange = { phone = it },
                    email = email,
                    onEmailChange = { email = it }
                )

                // 6. Sección "Cuenta bancaria para pagos"
                SectionTitle(stringResource(R.string.profile_bank_account_title))
                BankInfoBanner()
                BankAccountCard(
                    bankName = bankName,
                    onBankNameChange = { bankName = it },
                    accountType = accountType,
                    onAccountTypeChange = { accountType = it },
                    accountNumber = accountNumber,
                    onAccountNumberChange = { accountNumber = it },
                    accountHolder = accountHolder,
                    onAccountHolderChange = { accountHolder = it }
                )

                // 7. Sección "Notificaciones"
                SectionTitle(stringResource(R.string.profile_notifications_title))
                NotificationsCard(
                    notifyNewOrders = notifyNewOrders,
                    onNotifyNewOrdersChange = { notifyNewOrders = it },
                    notifyNewReviews = notifyNewReviews,
                    onNotifyNewReviewsChange = { notifyNewReviews = it },
                    notifyAlertSound = notifyAlertSound,
                    onNotifyAlertSoundChange = { notifyAlertSound = it },
                    notifyDailySummary = notifyDailySummary,
                    onNotifyDailySummaryChange = { notifyDailySummary = it }
                )

                // 8. Botones de acción y pie de página
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val uid = profile?.uid ?: firestoreProfile.uid
                        val updated = RestaurantProfile(
                            uid = uid,
                            name = name,
                            address = address,
                            phone = phone,
                            email = email,
                            logoUrl = logoUrl,
                            bankName = bankName,
                            accountType = accountType,
                            accountNumber = accountNumber,
                            accountHolder = accountHolder,
                            notifyNewOrders = notifyNewOrders,
                            notifyNewReviews = notifyNewReviews,
                            notifyAlertSound = notifyAlertSound,
                            notifyDailySummary = notifyDailySummary
                        )
                        onSaveProfile(updated)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = hasChanges && !dashboard.isSavingProfile,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White,
                        disabledContainerColor = PrimaryDisabled,
                        disabledContentColor = Color.White
                    )
                ) {
                    if (dashboard.isSavingProfile) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.profile_saving), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text(stringResource(R.string.profile_save_changes), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Peach,
                    border = BorderStroke(1.dp, PeachStroke)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout),
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.profile_logout_action),
                            color = Primary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_store),
                        contentDescription = null,
                        tint = Hint,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.profile_app_version),
                        color = Hint,
                        fontSize = 12.sp
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }

    if (showInviteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isInviting) {
                    showInviteDialog = false
                    inviteError = null
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.profile_invite_dialog_title),
                    color = Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.profile_invite_courier_subtitle),
                        color = Muted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    AppTextField(
                        label = stringResource(R.string.profile_label_phone),
                        value = courierPhone,
                        onValueChange = {
                            courierPhone = it
                            inviteError = null
                        },
                        placeholder = stringResource(R.string.profile_invite_dialog_hint),
                        keyboardType = KeyboardType.Phone,
                        error = inviteError
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (!Validators.isValidPhone(courierPhone)) {
                            inviteError = appContext.getString(R.string.profile_invite_invalid_phone)
                            return@Button
                        }
                        val normalized = Validators.normalizePhone(courierPhone)
                        isInviting = true
                        onInviteCourier(normalized) { success ->
                            isInviting = false
                            if (success) {
                                showInviteDialog = false
                                courierPhone = ""
                                inviteError = null
                            }
                        }
                    },
                    enabled = courierPhone.isNotBlank() && !isInviting,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = Color.White
                    )
                ) {
                    if (isInviting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            stringResource(R.string.profile_invite_dialog_confirm),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showInviteDialog = false
                        inviteError = null
                    },
                    enabled = !isInviting
                ) {
                    Text(stringResource(R.string.profile_invite_dialog_cancel), color = Muted)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
private fun RestaurantHeaderCard(
    name: String,
    initial: String,
    logoUrl: String?,
    rating: String,
    onPickPhoto: () -> Unit,
    onNavigateToReviews: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, FieldStroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(72.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    if (!logoUrl.isNullOrBlank()) {
                        ProductPhotoPreview(logoUrl, Modifier.fillMaxSize())
                    } else {
                        Text(
                            text = initial,
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(BorderStroke(1.dp, FieldStroke), CircleShape)
                        .clickable(onClick = onPickPhoto),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_camera),
                        contentDescription = stringResource(R.string.profile_photo_change),
                        tint = Primary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = Ink,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.profile_verified_account),
                    color = Muted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_star),
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = rating,
                        color = Ink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.profile_see_reviews),
                        color = Primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(onClick = onNavigateToReviews)
                            .padding(vertical = 2.dp, horizontal = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(14.dp),
        color = Peach,
        border = BorderStroke(1.dp, PeachStroke)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = PrimaryDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DeliveryTeamCard(onInviteClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, FieldStroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TileBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delivery_van),
                    contentDescription = null,
                    tint = IconBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.profile_invite_courier_title),
                    color = Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.profile_invite_courier_subtitle),
                    color = Muted,
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                )
            }
            Text(
                text = stringResource(R.string.profile_invite_action),
                color = Primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onInviteClick)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun BusinessDataCard(
    name: String,
    onNameChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, FieldStroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AppTextField(
                label = stringResource(R.string.profile_label_business_name),
                value = name,
                onValueChange = onNameChange,
                placeholder = stringResource(R.string.profile_placeholder_business_name),
                keyboardType = KeyboardType.Text
            )
            AppTextField(
                label = stringResource(R.string.profile_label_address),
                value = address,
                onValueChange = onAddressChange,
                placeholder = stringResource(R.string.profile_placeholder_address),
                keyboardType = KeyboardType.Text
            )
            AppTextField(
                label = stringResource(R.string.profile_label_phone),
                value = phone,
                onValueChange = onPhoneChange,
                placeholder = stringResource(R.string.profile_placeholder_phone),
                keyboardType = KeyboardType.Phone
            )
            AppTextField(
                label = stringResource(R.string.profile_label_email),
                value = email,
                onValueChange = onEmailChange,
                placeholder = stringResource(R.string.profile_placeholder_email),
                keyboardType = KeyboardType.Email
            )
        }
    }
}

@Composable
private fun BankInfoBanner() {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = TileBlue,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_wallet),
                contentDescription = null,
                tint = IconBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.profile_bank_info_banner),
                color = Color(0xFF1E40AF),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun BankAccountCard(
    bankName: String,
    onBankNameChange: (String) -> Unit,
    accountType: String,
    onAccountTypeChange: (String) -> Unit,
    accountNumber: String,
    onAccountNumberChange: (String) -> Unit,
    accountHolder: String,
    onAccountHolderChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, FieldStroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AppTextField(
                label = stringResource(R.string.profile_label_bank),
                value = bankName,
                onValueChange = onBankNameChange,
                placeholder = stringResource(R.string.profile_placeholder_bank),
                keyboardType = KeyboardType.Text
            )

            Column {
                Text(
                    text = stringResource(R.string.profile_label_account_type),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = InkSoft,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AccountTypeChip(
                        text = stringResource(R.string.profile_account_type_savings),
                        selected = accountType == RestaurantProfile.ACCOUNT_TYPE_SAVINGS,
                        onClick = { onAccountTypeChange(RestaurantProfile.ACCOUNT_TYPE_SAVINGS) }
                    )
                    AccountTypeChip(
                        text = stringResource(R.string.profile_account_type_checking),
                        selected = accountType == RestaurantProfile.ACCOUNT_TYPE_CHECKING,
                        onClick = { onAccountTypeChange(RestaurantProfile.ACCOUNT_TYPE_CHECKING) }
                    )
                }
            }

            AppTextField(
                label = stringResource(R.string.profile_label_account_number),
                value = accountNumber,
                onValueChange = onAccountNumberChange,
                placeholder = stringResource(R.string.profile_placeholder_account_number),
                keyboardType = KeyboardType.Number
            )

            AppTextField(
                label = stringResource(R.string.profile_label_account_holder),
                value = accountHolder,
                onValueChange = onAccountHolderChange,
                placeholder = stringResource(R.string.profile_placeholder_account_holder),
                keyboardType = KeyboardType.Text
            )
        }
    }
}

@Composable
private fun AccountTypeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Primary else CircleGray,
        border = if (selected) null else BorderStroke(1.dp, OutlineGray)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else InkSoft,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun NotificationsCard(
    notifyNewOrders: Boolean,
    onNotifyNewOrdersChange: (Boolean) -> Unit,
    notifyNewReviews: Boolean,
    onNotifyNewReviewsChange: (Boolean) -> Unit,
    notifyAlertSound: Boolean,
    onNotifyAlertSoundChange: (Boolean) -> Unit,
    notifyDailySummary: Boolean,
    onNotifyDailySummaryChange: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, FieldStroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            NotificationSwitchItem(
                title = stringResource(R.string.profile_notif_new_orders_title),
                subtitle = stringResource(R.string.profile_notif_new_orders_sub),
                checked = notifyNewOrders,
                onCheckedChange = onNotifyNewOrdersChange
            )
            HorizontalDivider(color = DividerGray, thickness = 1.dp)
            NotificationSwitchItem(
                title = stringResource(R.string.profile_notif_new_reviews_title),
                subtitle = stringResource(R.string.profile_notif_new_reviews_sub),
                checked = notifyNewReviews,
                onCheckedChange = onNotifyNewReviewsChange
            )
            HorizontalDivider(color = DividerGray, thickness = 1.dp)
            NotificationSwitchItem(
                title = stringResource(R.string.profile_notif_alert_sound_title),
                subtitle = stringResource(R.string.profile_notif_alert_sound_sub),
                checked = notifyAlertSound,
                onCheckedChange = onNotifyAlertSoundChange
            )
            HorizontalDivider(color = DividerGray, thickness = 1.dp)
            NotificationSwitchItem(
                title = stringResource(R.string.profile_notif_daily_summary_title),
                subtitle = stringResource(R.string.profile_notif_daily_summary_sub),
                checked = notifyDailySummary,
                onCheckedChange = onNotifyDailySummaryChange
            )
        }
    }
}

@Composable
private fun NotificationSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_notifications),
            contentDescription = null,
            tint = Hint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Ink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = Muted,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF22C55E),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = OutlineGray,
                uncheckedBorderColor = Color.Transparent,
                checkedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Ink,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
    )
}

@Composable
private fun ProductPhotoPreview(imageUri: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(initialValue = null, key1 = imageUri) {
        value = withContext(Dispatchers.IO) { loadProfileBitmap(context, imageUri.toUri()) }
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(modifier.background(FieldBg), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(R.drawable.ic_camera),
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

private fun loadProfileBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
        val largestDimension = maxOf(bounds.outWidth, bounds.outHeight)
        var sampleSize = 1
        while (largestDimension / sampleSize > 512) sampleSize *= 2
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
    } catch (_: IOException) {
        null
    } catch (_: SecurityException) {
        null
    }
}
