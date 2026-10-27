package com.example.packmateapp.ui.packing

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.packmateapp.R
import com.example.packmateapp.data.PackRepository
import com.example.packmateapp.data.entity.PackingItemEntity
import com.example.packmateapp.data.entity.TripEntity
import com.example.packmateapp.databinding.FragmentPackingListBinding
import com.example.packmateapp.util.DateUtils
import com.example.packmateapp.util.NavKeys
import kotlinx.coroutines.launch

class PackingListFragment : Fragment(R.layout.fragment_packing_list) {

    private var _binding: FragmentPackingListBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: PackRepository
    private lateinit var adapter: PackingAdapter

    private var tripId = -1L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPackingListBinding.bind(view)
        repository = PackRepository.getInstance(requireContext())
        tripId = arguments?.getLong(NavKeys.TRIP_ID, -1L) ?: -1L

        binding.header.btnBack.setOnClickListener { findNavController().popBackStack() }

        adapter = PackingAdapter(
            onCheckedChange = { item, isPacked -> onItemChecked(item, isPacked) },
            onItemLongClick = { item -> deleteItem(item) }
        )
        binding.recyclerPacking.adapter = adapter

        binding.btnAddItem.setOnClickListener {
            val bundle = Bundle().apply { putLong(NavKeys.TRIP_ID, tripId) }
            findNavController().navigate(R.id.action_packingListFragment_to_itemAddFragment, bundle)
        }

        binding.btnLoadDefault.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val added = repository.loadDefaultItems(tripId)
                val message = if (added > 0) {
                    getString(R.string.toast_default_loaded, added)
                } else {
                    getString(R.string.toast_default_none)
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                refreshItems()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val trip = repository.getTrip(tripId)
            if (trip == null) {
                findNavController().popBackStack()
                return@launch
            }
            showTripInfo(trip)
            // 이미 다 챙긴 상태로 들어와도 완료 화면으로 자동 이동하지 않는다
            refreshItems()
        }
    }

    private fun showTripInfo(trip: TripEntity) {
        binding.header.tvHeaderTitle.text = trip.name
        val dDay = DateUtils.dDayText(requireContext(), trip.startDate)
        val text = getString(
            R.string.trip_schedule_format,
            DateUtils.toMonthDay(trip.startDate),
            trip.days,
            dDay
        )
        // D-day 부분만 굵게
        val spannable = SpannableString(text)
        val start = text.lastIndexOf(dDay)
        if (dDay.isNotEmpty() && start >= 0) {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                start + dDay.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        binding.tvTripInfo.text = spannable
    }

    // 목록과 진행률을 DB에서 다시 읽는다. 전체 1개 이상이고 모두 챙겼으면 true
    private suspend fun refreshItems(): Boolean {
        val items = repository.getItems(tripId)
        val total = repository.countAll(tripId)
        val packed = repository.countPacked(tripId)

        adapter.submitList(items)
        binding.tvEmpty.isVisible = items.isEmpty()

        val percent = if (total > 0) packed * 100 / total else 0
        binding.progressPacked.max = total.coerceAtLeast(1)
        binding.progressPacked.progress = packed
        binding.tvProgress.text = getString(R.string.progress_format, packed, total, percent)

        return total > 0 && packed == total
    }

    private fun onItemChecked(item: PackingItemEntity, isPacked: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.setPacked(item.itemId, isPacked)
            val allPacked = refreshItems()
            // 마지막 짐을 체크해서 전부 챙겨졌을 때만 완료 화면으로 이동
            if (isPacked && allPacked &&
                findNavController().currentDestination?.id == R.id.packingListFragment
            ) {
                val bundle = Bundle().apply { putLong(NavKeys.TRIP_ID, tripId) }
                findNavController().navigate(R.id.action_packingListFragment_to_packCompleteFragment, bundle)
            }
        }
    }

    private fun deleteItem(item: PackingItemEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.deleteItem(item)
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_item_deleted, item.name),
                Toast.LENGTH_SHORT
            ).show()
            refreshItems()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
