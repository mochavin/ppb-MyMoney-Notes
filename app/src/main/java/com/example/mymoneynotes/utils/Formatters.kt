package com.example.mymoneynotes.utils

import java.text.NumberFormat
import java.util.Locale

// Reusable helper function for Rupiah formatting
fun formatRupiah(amount: Double): String {
    val localeID = Locale("in", "ID") // Use "in" for Indonesian locale
    val formatter = NumberFormat.getCurrencyInstance(localeID)
    formatter.maximumFractionDigits = 0
    formatter.minimumFractionDigits = 0
    return formatter.format(amount)
}