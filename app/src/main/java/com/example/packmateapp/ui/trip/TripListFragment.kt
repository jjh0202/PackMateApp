package com.example.packmateapp.ui.trip

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.packmateapp.R
import com.example.packmateapp.data.PackRepository
import com.example.packmateapp.data.entity.TripEntity
import com.example.packmateapp.databinding.FragmentTripListBinding
import com.example.packmateapp.util.NavKeys
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class TripListFragment : Fragment(R.layout.fragment_trip_list) {

    private var _binding: FragmentTripListBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: PackRepository
    private lateinit var adapter: TripAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTripListBinding.bind(view)
        repository = PackRepository.getInstance(requireContext())

        binding.header.btnBack.visibility = View.GONE
        binding.header.tvHeaderTitle.setText(R.string.title_trip_list)

        adapter = TripAdapter(
            onItemClick = { tripId -> openPackingList(tripId) },
            onItemLongClick = { trip -> confirmDelete(trip) }
        )
        binding.recyclerTrip.adapter = adapter

        binding.fabAddTrip.setOnClickListener {
            findNavController().navigate(R.id.action_tripListFragment_to_tripAddFragment)
        }

        // 다른 화면에서 돌아올 때마다 onViewCreated가 다시 불리므로 여기서 새로 조회
        loadTrips()
    }

    private fun loadTrips() {
        viewLifecycleOwner.lifecycleScope.launch {
            val trips = repository.getTripSummaries()
            adapter.submitList(trips)
            binding.tvEmpty.isVisible = trips.isEmpty()
        }
    }

    private fun openPackingList(tripId: Long) {
        val bundle = Bundle().apply { putLong(NavKeys.TRIP_ID, tripId) }
        findNavController().navigate(R.id.action_tripListFragment_to_packingListFragment, bundle)
    }

    private fun confirmDelete(trip: TripEntity) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_delete_trip_title)
            .setMessage(getString(R.string.dialog_delete_trip_message, trip.name))
            .setNegativeButton(R.string.btn_cancel, null)
            .setPositiveButton(R.string.btn_delete) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.deleteTrip(trip)
                    Toast.makeText(requireContext(), R.string.toast_trip_deleted, Toast.LENGTH_SHORT).show()
                    loadTrips()
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
