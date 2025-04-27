package com.example.mymoneynotes.ui.theme.screens // or ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mymoneynotes.R
import com.example.mymoneynotes.data.Category
import com.example.mymoneynotes.viewmodel.TransactionViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

@Composable
fun ChartScreen(viewModel: TransactionViewModel) {
    val expenseSummary by viewModel.expenseSummary.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.chart_screen_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (expenseSummary.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f), // Take available space
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_data_for_chart))
            }
        } else {
            // Prepare data for Vico Chart
            // Vico needs numerical x-axis values, so we map categories to indices
            val categoryList = expenseSummary.keys.toList() // Consistent order
            val chartEntries = categoryList.mapIndexed { index, category ->
                entryOf(index.toFloat(), expenseSummary[category]?.toFloat() ?: 0f)
            }
            val chartEntryModelProducer = ChartEntryModelProducer(chartEntries)

            // Formatter to show category names on the bottom axis
            val bottomAxisValueFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
                categoryList.getOrNull(value.toInt())?.name ?: ""
            }

            Chart(
                chart = columnChart(),
                chartModelProducer = chartEntryModelProducer,
                startAxis = rememberStartAxis(), // Simple Y-axis
                bottomAxis = rememberBottomAxis( // X-axis with category names
                    valueFormatter = bottomAxisValueFormatter,
                    labelRotationDegrees = -45f // Rotate labels if they overlap
                ),
                modifier = Modifier
                    .height(300.dp) // Give the chart a fixed height
                    .fillMaxWidth()
            )

            // Optional: Display raw data below chart
            // Spacer(modifier = Modifier.height(16.dp))
            // expenseSummary.forEach { (category, total) ->
            //     Text("${category.name}: $${String.format("%.2f", total)}")
            // }
        }
    }
}