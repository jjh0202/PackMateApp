package com.example.packmateapp.ui.packing

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.packmateapp.R
import com.example.packmateapp.data.entity.PackingItemEntity
import com.example.packmateapp.databinding.ItemPackingBinding
import com.example.packmateapp.model.ItemCategory

class PackingAdapter(
    private val onCheckedChange: (PackingItemEntity, Boolean) -> Unit,
    private val onItemLongClick: (PackingItemEntity) -> Unit
) : ListAdapter<PackingItemEntity, PackingAdapter.PackingViewHolder>(DiffCallback) {

    inner class PackingViewHolder(private val binding: ItemPackingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PackingItemEntity) {
            val context = binding.root.context

            binding.tvItemName.text = if (item.quantity >= 2) {
                context.getString(R.string.item_name_quantity_format, item.name, item.quantity)
            } else {
                item.name
            }
            binding.tvCategory.text = ItemCategory.fromName(item.category).label

            // 챙긴 짐은 취소선 + 회색
            val textColor = ContextCompat.getColor(
                context,
                if (item.isPacked) R.color.text_packed else R.color.text_primary
            )
            binding.tvItemName.setTextColor(textColor)
            binding.tvItemName.paintFlags = if (item.isPacked) {
                binding.tvItemName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.tvItemName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            // 재사용되는 ViewHolder에서 이전 리스너가 불리지 않도록 먼저 제거
            binding.cbPacked.setOnCheckedChangeListener(null)
            binding.cbPacked.isChecked = item.isPacked
            binding.cbPacked.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(item, isChecked)
            }

            binding.root.setOnClickListener { binding.cbPacked.toggle() }
            binding.root.setOnLongClickListener {
                onItemLongClick(item)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackingViewHolder {
        val binding = ItemPackingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PackingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PackingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object DiffCallback : DiffUtil.ItemCallback<PackingItemEntity>() {
        override fun areItemsTheSame(oldItem: PackingItemEntity, newItem: PackingItemEntity) =
            oldItem.itemId == newItem.itemId

        override fun areContentsTheSame(oldItem: PackingItemEntity, newItem: PackingItemEntity) =
            oldItem == newItem
    }
}
