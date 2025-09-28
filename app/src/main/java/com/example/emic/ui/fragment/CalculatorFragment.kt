package com.example.emic.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.emic.databinding.FragmentCalculatorBinding
import com.example.emic.ui.adapter.ScheduleAdapter
import com.example.emic.ui.viewmodel.LoanViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class CalculatorFragment : Fragment() {
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private val vm: LoanViewModel by activityViewModels()
    private lateinit var adapter: ScheduleAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = ScheduleAdapter()
        binding.rvSchedule.adapter = adapter

        binding.etPrincipal.setText(vm.principal.value?.toString() ?: "")
        binding.etRate.setText(vm.annualRate.value?.toString() ?: "")
        binding.etTenure.setText(vm.tenureMonths.value?.toString() ?: "")

        binding.btnCalculate.setOnClickListener {
            vm.principal.value = binding.etPrincipal.text.toString().toDoubleOrNull() ?: 0.0
            vm.annualRate.value = binding.etRate.text.toString().toDoubleOrNull() ?: 0.0
            vm.tenureMonths.value = binding.etTenure.text.toString().toIntOrNull() ?: 0
        }

        binding.btnSaveLoan.setOnClickListener {
            val title = binding.etTitle.text.toString().ifBlank { "Loan ${System.currentTimeMillis()}" }
            val note = binding.etNote.text.toString()
            vm.saveLoan(title, note)
        }

        vm.emiResult.observe(viewLifecycleOwner) { res ->
            binding.tvEmi.text = String.format("EMI: ₹%.2f", res.emi)
            binding.tvTotalInterest.text = String.format("Interest: ₹%.2f", res.totalInterest)
            binding.tvTotalPayment.text = String.format("Total: ₹%.2f", res.totalPayment)
            renderPie(res)
        }

        vm.schedule.observe(viewLifecycleOwner) { rows ->
            adapter.submitList(rows)
        }
    }

    private fun renderPie(res: com.example.emic.util.EMIResult) {
        val entries = listOf(
            PieEntry(res.totalInterest.toFloat(), "Interest"),
            PieEntry((res.totalPayment - res.totalInterest).toFloat(), "Principal")
        )
        val dataSet = PieDataSet(entries, "Breakdown").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList()
            valueTextSize = 12f
        }
        binding.pieChart.data = PieData(dataSet)
        binding.pieChart.description.isEnabled = false
        binding.pieChart.centerText = "EMI"
        binding.pieChart.animateY(800)
        binding.pieChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
