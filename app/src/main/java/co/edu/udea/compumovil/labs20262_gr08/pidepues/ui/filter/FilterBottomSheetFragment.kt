package co.edu.udea.compumovil.labs20262_gr08.pidepues.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import co.edu.udea.compumovil.labs20262_gr08.pidepues.databinding.FragmentFilterBottomSheetBinding

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentFilterBottomSheetBinding? = null
    private val binding get() = _binding!!

    var onFiltersAppliedListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnCloseBottomSheet.setOnClickListener {
            dismiss()
        }

        binding.btnClearFilters.setOnClickListener {
            binding.chipGroupSort.check(binding.chipRelevance.id)
            binding.chipGroupPrice.clearCheck()
            binding.chipGroupPreferences.clearCheck()
        }

        binding.btnApplyFilters.setOnClickListener {
            onFiltersAppliedListener?.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "FilterBottomSheetFragment"
        fun newInstance() = FilterBottomSheetFragment()
    }
}