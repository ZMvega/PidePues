package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.rating

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    var selectedStars by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }

    val primaryOrange = Color(0xFFEA4C1D)
    val starYellow = Color(0xFFFFB800)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp)
    ) {
        // Barra superior
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEDEDED))
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Calificar pedido",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )
        }

        Spacer(Modifier.height(16.dp))

        // Tarjeta resumen del pedido
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFF3EE))
                ) {
                    Text(text = restaurantEmoji, fontSize = 28.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(text = "Tu pedido del $orderDateLabel", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        text = restaurantName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                    Text(text = orderShortCode, fontSize = 13.sp, color = Color.Gray)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Tarjeta de estrellas y comentario
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp).fillMaxWidth()
            ) {
                Text(
                    text = "¿Cómo estuvo tu experiencia?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Toca las estrellas para calificar",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.height(16.dp))

                // Selector de 5 estrellas
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= selectedStars) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = "$i estrellas",
                            tint = if (i <= selectedStars) starYellow else Color(0xFFD0D5DD),
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { selectedStars = i }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

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

        Spacer(Modifier.weight(1f))

        // Botón enviar
        Button(
            onClick = { onSubmit(selectedStars, comment) },
            enabled = selectedStars > 0 && !isSubmitting,
            colors = ButtonDefaults.buttonColors(containerColor = primaryOrange),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().height(54.dp)
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
            } else {
                Text("Enviar calificación", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}