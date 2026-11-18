package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.FragmentHomeBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.SortOption
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.RestaurantAdapter
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.infoRes
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.listTitleRes
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.MainActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.RestaurantsViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.applySystemBarsInsets

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RestaurantsViewModel by activityViewModels()

    private val adapter = RestaurantAdapter { restaurant ->
        // Semana 7: aquí se abrirá el menú del restaurante
        Toast.makeText(requireContext(), getString(R.string.menu_coming_soon, restaurant.name), Toast.LENGTH_SHORT).show()
    }

    private val chipIds = mutableMapOf<SortOption, Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //la cabecera naranja baja del notch / barra de estado
        binding.header.applySystemBarsInsets(bottom = false)

        binding.rvRestaurants.adapter = adapter
        setupSortChips()

        val main = requireActivity() as MainActivity
        binding.searchBar.setOnClickListener { main.selectTab(R.id.nav_search) }
        binding.btnFilters.setOnClickListener { main.openFilters() }
        binding.tvSeeAll.setOnClickListener { main.selectTab(R.id.nav_search) }
        binding.btnPromo.setOnClickListener { viewModel.applyTopRatedPromo() }
        binding.tvEmptyClear.setOnClickListener { viewModel.clearFilters() }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.tvLocation.setText(
                if (state.location.isFromDevice) R.string.location_device_label else R.string.location_demo_label
            )
            chipIds[state.filters.sort]?.let { binding.chipsSort.check(it) }
            binding.tvSortInfo.setText(state.filters.sort.infoRes)
            binding.tvListTitle.setText(state.filters.sort.listTitleRes)

            adapter.submitList(state.nearby)
            val empty = state.nearby.isEmpty()
            binding.emptyState.isVisible = empty
            binding.rvRestaurants.isVisible = !empty
        }
    }

    /** Chips: Todos, Cercanos, Mejor nota, Rápido. */
    private fun setupSortChips() {
        val options = listOf(
            SortOption.RECOMMENDED to R.string.chip_all,
            SortOption.NEAREST to R.string.chip_nearby,
            SortOption.BEST_RATED to R.string.chip_best,
            SortOption.FASTEST to R.string.chip_fast
        )
        options.forEach { (option, label) ->
            val chip = layoutInflater.inflate(R.layout.item_chip_filter, binding.chipsSort, false) as Chip
            chip.id = View.generateViewId()
            chip.setText(label)
            chipIds[option] = chip.id
            binding.chipsSort.addView(chip)
        }
        binding.chipsSort.setOnCheckedStateChangeListener { _, checkedIds ->
            val id = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            chipIds.entries.firstOrNull { it.value == id }?.key?.let { viewModel.setSort(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
