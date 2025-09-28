package com.example.emic.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.emic.databinding.ItemScheduleRowBinding
import com.example.emic.util.AmortizationRow

class ScheduleAdapter : ListAdapter<AmortizationRow, ScheduleAdapter.VH>(
    object : DiffUtil.ItemCallback<AmortizationRow>() {
        override fun areItemsTheSame(oldItem: AmortizationRow, newItem: AmortizationRow) =
            oldItem.month == newItem.month
        override fun areContentsTheSame(oldItem: AmortizationRow, newItem: AmortizationRow) =
            oldItem == newItem
    }
) {
    inner class VH(val binding: ItemScheduleRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemScheduleRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val row = getItem(position)
        with(holder.binding) {
            tvMonth.text = "Month ${row.month}"
            tvPayment.text = String.format("Payment ₹%.2f", row.payment)
            tvPrincipal.text = String.format("Principal ₹%.2f", row.principalComponent)
            tvInterest.text = String.format("Interest ₹%.2f", row.interestComponent)
            tvBalance.text = String.format("Balance ₹%.2f", row.remainingPrincipal)
        }
    }
}
