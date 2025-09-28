package com.example.emic.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.emic.data.model.EncryptedLoan

@Dao
interface EncryptedLoanDao {
    @Query("SELECT * FROM encrypted_loans ORDER BY id DESC")
    fun getAllLoans(): LiveData<List<EncryptedLoan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(loan: EncryptedLoan): Long

    @Delete
    suspend fun delete(loan: EncryptedLoan)
}
