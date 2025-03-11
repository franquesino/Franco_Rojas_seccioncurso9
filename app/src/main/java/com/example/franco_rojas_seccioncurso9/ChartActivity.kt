package com.example.franco_rojas_seccioncurso9

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.franco_rojas_seccioncurso9.repository.ProductRepository
import com.example.franco_rojas_seccioncurso9.model.Product
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import androidx.lifecycle.lifecycleScope
import android.graphics.Color
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.LargeValueFormatter
import androidx.core.content.ContextCompat


class ChartActivity : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var repository: ProductRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        Log.d("ChartActivity", "Se abrió la actividad del gráfico")

        barChart = findViewById(R.id.barChart)
        repository = ProductRepository(this)

        loadChartData()
    }

    private fun loadChartData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val products = repository.getAllProducts()
                .sortedByDescending { it.rating }  // Ordena por calificación de mayor a menor
                .take(5)  // Toma solo los 5 mejores

            Log.d("ChartActivity", "Productos cargados: ${products.size}")

            val entries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()

            for ((index, product) in products.withIndex()) {
                Log.d("ChartActivity", "Producto: ${product.title} - Rating: ${product.rating}")
                entries.add(BarEntry(index.toFloat(), product.rating.toFloat()))
                labels.add(product.title.take(10)) // Solo toma los primeros 10 caracteres del nombre
            }

            runOnUiThread {
                if (entries.isEmpty()) {
                    Log.d("ChartActivity", "No hay datos para mostrar")
                    return@runOnUiThread
                }

                val dataSet = BarDataSet(entries, "Top 5 Productos").apply {
                    color = ContextCompat.getColor(this@ChartActivity, R.color.purple_500) // Añade color
                    valueTextColor = Color.BLACK
                }

                val barData = BarData(dataSet).apply {
                    barWidth = 0.5f
                    setValueFormatter(LargeValueFormatter()) // Para formato de números
                }

                with(barChart) {
                    data = barData
                    description.isEnabled = false
                    setFitBars(true)
                    animateY(1000) // Animación

                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        granularity = 1f
                        valueFormatter = IndexAxisValueFormatter(labels) // Etiquetas de texto
                    }

                    axisLeft.apply {
                        granularity = 1f
                        axisMinimum = 0f
                    }

                    axisRight.isEnabled = false
                    legend.isEnabled = false
                    invalidate()
                }
            }
        }
    }
}
