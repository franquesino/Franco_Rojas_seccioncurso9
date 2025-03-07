package com.example.franco_rojas_seccioncurso9

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.franco_rojas_seccioncurso9.model.Product
import com.example.franco_rojas_seccioncurso9.repository.ProductRepository
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChartActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var repository: ProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        barChart = findViewById(R.id.barChart)
        repository = ProductRepository(this)

        loadTopRatedProducts()
    }

    private fun loadTopRatedProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            val products = repository.getAllProducts()
                .sortedByDescending { it.rating }
                .take(5) // Tomar los 5 mejores

            val entries = products.mapIndexed { index, product ->
                BarEntry(index.toFloat(), product.rating.toFloat())
            }

            val dataSet = BarDataSet(entries, "Top 5 Productos")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueTextColor = Color.BLACK
            dataSet.valueTextSize = 16f

            val barData = BarData(dataSet)

            runOnUiThread {
                barChart.data = barData
                barChart.description.isEnabled = false
                barChart.setFitBars(true)
                barChart.animateY(1500)

                val xAxis = barChart.xAxis
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return products.getOrNull(value.toInt())?.title ?: ""
                    }
                }
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.granularity = 1f
                xAxis.textSize = 12f

                barChart.invalidate()
            }
        }
    }
}
