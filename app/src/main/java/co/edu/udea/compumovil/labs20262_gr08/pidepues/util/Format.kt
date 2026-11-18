package co.edu.udea.compumovil.labs20262_gr08.pidepues.util

import java.text.NumberFormat
import java.util.Locale

private val COP: NumberFormat = NumberFormat.getInstance(Locale.forLanguageTag("es-CO"))

/** 28000 -> "$ 28.000" */
fun Int.toCop(): String = "$ ${COP.format(this)}"

// Convierte un número entero a texto en formato $ COP
fun Int.toCopNumber(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    return format.format(this)
}
