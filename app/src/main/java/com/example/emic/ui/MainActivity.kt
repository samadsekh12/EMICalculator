package com.example.emic.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.emic.databinding.ActivityMainBinding
import com.example.emic.ui.viewmodel.LoanViewModel
import com.example.emic.worker.ReminderWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val vm: LoanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, com.example.emic.ui.fragment.CalculatorFragment())
            .commit()

        binding.btnCalculator.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, com.example.emic.ui.fragment.CalculatorFragment())
                .commit()
        }
        binding.btnPrepayment.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, com.example.emic.ui.fragment.PrepaymentFragment())
                .commit()
        }
        binding.btnRates.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, com.example.emic.ui.fragment.RatesFragment())
                .commit()
        }

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "emi_reminder_daily",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
