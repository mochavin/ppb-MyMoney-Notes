package com.example.mymoneynotes.utils

import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.data.PieEntry

// Custom formatter to display amounts as Rupiah strings on the chart slices/lines
class AmountValueFormatter : ValueFormatter() {
    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        val label = pieEntry?.label ?: ""
        val formattedAmount = formatRupiah(value.toDouble())
        // Stick to this reliable single-line format:
        return "$label: $formattedAmount"
    }

    // Optional: Override getFormattedValue if you use this formatter elsewhere (e.g., Bar charts)
    // override fun getFormattedValue(value: Float): String {
    //     return formatRupiah(value.toDouble())
    // }
}

class LabelAndAmountValueFormatter : ValueFormatter() { // Renamed for clarity

    override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
        val label = pieEntry?.label ?: ""
        val formattedAmount = formatRupiah(value.toDouble())
        // Stick to this reliable single-line format:
        return "$label: $formattedAmount"
    }
    // Optional: Override getFormattedValue if needed for other chart types
    // override fun getFormattedValue(value: Float): String {
    //     // For other chart types, you might not have the PieEntry readily available
    //     // You might need a different approach or just format the value.
    //     return formatRupiah(value.toDouble())
    // }
}
