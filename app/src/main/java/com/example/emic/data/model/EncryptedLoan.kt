package com.example.emic.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores encrypted payload string for a loan record.
 * The payload is AES-GCM encrypted JSON containing:
 * { principal: Double, annualRate: Double, tenureMonths: Int, createdAt: Long, note: String? }
 */
@Entity(tableName = "encrypted_loans")
data class EncryptedLoan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val payloadEncBase64: String // Base64(IV || Ciphertext || Tag)
)
