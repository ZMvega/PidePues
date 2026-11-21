package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.profile

import androidx.compose.foundation.*
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
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.SessionViewModel

private data class ProfileOption(
    @StringRes val title: Int,
    @StringRes val subtitle: Int,
    @DrawableRes val icon: Int,
    val iconTint: Color,
    val tile: Color
)

private val accountOptions = listOf(
    ProfileOption(R.string.opt_addresses_title, R.string.opt_addresses_subtitle, R.drawable.ic_location, Primary, TileOrange),
    ProfileOption(R.string.opt_payments_title, R.string.opt_payments_subtitle, R.drawable.ic_credit_card, IconBlue, TileBlue),
    ProfileOption(R.string.opt_favorites_title, R.string.opt_favorites_subtitle, R.drawable.ic_favorite_border, IconPink, TilePink),
    ProfileOption(R.string.opt_history_title, R.string.opt_history_subtitle, R.drawable.ic_history, Gold, TileAmber)
)

private val preferenceOptions = listOf(
    ProfileOption(R.string.opt_notifications_title, R.string.opt_notifications_subtitle, R.drawable.ic_notifications, IconGreen, TileGreen),
    ProfileOption(R.string.opt_settings_title, R.string.opt_settings_subtitle, R.drawable.ic_settings, Muted, TileGray)
)

private val supportOptions = listOf(
    ProfileOption(R.string.opt_help_title, R.string.opt_help_subtitle, R.drawable.ic_edit, IconBlue, TileBlue),
    ProfileOption(R.string.opt_terms_title, R.string.opt_terms_subtitle, R.drawable.ic_description, Muted, TileGray)
)

@Composable
fun ProfileScreen(
    sessionViewModel: SessionViewModel,
    onOpenOrders: () -> Unit,
    onLogout: () -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    val profile by sessionViewModel.profile.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().background(ScreenBg)) {
        // Barra superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp)
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.profile_title),
                color = Ink,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            LinkText(text = stringResource(R.string.profile_edit), onClick = { })
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Cabecera naranja: avatar, datos reales y estadísticas
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.linearGradient(listOf(HeaderStart, HeaderEnd)))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(72.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(White20)
                                .border(3.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile?.initials.orEmpty(),
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_camera),
                                contentDescription = stringResource(R.string.cd_change_photo),
                                tint = Primary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Column(Modifier.padding(start = 16.dp)) {
                        Text(
                            text = profile?.fullName.orEmpty(),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = profile?.email.orEmpty(),
                            color = White80,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(text = profile?.phone.orEmpty(), color = White80, fontSize = 13.sp)
                    }
                }

                // Las estadísticas son de ejemplo hasta tener pedidos reales (semana 8)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatTile(stringResource(R.string.emoji_orders), stringResource(R.string.stat_orders_value), stringResource(R.string.stat_orders_label), Modifier.weight(1f))
                    StatTile(stringResource(R.string.emoji_favorites), stringResource(R.string.stat_favorites_value), stringResource(R.string.stat_favorites_label), Modifier.weight(1f))
                    StatTile(stringResource(R.string.emoji_rating), stringResource(R.string.stat_rating_value), stringResource(R.string.stat_rating_label), Modifier.weight(1f))
                }
            }

            // Nivel PidePues Gold
            AppCard(Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EmojiTile(emoji = stringResource(R.string.emoji_gold), background = TileAmber)
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(
                            text = stringResource(R.string.gold_title),
                            color = Ink,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LinearProgressIndicator(
                                progress = { 0.6f },
                                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = Gold,
                                trackColor = DividerGray
                            )
                            Text(
                                text = stringResource(R.string.gold_points),
                                color = Muted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = Hint,
                        modifier = Modifier.padding(start = 8.dp).size(20.dp)
                    )
                }
            }

            SectionLabel(stringResource(R.string.section_account))
            OptionsCard(accountOptions, onOptionClick = { opt ->
                if (opt.title == R.string.opt_history_title) onOpenOrders()
            })
            SectionLabel(stringResource(R.string.section_preferences))
            OptionsCard(preferenceOptions)
            SectionLabel(stringResource(R.string.section_support))
            OptionsCard(supportOptions)

            // Cerrar sesión
            AppCard(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                onClick = onLogout
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconTile(icon = R.drawable.ic_logout, iconTint = Danger, background = TileRed)
                    Text(
                        text = stringResource(R.string.profile_logout),
                        color = Danger,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f).padding(start = 12.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = Danger,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = stringResource(R.string.profile_footer),
                color = Hint,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            )
        }
    }
}

@Composable
private fun StatTile(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(White20)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = emoji, fontSize = 20.sp)
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(text = label, color = White80, fontSize = 12.sp)
    }
}

@Composable
private fun OptionsCard(
    options: List<ProfileOption>,
    onOptionClick: ((ProfileOption) -> Unit)? = null
) {
    AppCard(Modifier.fillMaxWidth()) {
        Column {
            options.forEachIndexed { index, option ->
                if (index > 0) HorizontalDivider(color = DividerGray, modifier = Modifier.padding(start = 72.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOptionClick?.invoke(option) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconTile(icon = option.icon, iconTint = option.iconTint, background = option.tile)
                    Column(Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(
                            text = stringResource(option.title),
                            color = Ink,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(option.subtitle),
                            color = Muted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Icon(
                        painter = painterResource(R.drawable.ic_chevron_right),
                        contentDescription = null,
                        tint = Hint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
