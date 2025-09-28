package com.example.emic.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.emic.repository.LoanRepository
import com.example.emic.util.EMIUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoanViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = LoanRepository(app.applicationContext)

    val principal = MutableLiveData(1000000.0)
    val annualRate = MutableLiveData(8.5)
    val tenureMonths = MutableLiveData(240)
    val extraPayment = MutableLiveData(0.0)
    val prepayMode = MutableLiveData("REDUCE_TENURE")

    val emiResult: LiveData<EMIUtil.EMIResult> = MediatorLiveData<EMIUtil.EMIResult>().apply {
        val observer = Observer<Any> {
            val p = principal.value ?: 0.0
            val r = annualRate.value ?: 0.0
            val t = tenureMonths.value ?: 0
            value = EMIUtil.calculateEMI(p, r, t)
        }
        addSource(principal, observer)
        addSource(annualRate, observer)
        addSource(tenureMonths, observer)
    }

    val schedule: LiveData<List<EMIUtil.AmortizationRow>> = MediatorLiveData<List<EMIUtil.AmortizationRow>>().apply {
        val observer = Observer<Any> {
            val p = principal.value ?: 0.0
            val r = annualRate.value ?: 0.0
            val t = tenureMonths.value ?: 0
            value = EMIUtil.buildSchedule(p, r, t)
        }
        addSource(principal, observer)
        addSource(annualRate, observer)
        addSource(tenureMonths, observer)
    }

    val prepaymentResult: LiveData<EMIUtil.PrepaymentResult> = MediatorLiveData<EMIUtil.PrepaymentResult>().apply {
        val observer = Observer<Any> {
            val p = principal.value ?: 0.0
            val r = annualRate.value ?: 0.0
            val t = tenureMonths.value ?: 0
            val e = extraPayment.value ?: 0.0
            val mode = prepayMode.value ?: "REDUCE_TENURE"
            value = EMIUtil.simulatePrepayment(p, r, t, e, mode)
        }
        addSource(principal, observer)
        addSource(annualRate, observer)
        addSource(tenureMonths, observer)
        addSource(extraPayment, observer)
        addSource(prepayMode, observer)
    }

    val bankRates = repo.getBankRates()
    val loans = repo.getLoans()

    fun saveLoan(title: String, note: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.saveLoan(
                title = title,
                principal = principal.value ?: 0.0,
                annualRate = annualRate.value ?: 0.0,
                tenureMonths = tenureMonths.value ?: 0,
                note = note
            )
        }
    }

    fun deleteBankRate(rateId: Long) {
        val list = bankRates.value ?: return
        list.find { it.id == rateId }?.let { rate ->
            viewModelScope.launch(Dispatchers.IO) { repo.deleteBankRate(rate) }
        }
    }

    fun upsertBankRate(name: String, rate: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.upsertBankRate(com.example.emic.data.model.BankRate(bankName = name, annualInterestRate = rate))
        }
    }

    fun deleteLoanById(id: Long) {
        val list = loans.value ?: return
        list.find { it.id == id }?.let { loan ->
            viewModelScope.launch(Dispatchers.IO) { repo.deleteLoan(loan) }
        }
    }

    fun decodeLoan(base64: String) = repo.decryptLoanPayload(base64)
}
