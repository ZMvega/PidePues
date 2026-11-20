package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toUri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuCategory
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuItem
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.PrimaryButton
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.SessionViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

@Composable
fun RestaurantMenuScreen(
    state: RestaurantDashboardUiState,
    modifier: Modifier = Modifier,
    onCreateProduct: () -> Unit,
    onEditProduct: (MenuItem) -> Unit,
    onAvailabilityChange: (MenuItem, Boolean) -> Unit,
    onRetry: () -> Unit,
    onClearError: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategoryKey by rememberSaveable { mutableStateOf<String?>(null) }
    val availableCount = state.menuItems.count { it.isAvailable }

    Column(modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.restaurant_menu_title),
                    color = Ink,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(
                        R.string.restaurant_menu_available_count,
                        availableCount,
                        state.menuItems.size
                    ),
                    color = Muted,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Primary)
                    .clickable(onClick = onCreateProduct),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = stringResource(R.string.restaurant_product_add),
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                placeholder = { Text(stringResource(R.string.restaurant_product_search), color = Hint) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        tint = Muted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FieldStroke,
                    unfocusedBorderColor = FieldStroke,
                    focusedContainerColor = FieldBg,
                    unfocusedContainerColor = FieldBg
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MenuCategoryChip(
                    label = stringResource(R.string.restaurant_menu_all),
                    selected = selectedCategoryKey == null,
                    onClick = { selectedCategoryKey = null }
                )
                MenuCategory.entries.forEach { category ->
                    MenuCategoryChip(
                        label = category.label,
                        selected = selectedCategoryKey == category.key,
                        onClick = { selectedCategoryKey = category.key }
                    )
                }
            }

            AvailabilityInfoBanner()

            val filteredItems = state.menuItems.filter { item ->
                val matchesCategory = selectedCategoryKey == null || item.category.key == selectedCategoryKey
                val matchesQuery = query.isBlank() || item.name.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)
                matchesCategory && matchesQuery
            }

            when {
                state.menuMessageRes != null -> MenuErrorMessage(
                    messageRes = state.menuMessageRes,
                    onRetry = onRetry,
                    onClear = onClearError
                )
                state.isMenuLoading && state.menuItems.isEmpty() -> Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
                state.menuItems.isEmpty() -> MenuEmptyState()
                filteredItems.isEmpty() -> Text(
                    text = stringResource(R.string.restaurant_menu_no_search_results),
                    color = Muted,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp)
                )
                else -> {
                    val categories = selectedCategoryKey?.let { key ->
                        MenuCategory.entries.filter { it.key == key }
                    } ?: MenuCategory.entries
                    categories.forEach { category ->
                        val productsInCategory = filteredItems.filter { it.category == category }
                        if (productsInCategory.isNotEmpty()) {
                            val allInCategory = state.menuItems.filter { it.category == category }
                            MenuCategoryHeader(
                                category = category.label,
                                available = allInCategory.count { it.isAvailable },
                                total = allInCategory.size
                            )
                            productsInCategory.forEach { product ->
                                MenuProductCard(
                                    product = product,
                                    onClick = { onEditProduct(product) },
                                    onAvailabilityChange = { onAvailabilityChange(product, it) }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MenuCategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = if (selected) Primary else CircleGray,
        contentColor = if (selected) Color.White else InkSoft
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
        )
    }
}

@Composable
private fun AvailabilityInfoBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(FieldBg)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(IconGreen))
        Text(
            text = stringResource(R.string.restaurant_menu_availability_info),
            color = Muted,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

@Composable
private fun MenuCategoryHeader(category: String, available: Int, total: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(category, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.restaurant_menu_category_count, available, total),
            color = Hint,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun MenuProductCard(
    product: MenuItem,
    onClick: () -> Unit,
    onAvailabilityChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(17.dp),
        color = Color.White,
        border = BorderStroke(1.dp, FieldStroke),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProductThumbnail(
                imageUri = product.imageUri,
                name = product.name,
                size = 62.dp,
                cornerRadius = 14.dp
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = product.name,
                    color = Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.description,
                    color = Muted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = product.price.toCop(),
                    color = Primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(
                    checked = product.isAvailable,
                    onCheckedChange = onAvailabilityChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = IconGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = OutlineGray
                    )
                )
                Text(
                    text = stringResource(
                        if (product.isAvailable) R.string.restaurant_product_available
                        else R.string.restaurant_product_sold_out
                    ),
                    color = if (product.isAvailable) IconGreen else Hint,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ProductThumbnail(
    imageUri: String?,
    name: String,
    size: androidx.compose.ui.unit.Dp,
    cornerRadius: androidx.compose.ui.unit.Dp
) {
    if (imageUri.isNullOrBlank()) {
        val swatches = listOf(PrimaryDark, Primary, IconGreen, IconPink, IconBlue)
        val colorIndex = abs(name.hashCode() % swatches.size)
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(cornerRadius))
                .background(swatches[colorIndex]),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.firstOrNull()?.uppercase() ?: "?",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        ProductPhotoPreview(
            imageUri = imageUri,
            modifier = Modifier.size(size).clip(RoundedCornerShape(cornerRadius))
        )
    }
}

@Composable
private fun MenuEmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(68.dp).clip(CircleShape).background(TileOrange),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_restaurant),
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(30.dp)
            )
        }
        Text(
            text = stringResource(R.string.restaurant_menu_empty_title),
            color = Ink,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            text = stringResource(R.string.restaurant_menu_empty_message),
            color = Muted,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}

@Composable
private fun MenuErrorMessage(messageRes: Int, onRetry: () -> Unit, onClear: () -> Unit) {
    val message = stringResource(messageRes)
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = Danger, fontSize = 13.sp)
        TextButton(onClick = { onClear(); onRetry() }) {
            Text(stringResource(R.string.retry), color = Primary)
        }
    }
}

@Composable
fun MenuProductEditorScreen(
    item: MenuItem?,
    state: RestaurantDashboardUiState,
    editorKey: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSave: (MenuItem) -> Unit
) {
    var name by rememberSaveable(editorKey, item?.id) { mutableStateOf(item?.name.orEmpty()) }
    var description by rememberSaveable(editorKey, item?.id) { mutableStateOf(item?.description.orEmpty()) }
    var price by rememberSaveable(editorKey, item?.id) {
        mutableStateOf(if (item == null) "" else item.price.toString())
    }
    var selectedCategoryKey by rememberSaveable(editorKey, item?.id) {
        mutableStateOf(item?.category?.key ?: MenuCategory.HAMBURGUESAS.key)
    }
    var isAvailable by rememberSaveable(editorKey, item?.id) { mutableStateOf(item?.isAvailable ?: true) }
    var imageUri by rememberSaveable(editorKey, item?.id) { mutableStateOf(item?.imageUri) }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { pickedUri -> if (pickedUri != null) imageUri = pickedUri.toString() }
    )
    val selectedCategory = MenuCategory.fromKey(selectedCategoryKey)
    val parsedPrice = price.toIntOrNull()
    val isValid = name.isNotBlank() && parsedPrice != null && parsedPrice > 0
    val errorMessage = state.menuMessageRes?.let { stringResource(it) }

    BackHandler(onBack = onBack)

    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(CircleGray).clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.cd_back),
                    tint = Ink,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = stringResource(if (item == null) R.string.restaurant_product_new else R.string.restaurant_product_edit),
                color = Ink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductPhotoPicker(
                imageUri = imageUri,
                onPickPhoto = {
                    photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            )
            LabeledProductField(
                label = stringResource(R.string.restaurant_product_name),
                value = name,
                onValueChange = { name = it },
                placeholder = stringResource(R.string.restaurant_product_name_hint)
            )
            LabeledProductField(
                label = stringResource(R.string.restaurant_product_description),
                value = description,
                onValueChange = { description = it },
                placeholder = stringResource(R.string.restaurant_product_description_hint),
                minLines = 4
            )
            LabeledProductField(
                label = stringResource(R.string.restaurant_product_price),
                value = price,
                onValueChange = { value -> price = value.filter(Char::isDigit) },
                placeholder = stringResource(R.string.restaurant_product_price_hint),
                keyboardType = KeyboardType.Number,
                leadingDollar = true
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.restaurant_product_category),
                    color = InkSoft,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    MenuCategory.entries.chunked(2).forEach { rowCategories ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowCategories.forEach { category ->
                                MenuCategoryChip(
                                    label = category.label,
                                    selected = selectedCategory == category,
                                    onClick = { selectedCategoryKey = category.key }
                                )
                            }
                        }
                    }
                }
            }
            ProductAvailabilitySetting(isAvailable = isAvailable, onChange = { isAvailable = it })
            if (errorMessage != null) {
                Text(errorMessage, color = Danger, fontSize = 13.sp)
            }
            Spacer(Modifier.height(4.dp))
        }

        Surface(color = Color.White, shadowElevation = 6.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                PrimaryButton(
                    text = stringResource(
                        if (item == null) R.string.restaurant_product_create
                        else R.string.restaurant_product_save
                    ),
                    onClick = {
                        val savedPrice = parsedPrice ?: return@PrimaryButton
                        onSave(
                            MenuItem(
                                id = item?.id.orEmpty(),
                                restaurantId = item?.restaurantId ?: 0,
                                restaurantDocumentId = state.restaurantDocumentId.orEmpty(),
                                name = name.trim(),
                                description = description.trim(),
                                price = savedPrice,
                                emoji = item?.emoji?.ifBlank { "🍽️" } ?: "🍽️",
                                category = selectedCategory,
                                isAvailable = isAvailable,
                                imageUri = imageUri
                            )
                        )
                    },
                    enabled = isValid && !state.isSavingMenuItem
                )
            }
        }
    }
}

@Composable
private fun LabeledProductField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingDollar: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(label, color = InkSoft, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Hint, fontSize = 14.sp) },
            leadingIcon = if (leadingDollar) {
                { Text("$", color = Muted, fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }
            } else null,
            shape = RoundedCornerShape(14.dp),
            minLines = minLines,
            singleLine = minLines == 1,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = OutlineGray,
                focusedContainerColor = FieldBg,
                unfocusedContainerColor = FieldBg
            )
        )
    }
}

@Composable
private fun ProductPhotoPicker(imageUri: String?, onPickPhoto: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(164.dp)
            .clip(shape)
            .background(FieldBg)
            .drawBehind {
                drawRoundRect(
                    color = OutlineGray,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx()),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 5.dp.toPx()))
                    )
                )
            }
            .clickable(onClick = onPickPhoto),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri.isNullOrBlank()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Peach),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_camera),
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(23.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.restaurant_product_photo_add),
                    color = InkSoft,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = stringResource(R.string.restaurant_product_photo_hint),
                    color = Hint,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        } else {
            ProductPhotoPreview(imageUri, Modifier.fillMaxSize())
            Text(
                text = stringResource(R.string.restaurant_product_photo_change),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0x66000000))
                    .padding(vertical = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProductPhotoPreview(imageUri: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(initialValue = null, key1 = imageUri) {
        value = withContext(Dispatchers.IO) { loadProductBitmap(context, imageUri.toUri()) }
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
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

private fun loadProductBitmap(context: android.content.Context, uri: Uri): Bitmap? {
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

@Composable
private fun ProductAvailabilitySetting(isAvailable: Boolean, onChange: (Boolean) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = FieldBg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.restaurant_product_status),
                    color = Ink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(
                        if (isAvailable) R.string.restaurant_product_status_visible
                        else R.string.restaurant_product_status_hidden
                    ),
                    color = Muted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
            Switch(
                checked = isAvailable,
                onCheckedChange = onChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = IconGreen,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = OutlineGray
                )
            )
        }
    }
}
