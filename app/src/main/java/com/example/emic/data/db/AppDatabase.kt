package com.example.emic.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.emic.data.dao.BankRateDao
import com.example.emic.data.dao.EncryptedLoanDao
import com.example.emic.data.model.BankRate
import com.example.emic.data.model.EncryptedLoan

@Database(
    entities = [BankRate::class, EncryptedLoan::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bankRateDao(): BankRateDao
    abstract fun encryptedLoanDao(): EncryptedLoanDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "emi_calc.db"
                ).build().also { INSTANCE = it }
            }
    }
}
