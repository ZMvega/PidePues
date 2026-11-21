package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.role

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.MenuItem
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.UserProfile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.IconTile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.StatusBarAppearance
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.SessionViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.toCop

private enum class RestaurantSection {
    PANEL, ORDERS, HISTORY, MENU, PROFILE, REVIEWS
}

@Composable
fun RestaurantDashboardScreen(
    sessionViewModel: SessionViewModel,
    dashboardViewModel: RestaurantDashboardViewModel,
    onLogout: () -> Unit,
) {
    StatusBarAppearance(darkIcons = true)
    val profile by sessionViewModel.profile.collectAsStateWithLifecycle()
    val dashboard by dashboardViewModel.state.collectAsStateWithLifecycle()
    var section by rememberSaveable { mutableStateOf(RestaurantSection.PANEL) }
    var isMenuEditorVisible by rememberSaveable { mutableStateOf(false) }
    var menuEditorInstance by rememberSaveable { mutableIntStateOf(0) }
    var menuItemInEditor by remember { mutableStateOf<MenuItem?>(null) }
    var selectedOrderId by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedOrder = dashboard.allOrders.firstOrNull { it.id == selectedOrderId }

    DisposableEffect(profile?.uid) {
        val currentProfile = profile
        if (currentProfile != null) {
            dashboardViewModel.observeMenu(currentProfile.uid, currentProfile.fullName)
            dashboardViewModel.observeOrders(currentProfile.uid)
            dashboardViewModel.observeReviews(currentProfile.uid)
            dashboardViewModel.observeProfile(currentProfile.uid, currentProfile)
        }
        onDispose {
            dashboardViewModel.stopObservingMenu()
            dashboardViewModel.stopObservingOrders()
            dashboardViewModel.stopObservingReviews()
            dashboardViewModel.stopObservingProfile()
        }
    }

    if (selectedOrder != null) {
        OrderDetailScreen(
            order = selectedOrder,
            onBack = { selectedOrderId = null },
            onAcceptOrder = { dashboardViewModel.acceptOrder(selectedOrder) },
            onAdvanceToPrepared = { dashboardViewModel.markOrderAsPrepared(selectedOrder) },
            onAdvanceToEnCamino = { dashboardViewModel.markOrderAsEnCamino(selectedOrder) }
        )
        return
    }

    BackHandler(enabled = (section == RestaurantSection.MENU && isMenuEditorVisible) || section != RestaurantSection.PANEL) {
        if (section == RestaurantSection.MENU && isMenuEditorVisible) {
            isMenuEditorVisible = false
            menuItemInEditor = null
            dashboardViewModel.clearMenuMessage()
        } else {
            section = RestaurantSection.PANEL
        }
    }

    Scaffold(
        containerColor = ScreenBg,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            if (!(section == RestaurantSection.MENU && isMenuEditorVisible) && section != RestaurantSection.HISTORY && section != RestaurantSection.REVIEWS) {
                NavigationBar(containerColor = Color.White) {
                    listOf(
                        RestaurantSection.PANEL to R.string.dashboard_tab_panel,
                        RestaurantSection.ORDERS to R.string.dashboard_tab_orders,
                        RestaurantSection.MENU to R.string.dashboard_tab_menu,
                        RestaurantSection.PROFILE to R.string.nav_profile,
                    ).forEach { (destination, label) ->
                        val showBadge = destination == RestaurantSection.ORDERS && dashboard.solicitadoCount > 0
                        NavigationBarItem(
                            selected = section == destination,
                            onClick = { section = destination },
                            icon = {
                                if (showBadge) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = Primary,
                                                contentColor = Color.White
                                            ) {
                                                Text(dashboard.solicitadoCount.toString())
                                            }
                                        }
                                    ) {
                                        Icon(
                                            painter = painterResource(destination.iconResource()),
                                            contentDescription = null,
                                        )
                                    }
                                } else {
                                    Icon(
                                        painter = painterResource(destination.iconResource()),
                                        contentDescription = null,
                                    )
                                }
                            },
                            label = { Text(stringResource(label), fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                indicatorColor = TileOrange,
                                unselectedIconColor = Hint,
                                unselectedTextColor = Hint,
                            ),
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (section == RestaurantSection.REVIEWS) {
            RestaurantReviewsScreen(
                dashboard = dashboard,
                onSelectStarFilter = dashboardViewModel::selectStarFilter,
                onReplyToReview = { reviewId, replyText ->
                    dashboardViewModel.replyToReview(reviewId, replyText)
                },
                onBack = { section = RestaurantSection.PANEL },
                modifier = Modifier.padding(innerPadding)
            )
        } else if (section == RestaurantSection.HISTORY) {
            SalesHistoryScreen(
                allOrders = dashboard.allOrders,
                onBack = { section = RestaurantSection.PANEL },
                onOrderClick = { order -> selectedOrderId = order.id },
                modifier = Modifier.padding(innerPadding)
            )
        } else if (section == RestaurantSection.MENU) {
            if (isMenuEditorVisible) {
                MenuProductEditorScreen(
                    item = menuItemInEditor,
                    state = dashboard,
                    editorKey = menuEditorInstance,
                    modifier = Modifier.padding(innerPadding),
                    onBack = {
                        isMenuEditorVisible = false
                        menuItemInEditor = null
                        dashboardViewModel.clearMenuMessage()
                    },
                    onSave = { item ->
                        dashboardViewModel.saveMenuItem(item) { saved ->
                            if (saved) {
                                isMenuEditorVisible = false
                                menuItemInEditor = null
                            }
                        }
                    }
                )
            } else {
                RestaurantMenuScreen(
                    state = dashboard,
                    onCreateProduct = {
                        menuItemInEditor = null
                        menuEditorInstance++
                        dashboardViewModel.clearMenuMessage()
                        isMenuEditorVisible = true
                    },
                    onEditProduct = { item ->
                        menuItemInEditor = item
                        menuEditorInstance++
                        dashboardViewModel.clearMenuMessage()
                        isMenuEditorVisible = true
                    },
                    modifier = Modifier.padding(innerPadding),
                    onAvailabilityChange = dashboardViewModel::setAvailability,
                    onRetry = {
                        profile?.let { dashboardViewModel.retryMenu(it.uid, it.fullName) }
                    },
                    onClearError = dashboardViewModel::clearMenuMessage
                )
            }
        } else if (section == RestaurantSection.ORDERS) {
            RestaurantOrdersScreen(
                dashboard = dashboard,
                onSelectFilter = dashboardViewModel::selectOrderFilter,
                onAcceptOrder = dashboardViewModel::acceptOrder,
                onAdvanceToPrepared = dashboardViewModel::markOrderAsPrepared,
                onAdvanceToEnCamino = dashboardViewModel::markOrderAsEnCamino,
                onOrderClick = { order -> selectedOrderId = order.id },
                modifier = Modifier.padding(innerPadding)
            )
        } else if (section == RestaurantSection.PROFILE) {
            RestaurantProfileScreen(
                profile = profile,
                dashboard = dashboard,
                onNavigateToHistory = { section = RestaurantSection.HISTORY },
                onNavigateToReviews = { section = RestaurantSection.REVIEWS },
                onLogout = {
                    dashboardViewModel.stopAllObserving()
                    onLogout()
                },
                onSaveProfile = { updatedProfile ->
                    dashboardViewModel.saveRestaurantProfile(updatedProfile) { success ->
                        if (success) {
                            sessionViewModel.load()
                        }
                    }
                },
                onInviteCourier = { phone, onDone ->
                    val uid = profile?.uid ?: dashboard.restaurantProfile.uid
                    dashboardViewModel.inviteCourier(uid, phone, onDone)
                },
                modifier = Modifier.padding(innerPadding),
                onClearMessage = {
                    dashboardViewModel.clearProfileMessage()
                    dashboardViewModel.clearCourierMessage()
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                RestaurantHeader(profile = profile, section = section)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (section) {
                        RestaurantSection.PANEL -> DashboardContent(
                            dashboard = dashboard,
                            onOpenOrders = { section = RestaurantSection.ORDERS },
                            onOpenMenu = { section = RestaurantSection.MENU },
                            onOpenHistory = { section = RestaurantSection.HISTORY },
                            onOpenReviews = { section = RestaurantSection.REVIEWS },
                            onOrderClick = { orderId -> selectedOrderId = orderId }
                        )
                        RestaurantSection.ORDERS -> Unit
                        RestaurantSection.HISTORY -> HistoryContent(
                            orders = dashboard.orderHistory,
                            onOrderClick = { orderId -> selectedOrderId = orderId }
                        )
                        RestaurantSection.MENU -> Unit
                        RestaurantSection.PROFILE -> Unit
                        RestaurantSection.REVIEWS -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun RestaurantHeader(profile: UserProfile?, section: RestaurantSection) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            if (profile?.initials.isNullOrBlank()) {
                Icon(
                    painter = painterResource(R.drawable.ic_restaurant),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = profile.initials,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            if (section == RestaurantSection.PANEL) {
                val firstName = profile?.firstName.orEmpty()
                Text(
                    text = if (firstName.isBlank()) stringResource(R.string.dashboard_greeting_default)
                    else stringResource(R.string.dashboard_greeting, firstName),
                    color = Muted,
                    fontSize = 12.sp
                )
                Text(
                    text = profile?.fullName?.takeIf { it.isNotBlank() }
                        ?: stringResource(R.string.dashboard_restaurant_fallback),
                    color = Ink,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                Text(
                    text = stringResource(section.titleResource()),
                    color = Ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = profile?.fullName.orEmpty(),
                    color = Muted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    dashboard: RestaurantDashboardUiState,
    onOpenOrders: () -> Unit,
    onOpenMenu: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenReviews: () -> Unit,
    onOrderClick: (String) -> Unit = {}
) {
    NewOrdersCard(dashboard.newOrders.size, onOpenOrders)
    RevenueCard(dashboard.dailyRevenueCop)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = R.drawable.ic_history,
            iconTint = IconGreen,
            iconBackground = TileGreen,
            value = dashboard.completedOrders.toString(),
            label = stringResource(R.string.dashboard_completed_orders),
            onClick = onOpenHistory
        )
        MetricCard(
            modifier = Modifier.weight(1f),
            icon = R.drawable.ic_star,
            iconTint = Gold,
            iconBackground = TileAmber,
            value = stringResource(R.string.dashboard_rating_format, dashboard.formattedRating),
            label = stringResource(R.string.dashboard_review_count, dashboard.reviewCount),
            onClick = onOpenReviews
        )
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.dashboard_orders_in_progress),
            color = Ink,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(R.string.dashboard_view_board),
            color = Primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onOpenOrders)
                .padding(4.dp)
        )
    }
    if (dashboard.inProgressOrders.isEmpty()) {
        EmptyCard(
            icon = R.drawable.ic_shopping_bag,
            title = stringResource(R.string.dashboard_orders_empty_title),
            message = stringResource(R.string.dashboard_orders_empty_message)
        )
    } else {
        dashboard.inProgressOrders.forEach { order ->
            OrderCard(order = order, onClick = { onOrderClick(order.id) })
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        QuickAction(
            modifier = Modifier.weight(1f),
            icon = R.drawable.ic_restaurant,
            label = stringResource(R.string.dashboard_action_menu),
            onClick = onOpenMenu
        )
        QuickAction(
            modifier = Modifier.weight(1f),
            icon = R.drawable.ic_history,
            label = stringResource(R.string.dashboard_action_history),
            onClick = onOpenHistory
        )
        QuickAction(
            modifier = Modifier.weight(1f),
            icon = R.drawable.ic_notifications,
            label = stringResource(R.string.dashboard_action_simulate),
            onClick = onOpenOrders
        )
    }
}

@Composable
private fun NewOrdersCard(count: Int, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Peach,
        border = BorderStroke(1.dp, PeachStroke)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTile(
                icon = R.drawable.ic_notifications,
                iconTint = Color.White,
                background = Primary,
                size = 44.dp
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (count == 0) stringResource(R.string.dashboard_no_new_orders)
                    else stringResource(R.string.dashboard_new_orders_count, count),
                    color = PrimaryDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.dashboard_waiting_orders),
                    color = Muted,
                    fontSize = 12.sp
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun RevenueCard(dailyRevenueCop: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(HeaderStart, HeaderEnd)))
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.dashboard_today_revenue),
            color = White90,
            fontSize = 13.sp
        )
        Text(
            text = dailyRevenueCop.toCop(),
            color = Color.White,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(Modifier.height(18.dp))
        HorizontalDivider(color = White20)
        Text(
            text = stringResource(R.string.dashboard_revenue_empty),
            color = White90,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    icon: Int,
    iconTint: Color,
    iconBackground: Color,
    value: String,
    label: String,
    onClick: (() -> Unit)? = null
) {
    val cardModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    Column(
        modifier = cardModifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        IconTile(icon = icon, iconTint = iconTint, background = iconBackground, size = 38.dp)
        Text(
            text = value,
            color = Ink,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(text = label, color = Muted, fontSize = 11.sp, lineHeight = 15.sp)
    }
}

@Composable
private fun QuickAction(
    modifier: Modifier,
    icon: Int,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 70.dp),
        shape = RoundedCornerShape(16.dp),
        color = Peach
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                color = PrimaryDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun OrdersContent(
    orders: List<RestaurantDashboardOrder>,
    onOrderClick: (String) -> Unit = {}
) {
    if (orders.isEmpty()) {
        EmptyCard(
            icon = R.drawable.ic_shopping_bag,
            title = stringResource(R.string.dashboard_orders_empty_title),
            message = stringResource(R.string.dashboard_orders_empty_message)
        )
    } else {
        orders.forEach { order ->
            OrderCard(order = order, onClick = { onOrderClick(order.id) })
        }
    }
}

@Composable
private fun HistoryContent(
    orders: List<RestaurantDashboardOrder>,
    onOrderClick: (String) -> Unit = {}
) {
    if (orders.isEmpty()) {
        EmptyCard(
            icon = R.drawable.ic_history,
            title = stringResource(R.string.dashboard_history_empty_title),
            message = stringResource(R.string.dashboard_history_empty_message)
        )
    } else {
        orders.forEach { order ->
            OrderCard(order = order, onClick = { onOrderClick(order.id) })
        }
    }
}

@Composable
private fun EmptyCard(icon: Int, title: String, message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconTile(icon = icon, iconTint = Primary, background = TileOrange, size = 52.dp)
            Text(
                text = title,
                color = Ink,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = message,
                color = Muted,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun OrderCard(
    order: RestaurantDashboardOrder,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, FieldStroke)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(order.customerName, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    stringResource(R.string.dashboard_order_item_count, order.itemCount),
                    color = Muted,
                    fontSize = 12.sp
                )
            }
            Text(order.totalCop.toCop(), color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private fun RestaurantSection.iconResource(): Int = when (this) {
    RestaurantSection.PANEL -> R.drawable.ic_home
    RestaurantSection.ORDERS -> R.drawable.ic_shopping_bag
    RestaurantSection.HISTORY -> R.drawable.ic_history
    RestaurantSection.MENU -> R.drawable.ic_restaurant
    RestaurantSection.PROFILE -> R.drawable.ic_person
    RestaurantSection.REVIEWS -> R.drawable.ic_star
}

private fun RestaurantSection.titleResource(): Int = when (this) {
    RestaurantSection.PANEL -> R.string.dashboard_title
    RestaurantSection.ORDERS -> R.string.dashboard_tab_orders
    RestaurantSection.HISTORY -> R.string.dashboard_action_history
    RestaurantSection.MENU -> R.string.dashboard_tab_menu
    RestaurantSection.PROFILE -> R.string.nav_profile
    RestaurantSection.REVIEWS -> R.string.reviews_title
}
