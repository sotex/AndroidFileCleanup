package de.felixnuesse.disky.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import de.felixnuesse.disky.R
import de.felixnuesse.disky.databinding.FilterBottomsheetBinding
import de.felixnuesse.disky.model.FileCategory
import de.felixnuesse.disky.scanner.FilterManager

class FilterBottomSheet(
    private val filterManager: FilterManager,
    private val callback: FilterCallback
) : BottomSheetDialogFragment() {

    private lateinit var binding: FilterBottomsheetBinding

    private val sizeValues = listOf(0L, 1024L * 1024, 10L * 1024L * 1024, 50L * 1024L * 1024, 100L * 1024L * 1024, 500L * 1024L * 1024, 1024L * 1024L * 1024)
    private val durationValues = listOf(0L, 60L * 1000, 5L * 60L * 1000, 10L * 60L * 1000, 30L * 60L * 1000, 60L * 60L * 1000)
    private val resolutionValues = listOf(0, 480 * 640, 720 * 1280, 1080 * 1920, 1440 * 2560, 2160 * 3840)

    private var selectedMinSizeIndex = 0
    private var selectedMaxSizeIndex = 0
    private var selectedMinDurationIndex = 0
    private var selectedMaxDurationIndex = 0
    private var selectedResolutionIndex = 0

    interface FilterCallback {
        fun onFilterApplied(filterManager: FilterManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FilterBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChips()
        setupDropdowns()
        setupButtons()
        loadCurrentFilters()
    }

    private fun setupChips() {
        val chipCategoryMap = mapOf(
            R.id.chip_audio to FileCategory.AUDIO,
            R.id.chip_video to FileCategory.VIDEO,
            R.id.chip_image to FileCategory.IMAGE,
            R.id.chip_document to FileCategory.DOCUMENT,
            R.id.chip_archive to FileCategory.ARCHIVE,
            R.id.chip_apk to FileCategory.APK
        )

        binding.filterTypeChips.setOnCheckedStateChangeListener { _, checkedIds ->
            val selectedCategories = mutableSetOf<FileCategory>()
            checkedIds.forEach { id ->
                chipCategoryMap[id]?.let { selectedCategories.add(it) }
            }
            filterManager.selectedCategories = selectedCategories
        }
    }

    private fun setupDropdowns() {
        setupDropdown(binding.filterSizeMin) { index ->
            selectedMinSizeIndex = index
        }

        setupDropdown(binding.filterSizeMax) { index ->
            selectedMaxSizeIndex = index
        }

        setupDropdown(binding.filterDurationMin) { index ->
            selectedMinDurationIndex = index
        }

        setupDropdown(binding.filterDurationMax) { index ->
            selectedMaxDurationIndex = index
        }

        setupDropdown(binding.filterResolution) { index ->
            selectedResolutionIndex = index
        }
    }

    private fun setupDropdown(dropdown: AutoCompleteTextView, onSelect: (Int) -> Unit) {
        dropdown.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, pos, _ ->
                onSelect(pos)
            }
    }

    private fun setupButtons() {
        binding.filterResetButton.setOnClickListener {
            filterManager.reset()
            loadCurrentFilters()
        }

        binding.filterApplyButton.setOnClickListener {
            applyFilters()
            dismiss()
        }
    }

    private fun loadCurrentFilters() {
        val chipCategoryMap = mapOf(
            FileCategory.AUDIO to R.id.chip_audio,
            FileCategory.VIDEO to R.id.chip_video,
            FileCategory.IMAGE to R.id.chip_image,
            FileCategory.DOCUMENT to R.id.chip_document,
            FileCategory.ARCHIVE to R.id.chip_archive,
            FileCategory.APK to R.id.chip_apk
        )

        binding.filterTypeChips.clearCheck()
        filterManager.selectedCategories.forEach { category ->
            chipCategoryMap[category]?.let { binding.filterTypeChips.check(it) }
        }

        selectedMinSizeIndex = sizeValues.indexOfFirst { it == filterManager.minSize }.takeIf { it >= 0 } ?: 0
        selectedMaxSizeIndex = if (filterManager.maxSize == Long.MAX_VALUE) 0 else sizeValues.indexOfFirst { it >= filterManager.maxSize }.takeIf { it >= 0 } ?: 0
        selectedMinDurationIndex = durationValues.indexOfFirst { it == filterManager.minDuration }.takeIf { it >= 0 } ?: 0
        selectedMaxDurationIndex = if (filterManager.maxDuration == Long.MAX_VALUE) 0 else durationValues.indexOfFirst { it >= filterManager.maxDuration }.takeIf { it >= 0 } ?: 0
        selectedResolutionIndex = resolutionValues.indexOfFirst { it == filterManager.minResolution }.takeIf { it >= 0 } ?: 0

        updateDropdowns()
    }

    private fun updateDropdowns() {
        binding.filterSizeMin.setText(resources.getStringArray(R.array.size_options)[selectedMinSizeIndex], false)
        binding.filterSizeMax.setText(resources.getStringArray(R.array.size_options)[selectedMaxSizeIndex], false)
        binding.filterDurationMin.setText(resources.getStringArray(R.array.duration_options)[selectedMinDurationIndex], false)
        binding.filterDurationMax.setText(resources.getStringArray(R.array.duration_options)[selectedMaxDurationIndex], false)
        binding.filterResolution.setText(resources.getStringArray(R.array.resolution_options)[selectedResolutionIndex], false)
    }

    private fun applyFilters() {
        val minSize = sizeValues[selectedMinSizeIndex]
        val maxSize = if (selectedMaxSizeIndex == 0) Long.MAX_VALUE else sizeValues[selectedMaxSizeIndex]
        filterManager.setSizeRange(minSize, maxSize)

        val minDuration = durationValues[selectedMinDurationIndex]
        val maxDuration = if (selectedMaxDurationIndex == 0) Long.MAX_VALUE else durationValues[selectedMaxDurationIndex]
        filterManager.setDurationRange(minDuration, maxDuration)

        filterManager.applyMinResolution(resolutionValues[selectedResolutionIndex])

        callback.onFilterApplied(filterManager)
    }

    companion object {
        const val TAG = "FilterBottomSheet"
    }
}