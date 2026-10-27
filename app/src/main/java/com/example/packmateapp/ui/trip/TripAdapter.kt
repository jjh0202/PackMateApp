package com.example.packmateapp.ui.trip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.packmateapp.R
import com.example.packmateapp.data.TripSummary
import com.example.packmateapp.data.entity.TripEntity
import com.example.packmateapp.databinding.ItemTripBinding
import com.example.packmateapp.util.DateUtils

class TripAdapter(
    private val onItemClick: (Long) -> Unit,
    private val onItemLongClick: (TripEntity) -> Unit
) : ListAdapter<TripSummary, TripAdapter.TripViewHolder>(DiffCallback) {

    inner class TripViewHolder(private val binding: ItemTripBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(summary: TripSummary) {
            val trip = summary.trip
            val context = binding.root.context

            binding.tvEmoji.text = trip.emoji
            binding.tvTripName.text = trip.name
            binding.tvTripInfo.text = context.getString(
                R.string.trip_card_info_format,
                trip.place,
                DateUtils.toMonthDay(trip.startDate),
                trip.days
            )
            binding.progressTrip.max = summary.totalCount.coerceAtLeast(1)
            binding.progressTrip.progress = summary.packedCount
            binding.tvTripProgress.text = context.getString(
                R.string.trip_card_progress_format,
                summary.packedCount,
                summary.totalCount
            )
            binding.tvDday.text = DateUtils.dDayText(context, trip.startDate)

            binding.root.setOnClickListener { onItemClick(trip.tripId) }
            binding.root.setOnLongClickListener {
                onItemLongClick(trip)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object DiffCallback : DiffUtil.ItemCallback<TripSummary>() {
        override fun areItemsTheSame(oldItem: TripSummary, newItem: TripSummary) =
            oldItem.trip.tripId == newItem.trip.tripId

        override fun areContentsTheSame(oldItem: TripSummary, newItem: TripSummary) =
            oldItem == newItem
    }
}
