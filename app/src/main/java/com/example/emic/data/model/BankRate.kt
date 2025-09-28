package com.example.emic.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_rates")
data class BankRate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val bankName: String,
    val annualInterestRate: Double // percentage e.g., 8.5
)
