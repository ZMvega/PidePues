package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import co.edu.udea.compumovil.labs20262_gr08.pidepues.R
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.FragmentSearchBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.ItemRecentSearchBinding
import co.edu.udea.compumovil.labs20262_gr08.pidepues.model.Category
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.common.RestaurantAdapter
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.MainActivity
import co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.main.RestaurantsViewModel
import co.edu.udea.compumovil.labs20262_gr08.pidepues.util.applySystemBarsInsets

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RestaurantsViewModel by activityViewModels()

    private val categoryAdapter = CategoryAdapter { viewModel.applyCategory(it) }
    private val resultsAdapter = RestaurantAdapter { restaurant ->
        Toast.makeText(requireContext(), getString(R.string.menu_coming_soon, restaurant.name), Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.searchTop.applySystemBarsInsets(bottom = false)

        binding.rvCategories.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.rvCategories.adapter = categoryAdapter
        categoryAdapter.submitList(Category.values().toList())
        binding.rvResults.adapter = resultsAdapter

        // Buscar mientras se escribe; al presionar la lupa del teclado se guarda en recientes
        binding.etSearch.doAfterTextChanged { viewModel.setQuery(it?.toString().orEmpty()) }
        binding.etSearch.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.submitSearch(textView.text.toString())
                WindowCompat.getInsetsController(requireActivity().window, textView)
                    .hide(WindowInsetsCompat.Type.ime())
                true
            } else {
                false
            }
        }

        binding.btnFilters.setOnClickListener { (requireActivity() as MainActivity).openFilters() }
        binding.tvClearRecents.setOnClickListener { viewModel.clearRecents() }
        binding.tvClearResults.setOnClickListener { viewModel.clearAll() }
        binding.tvEmptyClear.setOnClickListener { viewModel.clearAll() }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            //reflejar busqueda si cambio desde fuera (ej. tocar una busqueda reciente)
            if (binding.etSearch.text.toString() != state.query) {
                binding.etSearch.setText(state.query)
                binding.etSearch.setSelection(state.query.length)
            }

            binding.groupDiscover.isVisible = !state.isSearching
            binding.groupResults.isVisible = state.isSearching

            renderRecents(state.recentSearches)

            val count = state.searchResults.size
            resultsAdapter.submitList(state.searchResults)
            binding.tvResultsCount.text = resources.getQuantityString(R.plurals.results_count, count, count)
            binding.emptyState.isVisible = count == 0
            binding.rvResults.isVisible = count > 0
        }
    }

    private fun renderRecents(recents: List<String>) {
        binding.groupRecents.isVisible = recents.isNotEmpty()
        binding.containerRecents.removeAllViews()
        recents.forEach { text ->
            val row = ItemRecentSearchBinding.inflate(layoutInflater, binding.containerRecents, false)
            row.tvText.text = text
            row.root.setOnClickListener { viewModel.selectRecent(text) }
            binding.containerRecents.addView(row.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
