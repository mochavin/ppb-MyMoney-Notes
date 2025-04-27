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
import androidx.compose.ui.draw.clip
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

@Composable
fun ChartScreen(viewModel: TransactionViewModel) {
    val expenseSummary by viewModel.expenseSummary.collectAsStateWithLifecycle()
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundColor = MaterialTheme.colorScheme.background
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    // Define a custom color palette that looks better than the default
    val chartColors = remember {
        listOf(
            0xFF6200EE.toInt(), // Purple
            0xFF03DAC5.toInt(), // Teal
            0xFFFF6D00.toInt(), // Orange
            0xFF018786.toInt(), // Dark teal
            0xFFB00020.toInt(), // Red
            0xFF3700B3.toInt(), // Deep purple
            0xFF03A9F4.toInt(), // Blue
            0xFF4CAF50.toInt(), // Green
            0xFFFFC107.toInt(), // Amber
            0xFF9C27B0.toInt()  // Purple
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.chart_screen_title),
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
            if (expenseSummary.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            stringResource(R.string.no_data_for_chart),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Add transactions to see your spending breakdown",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Chart content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        "Expense Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // PieChart with AndroidView
                    AndroidView(
                        factory = { context ->
                            PieChart(context).apply {
                                // Enhanced chart setup
                                layoutParams = LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )

                                // Basic appearance
                                description.isEnabled = false
                                setExtraOffsets(20f, 20f, 20f, 20f) // Give the chart some breathing room

                                // Configure the center hole
                                isDrawHoleEnabled = true
                                holeRadius = 58f
                                transparentCircleRadius = 61f
                                setHoleColor(surfaceColor.toArgb())
                                setTransparentCircleColor(surfaceColor.toArgb())
                                setTransparentCircleAlpha(50)

                                // Add center text
                                setCenterText("Expenses")
                                setCenterTextSize(18f)
                                setCenterTextTypeface(Typeface.DEFAULT_BOLD)
                                setCenterTextColor(onSurfaceColor.toArgb())

                                // Configure labels and values
                                setUsePercentValues(true)
                                setEntryLabelColor(Color.WHITE)
                                setEntryLabelTextSize(12f)

                                // Configure legend
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
                                }

                                // Animation settings
                                animateXY(1400, 1400, Easing.EaseInOutQuad)

                                // Disable touch/gesture functionality if not needed
                                // setTouchEnabled(false)

                                // Other settings
                                rotationAngle = 0f
                                isRotationEnabled = true
                                isHighlightPerTapEnabled = true
                                setMinAngleForSlices(20f) // Prevents tiny slices from being too small
                            }
                        },
                        update = { chart ->
                            // Update chart with data
                            val entries = expenseSummary.map { (category, amount) ->
                                PieEntry(amount.toFloat(), category.name)
                            }

                            val dataSet = PieDataSet(entries, "").apply {
                                colors = chartColors
                                valueTextColor = Color.BLACK
                                valueTextSize = 12f
                                sliceSpace = 3f
                                valueLineColor = Color.BLACK
                                valueLinePart1Length = 0.5f
                                valueLinePart2Length = 0.3f
                                yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                                selectionShift = 10f // Makes selected slice stand out more
                            }

                            val pieData = PieData(dataSet).apply {
                                setValueFormatter(PercentFormatter(chart))
                            }

                            chart.data = pieData
                            chart.highlightValues(null) // Clear highlights
                            chart.invalidate() // Refresh
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )

                    // Summary statistics below the chart
                    val totalAmount = expenseSummary.values.sum()
                    val topCategory = expenseSummary.maxByOrNull { it.value }

                    Spacer(modifier = Modifier.height(16.dp))

                    fun formatCurrencyIntegerSimpleTemplate(amount: Double): String {
                        val roundedAmount = amount.toInt() // Round to the nearest integer
                        return "Rp. %d".format(roundedAmount)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatCard(
                            title = "Total Expenses",
                            value = formatCurrencyIntegerSimpleTemplate(totalAmount),
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        topCategory?.let {
                            StatCard(
                                title = "Top Category",
                                value = "${it.key.name}\n" +
                                        formatCurrencyIntegerSimpleTemplate(it.value),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}