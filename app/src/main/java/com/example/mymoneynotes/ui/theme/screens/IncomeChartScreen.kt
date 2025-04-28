package com.example.mymoneynotes.ui.theme.screens

// Keep android.graphics imports if needed
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Addchart
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
import com.example.mymoneynotes.utils.formatRupiah
import com.example.mymoneynotes.utils.LabelAndAmountValueFormatter
import com.example.mymoneynotes.viewmodel.TransactionViewModel
// MPAndroidChart Imports
import com.github.mikephil.charting.charts.PieChart as MPPieChart // Alias
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.animation.Easing

@Composable
fun IncomeChartScreen(viewModel: TransactionViewModel) {
    val incomeSummary by viewModel.incomeSummary.collectAsStateWithLifecycle()
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundColor = MaterialTheme.colorScheme.background
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val labelAndAmountFormatter = remember { LabelAndAmountValueFormatter() }

    // Income-focused color palette (Greens, Blues, etc.)
    val chartColors = remember {
        listOf(
            0xFF4CAF50, 0xFF8BC34A, 0xFF009688, 0xFF03A9F4, 0xFF2196F3,
            0xFF3F51B5, 0xFF00BCD4, 0xFFCDDC39, 0xFFFFC107, 0xFF673AB7 // Example income colors
        ).map { it.toInt() }.shuffled()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.chart_screen_income_title),
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
                // Enhanced Empty state specific to income
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Addchart,
                            contentDescription = stringResource(R.string.no_income_data_for_chart),
                            modifier = Modifier.size(64.dp),
                            tint = onSurfaceVariantColor.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.no_income_data_for_chart),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = onSurfaceVariantColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.add_income_to_see_chart),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = onSurfaceVariantColor.copy(alpha = 0.7f)
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
                        style = MaterialTheme.typography.titleLarge,
                        color = onSurfaceColor,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // PieChart using AndroidView
                    AndroidView(
                        factory = { context ->
                            MPPieChart(context).apply {
                                layoutParams = LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                description.isEnabled = false
                                setExtraOffsets(5f, 10f, 5f, 5f)
                                isRotationEnabled = true
                                isHighlightPerTapEnabled = true
                                animateY(1000, Easing.EaseOutCubic)

                                isDrawHoleEnabled = true
                                holeRadius = 55f
                                transparentCircleRadius = 58f
                                setHoleColor(surfaceColor.toArgb())
                                setTransparentCircleColor(surfaceColor.toArgb())
                                setTransparentCircleAlpha(80)

                                centerText = context.getString(R.string.income_center_text)
                                setCenterTextSize(16f)
                                setCenterTextTypeface(Typeface.SANS_SERIF)
                                setCenterTextColor(onSurfaceVariantColor.toArgb())

                                // --- IMPORTANT: Disable default entry labels ---
                                setDrawEntryLabels(false)
                                // ----------------------------------------------

                                // Configure value settings
                                // setUsePercentValues(false)

                                legend.apply {
                                    verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                                    horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                                    orientation = Legend.LegendOrientation.HORIZONTAL
                                    setDrawInside(false)
                                    form = Legend.LegendForm.CIRCLE
                                    formSize = 9f
                                    textSize = 12f
                                    textColor = onSurfaceVariantColor.toArgb()
                                    xEntrySpace = 8f
                                    yEntrySpace = 4f
                                    yOffset = 10f
                                    isWordWrapEnabled = true
                                }
                                setMinAngleForSlices(10f)
                            }
                        },
                        update = { chart ->
                            // --- Prepare entries: value=amount, label=category name ---
                            val entries = incomeSummary.map { (category, amount) ->
                                PieEntry(amount.toFloat(), category.name) // Label is just the name
                            }
                            // ---------------------------------------------------------

                            val dataSet = PieDataSet(entries, "").apply {
                                sliceSpace = 2.5f
                                colors = chartColors // Use income colors
                                selectionShift = 6f

                                // --- Configure how VALUES are drawn ---
                                setDrawValues(true) // IMPORTANT: Enable drawing values
                                valueLinePart1OffsetPercentage = 80f
                                valueLinePart1Length = 0.4f
                                valueLinePart2Length = 0.5f
                                valueLineColor = onSurfaceVariantColor.copy(alpha = 0.7f).toArgb()
                                yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                                xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

                                setValueTextColors(listOf(onSurfaceVariantColor.toArgb()))
                                valueTextSize = 10f
                                valueTypeface = Typeface.SANS_SERIF
                                // ------------------------------------
                            }

                            val pieData = PieData(dataSet).apply {
                                // --- Apply the custom formatter to show amounts ---
                                setValueFormatter(labelAndAmountFormatter)
                                // -------------------------------------------------
                            }

                            // Update chart data
                            chart.data = pieData
                            // chart.setUsePercentValues(false)
                            chart.highlightValues(null)
                            chart.invalidate()
                            chart.animateY(800, Easing.EaseOutCubic)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .aspectRatio(1f) // Make it square
                            .padding(vertical = 8.dp)
                    )

                    // Summary statistics for income
                    val totalAmount = incomeSummary.values.sum()
                    val topCategory = incomeSummary.maxByOrNull { it.value }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = stringResource(R.string.total_income_stat),
                            value = formatRupiah(totalAmount),
                            modifier = Modifier.weight(1f)
                        )

                        topCategory?.let { (category, amount) ->
                            StatCard(
                                title = stringResource(R.string.top_income_source_stat),
                                value = category.name,
                                secondaryValue = formatRupiah(amount),
                                modifier = Modifier.weight(1f)
                            )
                        } ?: Box(modifier = Modifier.weight(1f)) // Placeholder
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}