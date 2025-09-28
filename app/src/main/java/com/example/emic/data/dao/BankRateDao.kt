package com.example.emic.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.emic.data.model.BankRate

@Dao
interface BankRateDao {
    @Query("SELECT * FROM bank_rates ORDER BY bankName ASC")
    fun getAllRates(): LiveData<List<BankRate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(rate: BankRate): Long

    @Delete
    suspend fun delete(rate: BankRate)
}
