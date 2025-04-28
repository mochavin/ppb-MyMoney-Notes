package com.example.mymoneynotes.ui.theme.screens

import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mymoneynotes.R
import com.example.mymoneynotes.viewmodel.TransactionViewModel
// MPAndroidChart Imports
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.animation.Easing
import java.text.NumberFormat
import java.util.Locale

// Reusable helper function for Rupiah formatting (could be moved to a common utils file)
private fun formatRupiahIncome(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    formatter.maximumFractionDigits = 0
    formatter.minimumFractionDigits = 0
    return formatter.format(amount)
}

@Composable
fun IncomeChartScreen(viewModel: TransactionViewModel) {
    val incomeSummary by viewModel.incomeSummary.collectAsStateWithLifecycle() // Use incomeSummary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundColor = MaterialTheme.colorScheme.background
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    // Income-focused color palette (Greens, Blues, Teals etc.)
    val chartColors = remember {
        listOf(
            0xFF4CAF50.toInt(), // Green
            0xFF8BC34A.toInt(), // Light Green
            0xFF009688.toInt(), // Teal
            0xFF03A9F4.toInt(), // Light Blue
            0xFF2196F3.toInt(), // Blue
            0xFF3F51B5.toInt(), // Indigo
            0xFF00BCD4.toInt(), // Cyan
            0xFFCDDC39.toInt(), // Lime
            0xFFFFC107.toInt(), // Amber
            0xFF673AB7.toInt()  // Deep Purple
        ).shuffled() // Shuffle for variety
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.chart_screen_income_title), // Title for Income Chart
            style = MaterialTheme.typography.headlineMedium,
            color = onSurfaceColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        )

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor)
        ) {
            if (incomeSummary.isEmpty()) {
                // Empty state specific to income
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            stringResource(R.string.no_income_data_for_chart),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.add_income_to_see_chart),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Chart content for income
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.income_breakdown),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // PieChart using AndroidView
                    AndroidView(
                        factory = { context ->
                            PieChart(context).apply {
                                layoutParams = LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                description.isEnabled = false
                                setExtraOffsets(20f, 20f, 20f, 20f)
                                isDrawHoleEnabled = true
                                holeRadius = 58f
                                transparentCircleRadius = 61f
                                setHoleColor(surfaceColor.toArgb())
                                setTransparentCircleColor(surfaceColor.toArgb())
                                setTransparentCircleAlpha(50)
                                setCenterText(context.getString(R.string.income_center_text)) // Center text for income
                                setCenterTextSize(18f)
                                setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                                setCenterTextColor(onSurfaceColor.toArgb())
                                setUsePercentValues(true)
                                setEntryLabelColor(Color.BLACK) // Contrasting label color
                                setEntryLabelTextSize(11f)
                                setEntryLabelTypeface(Typeface.DEFAULT)
                                legend.apply {
                                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                                    orientation = Legend.LegendOrientation.HORIZONTAL
                                    setDrawInside(false)
                                    xEntrySpace = 10f
                                    yEntrySpace = 0f
                                    yOffset = 10f
                                    textSize = 12f
                                    textColor = onSurfaceColor.toArgb()
                                    form = Legend.LegendForm.CIRCLE
                                    isWordWrapEnabled = true
                                }
                                // Animate only once during factory setup
                                // animateXY(1400, 1400, Easing.EaseInOutQuad) // <-- Moved to update or removed

                                isRotationEnabled = true
                                isHighlightPerTapEnabled = true
                                setMinAngleForSlices(15f)
                            }
                        },
                        update = { chart ->
                            // Update chart with income data
                            val entries = incomeSummary.map { (category, amount) ->
                                PieEntry(amount.toFloat(), category.name)
                            }

                            val dataSet = PieDataSet(entries, "").apply {
                                colors = chartColors // Use income-specific colors
                                valueTextColor = onSurfaceColor.toArgb()
                                valueTextSize = 10f
                                sliceSpace = 2f
                                valueLineColor = onSurfaceColor.toArgb()
                                valueLinePart1Length = 0.4f
                                valueLinePart2Length = 0.4f
                                yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                                xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                                valueLinePart1OffsetPercentage = 80f
                                selectionShift = 8f
                            }

                            val pieData = PieData(dataSet).apply {
                                setValueFormatter(PercentFormatter(chart))
                                setValueTextSize(10f)
                                setValueTextColor(onSurfaceColor.toArgb())
                            }

                            // ---- MODIFICATION START ----
                            chart.clear() // Clear previous data/renderers
                            chart.post { // Post the update to the view's queue
                                chart.data = pieData
                                chart.highlightValues(null)
                                // Optionally re-apply animation here if needed on updates
                                chart.animateXY(1000, 1000, Easing.EaseInOutQuad) // Shorter animation on update
                                chart.invalidate() // Request redraw AFTER setting data within post
                            }
                            // ---- MODIFICATION END ----
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    // Summary statistics for income
                    val totalAmount = incomeSummary.values.sum()
                    val topCategory = incomeSummary.maxByOrNull { it.value }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatCard(
                            title = stringResource(R.string.total_income_stat), // Stat title for income
                            value = formatRupiahIncome(totalAmount), // Format as Rupiah
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        topCategory?.let {
                            StatCard(
                                title = stringResource(R.string.top_income_source_stat), // Stat title for top income source
                                value = "${it.key.name}\n${formatRupiahIncome(it.value)}", // Format as Rupiah
                                modifier = Modifier.weight(1f)
                            )
                        } ?: Box(modifier = Modifier.weight(1f)) // Placeholder if no top category
                    }
                }
            }
        }
    }
}

// StatCard Composable is reused from ChartScreen.kt (or could be moved to a common components file)
// Ensure StatCard composable is available in this scope or imported if moved.

// Helper extension function for color alpha (if not already defined/imported)
// fun Int.copy(alpha: Float): Int { ... } // Defined in ChartScreen.kt