package com.example.emic.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.emic.data.db.AppDatabase
import com.example.emic.data.model.BankRate
import com.example.emic.data.model.EncryptedLoan
import com.example.emic.util.CryptoUtil
import org.json.JSONObject

class LoanRepository(private val context: Context) {

    private val db = AppDatabase.get(context)
    private val bankRateDao = db.bankRateDao()
    private val loanDao = db.encryptedLoanDao()

    fun getBankRates(): LiveData<List<BankRate>> = bankRateDao.getAllRates()
    suspend fun upsertBankRate(rate: BankRate) = bankRateDao.upsert(rate)
    suspend fun deleteBankRate(rate: BankRate) = bankRateDao.delete(rate)

    fun getLoans(): LiveData<List<EncryptedLoan>> = loanDao.getAllLoans()

    suspend fun saveLoan(
        title: String,
        principal: Double,
        annualRate: Double,
        tenureMonths: Int,
        note: String?
    ): Long {
        val json = JSONObject().apply {
            put("principal", principal)
            put("annualRate", annualRate)
            put("tenureMonths", tenureMonths)
            put("createdAt", System.currentTimeMillis())
            put("note", note ?: "")
        }.toString()

        val enc = CryptoUtil.encryptToBase64(context, json)
        return loanDao.upsert(EncryptedLoan(title = title, payloadEncBase64 = enc))
    }

    fun decryptLoanPayload(base64: String): JSONObject {
        val plain = CryptoUtil.decryptFromBase64(context, base64)
        return JSONObject(plain)
    }

    suspend fun deleteLoan(loan: EncryptedLoan) = loanDao.delete(loan)
}
