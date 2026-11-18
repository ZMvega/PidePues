package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.search

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Category
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.RestaurantsViewModel

@Composable
fun SearchScreen(
    restaurantsViewModel: RestaurantsViewModel,
    onFiltersClick: () -> Unit,
    onRestaurantClick: (Int) -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    val state by restaurantsViewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val resultsCount = state.searchResults.size
    val resultsLabel = LocalContext.current.resources.getQuantityString(R.plurals.results_count, resultsCount, resultsCount)

    // Texto local para que el cursor no salte; se sincroniza si cambia desde fuera (ej. una búsqueda reciente)
    var text by rememberSaveable { mutableStateOf(state.query) }
    LaunchedEffect(state.query) {
        if (text != state.query) text = state.query
    }

    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        // Barra superior: campo de búsqueda + botón de filtros
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp)
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(FieldBg)
                    .border(1.dp, FieldStroke, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = Hint,
                    modifier = Modifier.size(20.dp)
                )
                BasicTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        restaurantsViewModel.setQuery(it)
                    },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = Ink),
                    cursorBrush = SolidColor(Primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            restaurantsViewModel.submitSearch(text)
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (text.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.search_hint),
                                    color = Hint,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Primary)
                    .clickable(onClick = onFiltersClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_tune),
                    contentDescription = stringResource(R.string.cd_filters),
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (!state.isSearching) {
                // ----- Sin texto ni filtros: recientes + categorías -----
                if (state.recentSearches.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.recent_title),
                                color = Ink,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            LinkText(
                                text = stringResource(R.string.recent_clear),
                                onClick = { restaurantsViewModel.clearRecents() }
                            )
                        }
                    }
                    items(state.recentSearches, key = { it }) { recent ->
                        RecentSearchRow(
                            text = recent,
                            onClick = { restaurantsViewModel.selectRecent(recent) }
                        )
                    }
                }
                item {
                    Text(
                        text = stringResource(R.string.explore_title),
                        color = Ink,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                item {
                    Column {
                        Category.values().toList().chunked(4).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { category ->
                                    CategoryTile(
                                        category = category,
                                        onClick = { restaurantsViewModel.applyCategory(category) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                repeat(4 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                            }
                        }
                    }
                }
            } else {
                // ----- Con texto o filtros activos: resultados -----
                val count = resultsCount
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = resultsLabel,
                            color = Ink,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        LinkText(
                            text = stringResource(R.string.search_clear),
                            onClick = { restaurantsViewModel.clearAll() }
                        )
                    }
                }
                if (count == 0 && !state.isLoading) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.empty_results),
                                color = Muted,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                            LinkText(
                                text = stringResource(R.string.empty_clear),
                                onClick = { restaurantsViewModel.clearAll() },
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    items(state.searchResults, key = { it.restaurant.id }) { item ->
                        RestaurantCard(
                            item = item,
                            onClick = { onRestaurantClick(item.restaurant.id) },
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentSearchRow(text: String, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        onClick = onClick,
        cornerRadius = 14.dp,
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_schedule),
                contentDescription = null,
                tint = Muted,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                color = InkSoft,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f).padding(start = 12.dp)
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Hint,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun CategoryTile(category: Category, onClick: () -> Unit, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier, onClick = onClick, cornerRadius = 16.dp) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = category.emoji, fontSize = 28.sp)
            Text(
                text = category.label,
                color = InkSoft,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}
