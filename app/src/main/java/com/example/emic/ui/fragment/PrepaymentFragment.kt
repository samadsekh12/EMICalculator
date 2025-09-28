package com.example.emic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.emic.databinding.FragmentPrepaymentBinding
import com.example.emic.ui.adapter.ScheduleAdapter
import com.example.emic.ui.viewmodel.LoanViewModel

class PrepaymentFragment : Fragment() {
    private var _binding: FragmentPrepaymentBinding? = null
    private val binding get() = _binding!!
    private val vm: LoanViewModel by activityViewModels()
    private lateinit var adapter: ScheduleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPrepaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ScheduleAdapter()
        binding.rvPrepaySchedule.adapter = adapter

        binding.etExtraPayment.setText(vm.extraPayment.value?.toString() ?: "0.0")
        binding.rbReduceTenure.isChecked = vm.prepayMode.value == "REDUCE_TENURE"
        binding.rbReduceEmi.isChecked = vm.prepayMode.value == "REDUCE_EMI"

        binding.btnSimulate.setOnClickListener {
            vm.extraPayment.value = binding.etExtraPayment.text.toString().toDoubleOrNull() ?: 0.0
            vm.prepayMode.value = if (binding.rbReduceTenure.isChecked) "REDUCE_TENURE" else "REDUCE_EMI"
        }

        vm.prepaymentResult.observe(viewLifecycleOwner) { res ->
            adapter.submitList(res.schedule)
            binding.tvNewEmi.text = String.format("New EMI: ₹%.2f", res.newEmi)
            binding.tvNewTenure.text = "Months: ${res.newTenureMonths}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
