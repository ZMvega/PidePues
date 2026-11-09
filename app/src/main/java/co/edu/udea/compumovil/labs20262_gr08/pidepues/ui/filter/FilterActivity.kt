package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.filter

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ActivityFilterBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.results.ResultsActivity

class FilterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterBinding
    private lateinit var ratingChips: List<TextView>
    private lateinit var priceChips: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSortOptions()
        setupDistanceSlider()
        setupRatingChips()
        setupPriceChips()
        setupActions()
    }

    private fun setupSortOptions() {
        val sortContainerMap = mapOf(
            binding.optRecomendados to binding.rbRecomendados,
            binding.optCercano to binding.rbCercano,
            binding.optMejorNota to binding.rbMejorNota,
            binding.optMasRapido to binding.rbMasRapido
        )

        sortContainerMap.forEach { (container, radioButton) ->
            container.setOnClickListener {
                selectSortOption(container, radioButton, sortContainerMap)
            }
        }
    }

    private fun selectSortOption(
        selectedContainer: LinearLayout,
        selectedRadio: RadioButton,
        allMap: Map<LinearLayout, RadioButton>
    ) {
        val bgSelected = Color.parseColor("#FFF0EC")
        val bgUnselected = Color.parseColor("#F8F9FA")

        allMap.forEach { (container, radioButton) ->
            if (container == selectedContainer) {
                container.backgroundTintList = ColorStateList.valueOf(bgSelected)
                radioButton.isChecked = true
            } else {
                container.backgroundTintList = ColorStateList.valueOf(bgUnselected)
                radioButton.isChecked = false
            }
        }
    }

    private fun setupDistanceSlider() {
        binding.sliderDistance.addOnChangeListener { _, value, _ ->
            val formatted = if (value < 1.0) {
                "${(value * 1000).toInt()} m"
            } else {
                if (value % 1 == 0f) "${value.toInt()} km" else "$value km"
            }
            binding.tvDistanceValue.text = formatted
        }
    }

    private fun setupRatingChips() {
        ratingChips = listOf(
            binding.chipRatingTodos,
            binding.chipRating3,
            binding.chipRating35,
            binding.chipRating4,
            binding.chipRating45
        )

        ratingChips.forEach { chip ->
            chip.setOnClickListener { selectedChip ->
                selectSingleChip(selectedChip as TextView, ratingChips)
            }
        }
    }

    private fun setupPriceChips() {
        priceChips = listOf(
            binding.chipPrice1,
            binding.chipPrice2,
            binding.chipPrice3
        )

        priceChips.forEach { chip ->
            chip.setOnClickListener { selectedChip ->
                selectSingleChip(selectedChip as TextView, priceChips)
            }
        }
    }

    private fun selectSingleChip(selected: TextView, group: List<TextView>) {
        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val unselectedBg = Color.parseColor("#F8F9FA")
        val surfaceColor = ContextCompat.getColor(this, R.color.surface)
        val inkColor = ContextCompat.getColor(this, R.color.ink)

        group.forEach { chip ->
            if (chip == selected) {
                chip.backgroundTintList = ColorStateList.valueOf(primaryColor)
                chip.setTextColor(surfaceColor)
            } else {
                chip.backgroundTintList = ColorStateList.valueOf(unselectedBg)
                chip.setTextColor(inkColor)
            }
        }
    }

    private fun setupActions() {
        binding.btnBack.setOnClickListener { finish() }

        // Redirección a la pantalla de Resultados
        binding.btnApplyFilters.setOnClickListener {
            startActivity(Intent(this, ResultsActivity::class.java))
            finish()
        }

        binding.btnClearAll.setOnClickListener {
            binding.optRecomendados.performClick()
            binding.sliderDistance.value = 5.0f
            selectSingleChip(binding.chipRatingTodos, ratingChips)
            selectSingleChip(binding.chipPrice3, priceChips)
            binding.chipGroupCuisine.clearCheck()
        }
    }
}