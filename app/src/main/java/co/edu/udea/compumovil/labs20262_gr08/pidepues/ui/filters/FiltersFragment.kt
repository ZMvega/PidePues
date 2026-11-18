package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.filters


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.FragmentFiltersBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ItemSortOptionBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Category
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.RestaurantFilters
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.SortOption
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.emoji
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.subtitleRes
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.titleRes
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.RestaurantsViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.applySystemBarsInsets

/**
 * Pantalla de filtros. Trabaja sobre un draft: los cambios solo se
 * aplican al presionar "Aplicar filtros"; la flecha atrás los descarta.
 */
class FiltersFragment : Fragment() {

    private var _binding: FragmentFiltersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RestaurantsViewModel by activityViewModels()

    private var draft = RestaurantFilters()
    private val sortRows = mutableMapOf<SortOption, ItemSortOptionBinding>()
    private val ratingChipIds = mutableMapOf<Float, Int>()
    private val cuisineChips = mutableMapOf<Category, Chip>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.topBar.applySystemBarsInsets(bottom = false)
        binding.applyBar.applySystemBarsInsets(top = false)

        draft = viewModel.state.value!!.filters

        setupSortRows()
        setupDistanceSlider()
        setupRatingChips()
        setupCuisineChips()

        binding.btnBack.setOnClickListener { close() }
        binding.tvClear.setOnClickListener {
            draft = RestaurantFilters()
            renderAll()
        }
        binding.btnApply.setOnClickListener {
            viewModel.setFilters(draft)
            close()
        }
    }

    private fun close() = parentFragmentManager.popBackStack()

    //ordenar por
    private fun setupSortRows() {
        val rows = mapOf(
            SortOption.RECOMMENDED to binding.sortRecommended,
            SortOption.NEAREST to binding.sortNearest,
            SortOption.BEST_RATED to binding.sortBest,
            SortOption.FASTEST to binding.sortFastest
        )
        rows.forEach { (option, row) ->
            row.tvEmoji.text = option.emoji
            row.tvTitle.setText(option.titleRes)
            row.tvSubtitle.setText(option.subtitleRes)
            row.root.setOnClickListener {
                draft = draft.copy(sort = option)
                renderSort()
            }
            sortRows[option] = row
        }
        renderSort()
    }

    private fun renderSort() {
        sortRows.forEach { (option, row) ->
            val selected = option == draft.sort
            row.root.isSelected = selected
            row.rb.isChecked = selected
        }
    }

    //distancia maxima
    private fun setupDistanceSlider() {
        binding.sliderDistance.value = draft.maxDistanceKm.coerceIn(0.5f, 10f)
        binding.tvDistanceValue.text = formatKm(binding.sliderDistance.value)
        binding.sliderDistance.addOnChangeListener { _, value, fromUser ->
            if (fromUser) draft = draft.copy(maxDistanceKm = value)
            binding.tvDistanceValue.text = formatKm(value)
        }
    }

    private fun formatKm(value: Float): String =
        if (value < 1f) "${(value * 1000).toInt()} m"
        else if (value % 1f == 0f) "${value.toInt()} km"
        else "$value km"

    //calificación mínima
    private fun setupRatingChips() {
        listOf(0f, 3f, 3.5f, 4f, 4.5f).forEach { rating ->
            val chip = newChip()
            if (rating == 0f) chip.setText(R.string.rating_all)
            else chip.text = (if (rating % 1f == 0f) "${rating.toInt()}+" else "$rating+")
            ratingChipIds[rating] = chip.id
            binding.chipsRating.addView(chip)
        }
        binding.chipsRating.check(ratingChipIds[draft.minRating] ?: ratingChipIds.getValue(0f))
        binding.chipsRating.setOnCheckedStateChangeListener { _, checkedIds ->
            val id = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            ratingChipIds.entries.firstOrNull { it.value == id }?.key?.let {
                draft = draft.copy(minRating = it)
            }
        }
    }

    // tipo de cocina
    private fun setupCuisineChips() {
        Category.values().forEach { category ->
            val chip = newChip()
            chip.text = "${category.emoji} ${category.label}"
            chip.isChecked = category in draft.categories
            cuisineChips[category] = chip
            binding.chipsCuisine.addView(chip)
        }
        binding.chipsCuisine.setOnCheckedStateChangeListener { _, checkedIds ->
            val selected = cuisineChips.filterValues { it.id in checkedIds }.keys
            draft = draft.copy(categories = selected.toSet())
        }
    }

    private fun newChip(): Chip {
        val chip = layoutInflater.inflate(R.layout.item_chip_filter, binding.chipsRating, false) as Chip
        chip.id = View.generateViewId()
        return chip
    }

    /** Vuelve a "Limpiar". */
    private fun renderAll() {
        renderSort()
        binding.sliderDistance.value = draft.maxDistanceKm.coerceIn(0.5f, 10f)
        binding.chipsRating.check(ratingChipIds[draft.minRating] ?: ratingChipIds.getValue(0f))
        binding.chipsCuisine.clearCheck()
        cuisineChips.forEach { (category, chip) -> chip.isChecked = category in draft.categories }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}