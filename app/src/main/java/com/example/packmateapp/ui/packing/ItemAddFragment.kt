package com.example.packmateapp.ui.packing

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.packmateapp.R
import com.example.packmateapp.data.PackRepository
import com.example.packmateapp.data.entity.PackingItemEntity
import com.example.packmateapp.databinding.FragmentItemAddBinding
import com.example.packmateapp.model.ItemCategory
import com.example.packmateapp.util.NavKeys
import kotlinx.coroutines.launch

class ItemAddFragment : Fragment(R.layout.fragment_item_add) {

    private var _binding: FragmentItemAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: PackRepository

    private var tripId = -1L
    private var quantity = 1
    private val categories = ItemCategory.entries

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemAddBinding.bind(view)
        repository = PackRepository.getInstance(requireContext())
        tripId = arguments?.getLong(NavKeys.TRIP_ID, -1L) ?: -1L
        quantity = savedInstanceState?.getInt(KEY_QUANTITY) ?: 1

        binding.header.tvHeaderTitle.setText(R.string.title_item_add)
        binding.header.btnBack.setOnClickListener { findNavController().popBackStack() }

        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, categories.map { it.label })
        adapter.setDropDownViewResource(R.layout.item_spinner)
        binding.spinnerCategory.adapter = adapter
        binding.tvCategoryHint.text =
            categories.joinToString(getString(R.string.category_separator)) { it.label }

        binding.tvQuantity.text = quantity.toString()

        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
            }
        }

        binding.btnPlus.setOnClickListener {
            quantity++
            binding.tvQuantity.text = quantity.toString()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etItemName.text.toString().trim()
            if (name.isEmpty()) {
                binding.etItemName.error = getString(R.string.error_item_name_empty)
                binding.etItemName.requestFocus()
                return@setOnClickListener
            }

            val item = PackingItemEntity(
                tripId = tripId,
                name = name,
                category = categories[binding.spinnerCategory.selectedItemPosition].name,
                quantity = quantity
            )
            val saveAsDefault = binding.cbSaveDefault.isChecked
            binding.btnSave.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                repository.addItem(item, saveAsDefault)
                findNavController().popBackStack()
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_QUANTITY, quantity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_QUANTITY = "quantity"
    }
}
