package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.results

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityResultsBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ItemResultRestaurantCardBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.LayoutSortPopupBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.filter.FilterActivity

class ResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding
    private var currentSortOption = "Recomendados"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFormulaText()
        setupResultsData()
        setupListeners()
    }

    private fun setupFormulaText() {
        val formulaHtml = "Puntuación = <font color='#FF5722'><b>40% cercanía</b></font> + <font color='#FFB300'><b>60% calificación</b></font>"
        binding.tvAlgorithmFormula.text = Html.fromHtml(formulaHtml, Html.FROM_HTML_MODE_LEGACY)
    }

    private fun setupResultsData() {
        binding.tvResultsCount.text = "6 restaurantes"

        // Restaurante 1 (#1 Recomendado)
        configureCard(
            binding.cardResult1,
            name = "El Rincón Paisa",
            cuisine = "Comida Colombiana",
            details = "⭐ 4.8 (1,247)  ·  🕒 25 min  ·  📍 0.4 km",
            percentage = "94%",
            isTopRecommended = true
        )

        // Restaurante 2
        configureCard(
            binding.cardResult2,
            name = "Burger Bros",
            cuisine = "Hamburguesas Artesanales",
            details = "⭐ 4.6 (892)  ·  🕒 30 min  ·  📍 0.7 km",
            percentage = "90%",
            isTopRecommended = false
        )

        // Restaurante 3
        configureCard(
            binding.cardResult3,
            name = "Sushi Nikkei",
            cuisine = "Japonés · Peruano",
            details = "⭐ 4.9 (634)  ·  🕒 40 min  ·  📍 1.2 km",
            percentage = "89%",
            isTopRecommended = false
        )

        // Restaurante 4
        configureCard(
            binding.cardResult4,
            name = "Green Garden",
            cuisine = "Saludable · Vegano",
            details = "⭐ 4.7 (445)  ·  🕒 20 min  ·  📍 0.9 km",
            percentage = "89%",
            isTopRecommended = false
        )

        // Restaurante 5
        configureCard(
            binding.cardResult5,
            name = "Pizza Napoletana",
            cuisine = "Pizza Italiana",
            details = "⭐ 4.5 (1,089)  ·  🕒 35 min  ·  📍 1.5 km",
            percentage = "82%",
            isTopRecommended = false
        )
    }

    private fun configureCard(
        cardInclude: ItemResultRestaurantCardBinding,
        name: String,
        cuisine: String,
        details: String,
        percentage: String,
        isTopRecommended: Boolean
    ) {
        cardInclude.tvRestaurantName.text = name
        cardInclude.tvCuisineType.text = cuisine
        cardInclude.tvRatingAndDetails.text = details
        cardInclude.tvMatchPercentage.text = percentage

        if (isTopRecommended) {
            cardInclude.tvBadgeTopRecommended.visibility = View.VISIBLE
        } else {
            cardInclude.tvBadgeTopRecommended.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnFilter.setOnClickListener {
            startActivity(Intent(this, FilterActivity::class.java))
        }

        binding.btnSortSelector.setOnClickListener { anchorView ->
            showSortPopupWindow(anchorView)
        }
    }

    private fun showSortPopupWindow(anchorView: View) {
        val popupBinding = LayoutSortPopupBinding.inflate(LayoutInflater.from(this))
        val popupWindow = PopupWindow(
            popupBinding.root,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.elevation = 16f

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val inkColor = ContextCompat.getColor(this, R.color.ink)

        // Actualizar el estado visual de cada opción según currentSortOption
        fun updateSortOptionsState() {
            // Opción 1: Recomendados
            if (currentSortOption == "Recomendados") {
                popupBinding.tvTextRecomendados.setTextColor(primaryColor)
                popupBinding.tvTextRecomendados.setTypeface(null, Typeface.BOLD)
                popupBinding.ivCheckRecomendados.visibility = View.VISIBLE
            } else {
                popupBinding.tvTextRecomendados.setTextColor(inkColor)
                popupBinding.tvTextRecomendados.setTypeface(null, Typeface.NORMAL)
                popupBinding.ivCheckRecomendados.visibility = View.GONE
            }

            // Opción 2: Más cercano
            if (currentSortOption == "Más cercano") {
                popupBinding.tvTextCercano.setTextColor(primaryColor)
                popupBinding.tvTextCercano.setTypeface(null, Typeface.BOLD)
                popupBinding.ivCheckCercano.visibility = View.VISIBLE
            } else {
                popupBinding.tvTextCercano.setTextColor(inkColor)
                popupBinding.tvTextCercano.setTypeface(null, Typeface.NORMAL)
                popupBinding.ivCheckCercano.visibility = View.GONE
            }

            // Opción 3: Mejor calificación
            if (currentSortOption == "Mejor calificación") {
                popupBinding.tvTextMejorNota.setTextColor(primaryColor)
                popupBinding.tvTextMejorNota.setTypeface(null, Typeface.BOLD)
                popupBinding.ivCheckMejorNota.visibility = View.VISIBLE
            } else {
                popupBinding.tvTextMejorNota.setTextColor(inkColor)
                popupBinding.tvTextMejorNota.setTypeface(null, Typeface.NORMAL)
                popupBinding.ivCheckMejorNota.visibility = View.GONE
            }
        }

        updateSortOptionsState()

        popupBinding.popOptRecomendados.setOnClickListener {
            currentSortOption = "Recomendados"
            binding.tvSelectedSortText.text = currentSortOption
            popupWindow.dismiss()
        }

        popupBinding.popOptCercano.setOnClickListener {
            currentSortOption = "Más cercano"
            binding.tvSelectedSortText.text = currentSortOption
            popupWindow.dismiss()
        }

        popupBinding.popOptMejorNota.setOnClickListener {
            currentSortOption = "Mejor calificación"
            binding.tvSelectedSortText.text = currentSortOption
            popupWindow.dismiss()
        }

        popupWindow.showAsDropDown(anchorView, 0, 10)
    }
}