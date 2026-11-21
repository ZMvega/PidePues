package co.edu.udea.compumovil.labs20262_gr08.pidepues.util

import java.text.NumberFormat
import java.util.Locale

private val COP: NumberFormat = NumberFormat.getInstance(Locale.forLanguageTag("es-CO"))

/** 28000 -> "$ 28.000" */

fun Int.toCopNumber(): String = COP.format(this)

/** 28000 -> "$ 28.000" */
fun Int.toCop(): String = "$ " + toCopNumber()

private val DATE_TIME = java.text.SimpleDateFormat("d MMM · h:mm a", Locale.forLanguageTag("es-CO"))
private val TIME_ONLY = java.text.SimpleDateFormat("h:mm a", Locale.forLanguageTag("es-CO"))

/** "15 nov · 2:45 p. m." */
fun formatOrderDate(millis: Long): String = DATE_TIME.format(java.util.Date(millis))

/** "2:45 p. m." */
fun formatOrderTime(millis: Long): String = TIME_ONLY.format(java.util.Date(millis))

