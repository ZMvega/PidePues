package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.filters

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Category
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantFilters
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.SortOption
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.emoji
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.subtitleRes
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.titleRes
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.RestaurantsViewModel

/**
 * Pantalla de filtros. Trabaja sobre un borrador: los cambios solo se aplican
 * al presionar "Aplicar filtros"; la flecha atrás los descarta.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersScreen(
    restaurantsViewModel: RestaurantsViewModel,
    onClose: () -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    val state by restaurantsViewModel.state.collectAsStateWithLifecycle()
    var draft by remember { mutableStateOf(state.filters) }

    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        // Barra superior: atrás | Filtros | Limpiar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp)
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            CircleIconButton(
                icon = R.drawable.ic_arrow_back,
                onClick = onClose,
                modifier = Modifier.align(Alignment.CenterStart),
                contentDescription = stringResource(R.string.cd_back)
            )
            Text(
                text = stringResource(R.string.filters_title),
                color = Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
            LinkText(
                text = stringResource(R.string.filters_clear),
                onClick = { draft = RestaurantFilters() },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ----- Ordenar por -----
            AppCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.filters_sort_title),
                        color = Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    SortOption.values().forEach { option ->
                        SortRow(
                            option = option,
                            selected = draft.sort == option,
                            onClick = { draft = draft.copy(sort = option) }
                        )
                    }
                }
            }

            // ----- Distancia máxima -----
            AppCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.filters_distance_title),
                            color = Ink,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = formatKm(draft.maxDistanceKm),
                            color = Primary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = draft.maxDistanceKm,
                        onValueChange = { draft = draft.copy(maxDistanceKm = Math.round(it * 2f) / 2f) },
                        valueRange = 0.5f..10f,
                        steps = 18,
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = OutlineGray
                        )
                    )
                    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                        Text(
                            text = stringResource(R.string.filters_distance_min),
                            color = Hint,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = stringResource(R.string.filters_distance_max),
                            color = Hint,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // ----- Calificación mínima -----
            AppCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.filters_rating_title),
                        color = Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0f, 3f, 3.5f, 4f, 4.5f).forEach { rating ->
                            PidePuesChip(
                                text = if (rating == 0f) stringResource(R.string.rating_all) else ratingLabel(rating),
                                selected = draft.minRating == rating,
                                onClick = { draft = draft.copy(minRating = rating) }
                            )
                        }
                    }
                }
            }

            // ----- Tipo de cocina (selección múltiple) -----
            AppCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.filters_cuisine_title),
                        color = Ink,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    FlowRow(
                        modifier = Modifier.padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Category.values().forEach { category ->
                            val selected = category in draft.categories
                            PidePuesChip(
                                text = category.emoji + " " + category.label,
                                selected = selected,
                                onClick = {
                                    val updated = if (selected) draft.categories - category else draft.categories + category
                                    draft = draft.copy(categories = updated)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Botón fijo abajo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp)
                .background(Color.White)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            PrimaryButton(
                text = stringResource(R.string.filters_apply),
                onClick = {
                    restaurantsViewModel.setFilters(draft)
                    onClose()
                }
            )
        }
    }
}

@Composable
private fun SortRow(option: SortOption, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) TileOrange else FieldBg)
            .clickable(onClick = onClick)
            .padding(start = 14.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = option.emoji, fontSize = 18.sp, modifier = Modifier.width(32.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(option.titleRes),
                color = Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(option.subtitleRes),
                color = Muted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = Primary, unselectedColor = OutlineGray)
        )
    }
}

private fun formatKm(value: Float): String = when {
    value < 1f -> (value * 1000).toInt().toString() + " m"
    value % 1f == 0f -> value.toInt().toString() + " km"
    else -> value.toString() + " km"
}

private fun ratingLabel(value: Float): String =
    (if (value % 1f == 0f) value.toInt().toString() else value.toString()) + "+"
