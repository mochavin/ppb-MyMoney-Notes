package com.example.mymoneynotes.ui.theme.screens

import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart // Use PieChart icon
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb // Essential for converting Compose Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mymoneynotes.R
import com.example.mymoneynotes.utils.LabelAndAmountValueFormatter
import com.example.mymoneynotes.utils.formatRupiah // Import the Rupiah formatter
import com.example.mymoneynotes.viewmodel.TransactionViewModel
// MPAndroidChart Imports
import com.github.mikephil.charting.charts.PieChart as MPPieChart // Alias to avoid name clash
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.animation.Easing

@Composable
fun ChartScreen(viewModel: TransactionViewModel) {
    val expenseSummary by viewModel.expenseSummary.collectAsStateWithLifecycle()
    val surfaceColor = MaterialTheme.colorScheme.surface
    val backgroundColor = MaterialTheme.colorScheme.background
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val labelAndAmountFormatter = remember { LabelAndAmountValueFormatter() }

    // Define a custom color palette for expenses (Reds, Oranges, etc.)
    val chartColors = remember {
        listOf(
            0xFFEF5350, 0xFFEC407A, 0xFFAB47BC, 0xFF7E57C2, 0xFF5C6BC0, // Reds/Pinks/Purples
            0xFFFFA726, 0xFFFF7043, 0xFF8D6E63, 0xFFBDBDBD, 0xFF78909C  // Oranges/Browns/Greys
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
            stringResource(R.string.chart_screen_expense_title),
            style = MaterialTheme.typography.headlineMedium,
            color = onSurfaceColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(bottom = 24.dp)
                .fillMaxWidth()
        )

        Card(
            modifier = Modifier
                .weight(1f) // Takes available space
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = surfaceColor)
        ) {
            if (expenseSummary.isEmpty()) {
                // Enhanced Empty state specific to expenses
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
                            imageVector = Icons.Outlined.PieChart,
                            contentDescription = stringResource(R.string.no_expense_data_for_chart),
                            modifier = Modifier.size(64.dp),
                            tint = onSurfaceVariantColor.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.no_expense_data_for_chart),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = onSurfaceVariantColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.add_expenses_to_see_chart),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = onSurfaceVariantColor.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Chart content for expenses
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp) // Padding inside the card
                ) {
                    Text(
                        stringResource(R.string.expense_breakdown),
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
                                animateY(1000, Easing.EaseOutCubic) // Animate on creation

                                isDrawHoleEnabled = true
                                holeRadius = 55f
                                transparentCircleRadius = 58f
                                setHoleColor(surfaceColor.toArgb())
                                setTransparentCircleColor(surfaceColor.toArgb())
                                setTransparentCircleAlpha(80)

                                centerText = context.getString(R.string.expenses_center_text)
                                setCenterTextSize(16f)
                                setCenterTextTypeface(Typeface.SANS_SERIF)
                                setCenterTextColor(onSurfaceVariantColor.toArgb())

                                // --- IMPORTANT: Disable default entry labels ---
                                setDrawEntryLabels(false)
                                // ----------------------------------------------

                                // Configure value settings (now controlled by AmountValueFormatter)
                                // setUsePercentValues(false) // We are showing absolute amounts, not %

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
                                setMinAngleForSlices(10f) // Avoid tiny slices
                            }
                        },
                        update = { chart ->
                            // --- Prepare entries: value=amount, label=category name ---
                            val entries = expenseSummary.map { (category, amount) ->
                                PieEntry(amount.toFloat(), category.name) // Label is just the name
                            }
                            // ---------------------------------------------------------

                            val dataSet = PieDataSet(entries, "").apply { // No label for the dataset itself
                                sliceSpace = 2.5f
                                colors = chartColors // Use expense colors
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
                                // setValueTextSize(...) // Set size via PieDataSet
                                // setValueTextColor(...) // Set color via PieDataSet
                                // -------------------------------------------------
                            }

                            // Update chart data
                            chart.data = pieData
                            // chart.setUsePercentValues(false) // Ensure not using percentages for display logic
                            chart.highlightValues(null)
                            chart.invalidate() // Redraw
                            chart.animateY(800, Easing.EaseOutCubic) // Animate data update
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .aspectRatio(1f) // Make it square
                            .padding(vertical = 8.dp)
                    )

                    // Summary statistics below the chart
                    val totalAmount = expenseSummary.values.sum()
                    val topCategory = expenseSummary.maxByOrNull { it.value }

                    Spacer(modifier = Modifier.height(24.dp)) // Increased space before stats

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            title = stringResource(R.string.total_expenses_stat),
                            value = formatRupiah(totalAmount),
                            modifier = Modifier.weight(1f)
                        )

                        topCategory?.let { (category, amount) ->
                            StatCard(
                                title = stringResource(R.string.top_category_stat),
                                value = category.name,
                                secondaryValue = formatRupiah(amount),
                                modifier = Modifier.weight(1f)
                            )
                        } ?: Box(modifier = Modifier.weight(1f)) // Placeholder if no data
                    }
                    Spacer(modifier = Modifier.height(8.dp)) // Padding at the bottom if needed
                }
            }
        }
    }
}

// Keep StatCard as defined in the original prompt (it's good)
@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    secondaryValue: String? = null
) {
    Surface(
        modifier = modifier
            .heightIn(min = 80.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
            secondaryValue?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1
                )
            }
        }
    }
}