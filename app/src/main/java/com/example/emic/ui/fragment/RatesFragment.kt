package com.example.emic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.emic.data.model.BankRate
import com.example.emic.databinding.FragmentRatesBinding
import com.example.emic.ui.viewmodel.LoanViewModel

class RatesFragment : Fragment() {
    private var _binding: FragmentRatesBinding? = null
    private val binding get() = _binding!!
    private val vm: LoanViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRatesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val adapter = object : androidx.recyclerview.widget.ListAdapter<BankRate, androidx.recyclerview.widget.RecyclerView.ViewHolder>(
            object : androidx.recyclerview.widget.DiffUtil.ItemCallback<BankRate>() {
                override fun areItemsTheSame(oldItem: BankRate, newItem: BankRate) = oldItem.id == newItem.id
                override fun areContentsTheSame(oldItem: BankRate, newItem: BankRate) = oldItem == newItem
            }
        ) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
                val v = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_2, parent, false)
                return object : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {}
            }
            override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
                val item = getItem(position)
                holder.itemView.findViewById<android.widget.TextView>(android.R.id.text1).text = item.bankName
                holder.itemView.findViewById<android.widget.TextView>(android.R.id.text2).text = "${item.annualInterestRate}%"
                holder.itemView.setOnLongClickListener {
                    vm.deleteBankRate(item.id)
                    true
                }
                holder.itemView.setOnClickListener {
                    vm.annualRate.value = item.annualInterestRate
                }
            }
        }
        binding.rvRates.adapter = adapter

        vm.bankRates.observe(viewLifecycleOwner) { list -> adapter.submitList(list) }

        binding.btnAddRate.setOnClickListener {
            val name = binding.etBankName.text.toString().trim()
            val rate = binding.etBankRate.text.toString().toDoubleOrNull()
            if (name.isNotEmpty() && rate != null) {
                vm.upsertBankRate(name, rate)
                binding.etBankName.setText("")
                binding.etBankRate.setText("")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
