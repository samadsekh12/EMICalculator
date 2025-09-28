package com.example.emic

import com.example.emic.util.EMIUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class EMIUtilTest {

    @Test
    fun testEmiCalculation_basic() {
        val res = EMIUtil.calculateEMI(principal = 1000000.0, annualRatePercent = 8.5, tenureMonths = 240)
        assertTrue(abs(res.emi - 8678.0) < 50)
        assertEquals(res.totalPayment, res.emi * 240, 1e-6)
        assertEquals(res.totalInterest, res.totalPayment - 1000000.0, 1e-6)
    }

    @Test
    fun testSchedule_sums() {
        val p = 500000.0
        val r = 9.0
        val t = 120
        val schedule = EMIUtil.buildSchedule(p, r, t)
        val paidPrincipal = schedule.sumOf { it.principalComponent }
        assertTrue(abs(paidPrincipal - p) < 1.0)
        assertTrue(schedule.last().remainingPrincipal < 1.0)
    }

    @Test
    fun testPrepayment_reduceTenure() {
        val base = EMIUtil.buildSchedule(1000000.0, 8.0, 240).size
        val sim = EMIUtil.simulatePrepayment(1000000.0, 8.0, 240, extraPayment = 2000.0, mode = "REDUCE_TENURE")
        assertTrue(sim.newTenureMonths < base)
        val baseEmi = EMIUtil.calculateEMI(1000000.0, 8.0, 240).emi
        assertTrue(abs(sim.schedule[0].payment - (baseEmi + 2000.0)) < 0.1)
    }

    @Test
    fun testPrepayment_reduceEmi() {
        val sim = EMIUtil.simulatePrepayment(1000000.0, 8.0, 240, extraPayment = 2000.0, mode = "REDUCE_EMI")
        assertTrue(sim.newTenureMonths <= 240)
        assertTrue(sim.newEmi >= 0.0)
    }
}
