package co.edu.udea.compumovil.labs20262_gr08.pidepues.util

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/*sumaajusta espacio de padding, margins y demas para la vista*/
fun View.applySystemBarsInsets(top: Boolean = true, bottom: Boolean = true) {
    val initialLeft = paddingLeft
    val initialTop = paddingTop
    val initialRight = paddingRight
    val initialBottom = paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(
            WindowInsetsCompat.Type.systemBars() or
                WindowInsetsCompat.Type.displayCutout() or
                WindowInsetsCompat.Type.ime()
        )
        view.setPadding(
            initialLeft + bars.left,
            initialTop + if (top) bars.top else 0,
            initialRight + bars.right,
            initialBottom + if (bottom) bars.bottom else 0
        )
        insets
    }
}
