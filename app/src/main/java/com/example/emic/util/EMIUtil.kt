package com.example.emic.util

import kotlin.math.pow

data class EMIResult(
    val emi: Double,
    val totalInterest: Double,
    val totalPayment: Double
)

data class AmortizationRow(
    val month: Int,
    val payment: Double,
    val principalComponent: Double,
    val interestComponent: Double,
    val remainingPrincipal: Double
)

object EMIUtil {
    fun calculateEMI(principal: Double, annualRatePercent: Double, tenureMonths: Int): EMIResult {
        val r = annualRatePercent / 12.0 / 100.0
        val emi = if (r == 0.0) principal / tenureMonths
        else principal * r * (1 + r).pow(tenureMonths) / ((1 + r).pow(tenureMonths) - 1)

        val totalPayment = emi * tenureMonths
        val totalInterest = totalPayment - principal
        return EMIResult(emi, totalInterest, totalPayment)
    }

    fun buildSchedule(principal: Double, annualRatePercent: Double, tenureMonths: Int): List<AmortizationRow> {
        val r = annualRatePercent / 12.0 / 100.0
        val emi = calculateEMI(principal, annualRatePercent, tenureMonths).emi
        var balance = principal
        val rows = mutableListOf<AmortizationRow>()
        for (m in 1..tenureMonths) {
            val interest = balance * r
            val principalComponent = (emi - interest).coerceAtLeast(0.0)
            balance = (balance - principalComponent).coerceAtLeast(0.0)
            rows += AmortizationRow(
                month = m,
                payment = emi,
                principalComponent = principalComponent,
                interestComponent = interest,
                remainingPrincipal = balance
            )
            if (balance <= 0.0) break
        }
        return rows
    }

    data class PrepaymentResult(
        val schedule: List<AmortizationRow>,
        val newEmi: Double,
        val newTenureMonths: Int
    )

    fun simulatePrepayment(
        principal: Double,
        annualRatePercent: Double,
        tenureMonths: Int,
        extraPayment: Double,
        mode: String
    ): PrepaymentResult {
        val r = annualRatePercent / 12.0 / 100.0
        val baseEmi = calculateEMI(principal, annualRatePercent, tenureMonths).emi
        var balance = principal
        val rows = mutableListOf<AmortizationRow>()
        var month = 0

        when (mode) {
            "REDUCE_TENURE" -> {
                while (balance > 0 && month < 1200) {
                    month++
                    val interest = balance * r
                    val principalComponent = (baseEmi - interest).coerceAtLeast(0.0) + extraPayment
                    balance = (balance - principalComponent).coerceAtLeast(0.0)
                    rows += AmortizationRow(
                        month = month,
                        payment = baseEmi + extraPayment,
                        principalComponent = principalComponent,
                        interestComponent = interest,
                        remainingPrincipal = balance
                    )
                }
                return PrepaymentResult(rows, baseEmi, month)
            }
            "REDUCE_EMI" -> {
                var remainingMonths = tenureMonths
                while (balance > 0 && remainingMonths > 0 && month < 1200) {
                    month++
                    val interest = balance * r
                    val emiThisMonth = calculateEMI(balance, annualRatePercent, remainingMonths).emi
                    val principalComponent = (emiThisMonth - interest).coerceAtLeast(0.0) + extraPayment
                    balance = (balance - principalComponent).coerceAtLeast(0.0)
                    rows += AmortizationRow(
                        month = month,
                        payment = emiThisMonth + extraPayment,
                        principalComponent = principalComponent,
                        interestComponent = interest,
                        remainingPrincipal = balance
                    )
                    remainingMonths--
                }
                val newEmi = if (balance <= 0.0) 0.0
                else calculateEMI(balance, annualRatePercent, remainingMonths).emi
                return PrepaymentResult(rows, newEmi, month)
            }
            else -> {
                val schedule = buildSchedule(principal, annualRatePercent, tenureMonths)
                return PrepaymentResult(schedule, baseEmi, tenureMonths)
            }
        }
    }
}
