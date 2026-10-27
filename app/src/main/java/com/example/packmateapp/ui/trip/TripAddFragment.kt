package com.example.packmateapp.ui.trip

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.packmateapp.R
import com.example.packmateapp.data.PackRepository
import com.example.packmateapp.data.entity.TripEntity
import com.example.packmateapp.databinding.FragmentTripAddBinding
import com.example.packmateapp.databinding.ItemEmojiOptionBinding
import com.example.packmateapp.util.DateUtils
import kotlinx.coroutines.launch
import java.util.Calendar

class TripAddFragment : Fragment(R.layout.fragment_trip_add) {

    private var _binding: FragmentTripAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: PackRepository

    private var startDate = ""
    private var selectedEmoji = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTripAddBinding.bind(view)
        repository = PackRepository.getInstance(requireContext())

        binding.header.tvHeaderTitle.setText(R.string.title_trip_add)
        binding.header.btnBack.setOnClickListener { findNavController().popBackStack() }

        val emojis = resources.getStringArray(R.array.trip_emojis)
        startDate = savedInstanceState?.getString(KEY_START_DATE).orEmpty()
        selectedEmoji = savedInstanceState?.getString(KEY_EMOJI) ?: emojis.first()

        setupStartDate()
        setupDaysSpinner()
        setupEmojiOptions(emojis)

        binding.btnSave.setOnClickListener {
            val name = binding.etTripName.text.toString().trim()
            val place = binding.etPlace.text.toString().trim()
            if (name.isEmpty()) {
                binding.etTripName.error = getString(R.string.error_trip_name_empty)
                binding.etTripName.requestFocus()
                return@setOnClickListener
            }
            if (place.isEmpty()) {
                binding.etPlace.error = getString(R.string.error_place_empty)
                binding.etPlace.requestFocus()
                return@setOnClickListener
            }
            if (startDate.isEmpty()) {
                binding.etStartDate.error = getString(R.string.error_start_date_empty)
                Toast.makeText(requireContext(), R.string.error_start_date_empty, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val trip = TripEntity(
                name = name,
                place = place,
                startDate = startDate,
                days = binding.spinnerDays.selectedItemPosition + 1,
                emoji = selectedEmoji
            )
            binding.btnSave.isEnabled = false
            viewLifecycleOwner.lifecycleScope.launch {
                repository.addTrip(trip)
                Toast.makeText(requireContext(), R.string.toast_trip_added, Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupStartDate() {
        binding.etStartDate.setText(startDate)
        binding.etStartDate.setOnClickListener {
            // 이름·장소 입력 중 열린 키보드를 닫고 달력을 띄운다
            WindowCompat.getInsetsController(requireActivity().window, binding.root)
                .hide(WindowInsetsCompat.Type.ime())
            val calendar = DateUtils.toCalendar(startDate) ?: Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    startDate = DateUtils.toDbString(year, month, dayOfMonth)
                    binding.etStartDate.setText(startDate)
                    binding.etStartDate.error = null
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupDaysSpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.trip_days,
            R.layout.item_spinner
        )
        adapter.setDropDownViewResource(R.layout.item_spinner)
        binding.spinnerDays.adapter = adapter
        if (binding.spinnerDays.selectedItemPosition <= 0) {
            binding.spinnerDays.setSelection(DEFAULT_DAYS_POSITION)
        }
    }

    private fun setupEmojiOptions(emojis: Array<String>) {
        val optionViews = emojis.map { emoji ->
            ItemEmojiOptionBinding.inflate(layoutInflater, binding.layoutEmojis, true).root.apply {
                text = emoji
                contentDescription = emoji
            }
        }
        optionViews.forEach { option ->
            option.isSelected = option.text == selectedEmoji
            option.setOnClickListener {
                selectedEmoji = option.text.toString()
                optionViews.forEach { it.isSelected = it == option }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_START_DATE, startDate)
        outState.putString(KEY_EMOJI, selectedEmoji)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_START_DATE = "start_date"
        private const val KEY_EMOJI = "emoji"
        private const val DEFAULT_DAYS_POSITION = 2 // 3일
    }
}
