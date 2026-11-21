package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Review
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.IconTile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.PrimaryButton
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.StatusBarAppearance
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*

/**
 * Pantalla "Reseñas" para el rol Restaurante.
 * Conectada en tiempo real a Firestore. Muestra la tarjeta resumen con desglose de estrellas,
 * filtros por estrellas, listado de reseñas de clientes y opción para responder a cada una.
 */
@Composable
fun RestaurantReviewsScreen(
    dashboard: RestaurantDashboardUiState,
    onSelectStarFilter: (Int) -> Unit,
    onReplyToReview: (reviewId: String, replyText: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarAppearance(darkIcons = true)
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val appContext = context.applicationContext
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var replyingReview by remember { mutableStateOf<Review?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ScreenBg,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            ReviewsHeader(onBack = onBack)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta de resumen de calificación general y barras de progreso por estrella
            item {
                RatingSummaryCard(dashboard = dashboard)
            }

            // Barra horizontal de filtros por estrellas
            item {
                StarFilterRow(
                    selectedFilter = dashboard.selectedStarFilter,
                    onSelectFilter = onSelectStarFilter
                )
            }

            // Lista de reseñas filtradas o estado vacío
            if (dashboard.filteredReviews.isEmpty()) {
                item {
                    EmptyReviewsState()
                }
            } else {
                items(dashboard.filteredReviews, key = { it.id }) { review ->
                    ReviewCard(
                        review = review,
                        onOpenReplyDialog = { replyingReview = review }
                    )
                }
            }
        }
    }

    // Diálogo modal para escribir y enviar la respuesta a la reseña
    if (replyingReview != null) {
        ReplyDialog(
            review = replyingReview!!,
            onDismiss = { replyingReview = null },
            onSendReply = { replyText ->
                onReplyToReview(replyingReview!!.id, replyText)
                replyingReview = null
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(appContext.getString(R.string.reviews_reply_success))
                }
            }
        )
    }
}

@Composable
private fun ReviewsHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CircleGray)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.cd_back),
                tint = Ink,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.reviews_title),
                color = Ink,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.reviews_subtitle),
                color = Muted,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun RatingSummaryCard(dashboard: RestaurantDashboardUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF7F7F7)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Columna izquierda: Promedio general, 5 estrellas e indicaciones de conteo
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = dashboard.formattedRating,
                    color = Ink,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                RatingStarsRow(
                    rating = dashboard.rating.toFloat(),
                    starSize = 18.dp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.dashboard_review_count, dashboard.reviewCount),
                    color = Muted,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.width(16.dp))

            // Columna derecha: 5 barras de progreso apiladas de la 5 a la 1
            Column(
                modifier = Modifier.weight(1.4f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                (5 downTo 1).forEach { starLevel ->
                    val count = when (starLevel) {
                        5 -> dashboard.star5Count
                        4 -> dashboard.star4Count
                        3 -> dashboard.star3Count
                        2 -> dashboard.star2Count
                        1 -> dashboard.star1Count
                        else -> 0
                    }
                    val progress = dashboard.starProgress(starLevel)

                    StarLevelProgressBarRow(
                        starLevel = starLevel,
                        progress = progress,
                        count = count
                    )
                }
            }
        }
    }
}

@Composable
private fun StarLevelProgressBarRow(
    starLevel: Int,
    progress: Float,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = starLevel.toString(),
            color = Muted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(14.dp)
        )
        Spacer(Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(7.dp)
                .clip(CircleShape)
                .background(OutlineGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = progress)
                    .clip(CircleShape)
                    .background(Gold)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = count.toString(),
            color = Muted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            modifier = Modifier.width(16.dp)
        )
    }
}

@Composable
private fun StarFilterRow(
    selectedFilter: Int,
    onSelectFilter: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            0 to stringResource(R.string.reviews_filter_all),
            5 to stringResource(R.string.reviews_filter_stars_format, 5),
            4 to stringResource(R.string.reviews_filter_stars_format, 4),
            3 to stringResource(R.string.reviews_filter_stars_format, 3),
            2 to stringResource(R.string.reviews_filter_stars_format, 2),
            1 to stringResource(R.string.reviews_filter_stars_format, 1)
        ).forEach { (starValue, label) ->
            val isSelected = starValue == selectedFilter
            Surface(
                onClick = { onSelectFilter(starValue) },
                shape = RoundedCornerShape(50),
                color = if (isSelected) Primary else Color.White,
                border = if (isSelected) null else BorderStroke(1.dp, OutlineGray),
                modifier = Modifier.height(38.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight()
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else Ink,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(
    review: Review,
    onOpenReplyDialog: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OutlineGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera de la reseña: Avatar, cliente, fecha relativa y estrellas
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TileOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.customerInitial,
                        color = Primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.customerName,
                        color = Ink,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = review.formattedRelativeDate,
                        color = Muted,
                        fontSize = 12.sp
                    )
                }

                RatingStarsRow(
                    rating = review.rating,
                    starSize = 14.dp
                )
            }

            // Comentario de la reseña si no está vacío
            if (review.comment.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = review.comment,
                    color = InkSoft,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(Modifier.height(14.dp))

            // Sección de respuesta: Botón "Responder" o recuadro "Tu respuesta"
            if (review.hasReply) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ScreenBg)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.reviews_reply_header),
                            color = Ink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = review.reply,
                            color = Muted,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.reviews_reply_action),
                    color = Primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(onClick = onOpenReplyDialog)
                        .padding(vertical = 4.dp, horizontal = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun RatingStarsRow(
    rating: Float,
    starSize: androidx.compose.ui.unit.Dp
) {
    val roundedRating = Math.round(rating).coerceIn(0, 5)
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        (1..5).forEach { starIndex ->
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = null,
                tint = if (starIndex <= roundedRating) Gold else OutlineGray,
                modifier = Modifier.size(starSize)
            )
        }
    }
}

@Composable
private fun ReplyDialog(
    review: Review,
    onDismiss: () -> Unit,
    onSendReply: (String) -> Unit
) {
    var replyText by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.reviews_reply_dialog_title, review.customerName),
                color = Ink,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                if (review.comment.isNotBlank()) {
                    Text(
                        text = "\"${review.comment}\"",
                        color = Muted,
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(12.dp))
                }
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text(stringResource(R.string.reviews_reply_dialog_hint), fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            PrimaryButton(
                text = stringResource(R.string.reviews_reply_send),
                onClick = {
                    val trimmed = replyText.trim()
                    if (trimmed.isNotBlank()) {
                        onSendReply(trimmed)
                    }
                },
                enabled = replyText.trim().isNotBlank()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = Muted,
                    fontSize = 14.sp
                )
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun EmptyReviewsState() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconTile(
                icon = R.drawable.ic_star,
                iconTint = Gold,
                background = TileAmber,
                size = 52.dp
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.reviews_empty_title),
                color = Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.reviews_empty_message),
                color = Muted,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
