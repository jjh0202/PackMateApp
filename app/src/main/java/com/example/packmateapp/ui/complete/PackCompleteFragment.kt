package com.example.packmateapp.ui.complete

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.packmateapp.R
import com.example.packmateapp.data.PackRepository
import com.example.packmateapp.databinding.FragmentPackCompleteBinding
import com.example.packmateapp.util.DateUtils
import com.example.packmateapp.util.NavKeys
import kotlinx.coroutines.launch

class PackCompleteFragment : Fragment(R.layout.fragment_pack_complete) {

    private var _binding: FragmentPackCompleteBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: PackRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPackCompleteBinding.bind(view)
        repository = PackRepository.getInstance(requireContext())
        val tripId = arguments?.getLong(NavKeys.TRIP_ID, -1L) ?: -1L

        binding.header.btnBack.visibility = View.GONE
        binding.header.tvHeaderTitle.setText(R.string.title_pack_complete)

        viewLifecycleOwner.lifecycleScope.launch {
            val trip = repository.getTrip(tripId) ?: return@launch
            val itemCount = repository.countAll(tripId)
            binding.tvSummaryTitle.text =
                getString(R.string.complete_trip_title_format, trip.emoji, trip.name)
            binding.tvSummaryInfo.text = getString(
                R.string.complete_trip_info_format,
                DateUtils.toMonthDay(trip.startDate),
                trip.days,
                itemCount
            )
        }

        binding.btnBackChecklist.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnGoTripList.setOnClickListener {
            findNavController().popBackStack(R.id.tripListFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
