package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.rating

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.AppCard
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.AppTopBar
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.EmojiTile
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.components.StatusBarAppearance
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.theme.*

@Composable
fun RateOrderScreen(
    restaurantName: String,
    restaurantEmoji: String,
    orderDateLabel: String,
    orderShortCode: String,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onSubmit: (stars: Int, comment: String) -> Unit
) {
    StatusBarAppearance(darkIcons = true)
    var selectedStars by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        // Barra superior estándar de la app con botón de regreso
        AppTopBar(
            title = "Calificar pedido",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Tarjeta resumen del pedido
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    EmojiTile(
                        emoji = restaurantEmoji,
                        background = TileOrange,
                        size = 56.dp,
                        fontSize = 28.sp,
                        cornerRadius = 14.dp
                    )
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(text = "Tu pedido del $orderDateLabel", fontSize = 12.sp, color = Muted)
                        Text(
                            text = restaurantName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ink
                        )
                        Text(text = orderShortCode, fontSize = 13.sp, color = Muted)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tarjeta de estrellas y comentario
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp).fillMaxWidth()
                ) {
                    Text(
                        text = "¿Cómo estuvo tu experiencia?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Toca las estrellas para calificar",
                        fontSize = 14.sp,
                        color = Muted
                    )
                    Spacer(Modifier.height(20.dp))

                    // Selector de 5 estrellas con recurso vectorial ic_star
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in 1..5) {
                            Icon(
                                painter = painterResource(R.drawable.ic_star),
                                contentDescription = "$i estrellas",
                                tint = if (i <= selectedStars) Gold else Color(0xFFD0D5DD),
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable { selectedStars = i }
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Comentario opcional
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { if (it.length <= 280) comment = it },
                        label = { Text("Comentario (opcional)") },
                        placeholder = { Text("Cuéntanos qué te pareció la comida y el servicio...") },
                        minLines = 3,
                        maxLines = 4,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Botón enviar fijo al fondo
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { onSubmit(selectedStars, comment) },
                    enabled = selectedStars > 0 && !isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        disabledContainerColor = PrimaryDisabled
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Enviar calificación", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
