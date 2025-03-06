package com.example.franco_rojas_seccioncurso9

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.franco_rojas_seccioncurso9.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = ProductRepository(this)

        CoroutineScope(Dispatchers.IO).launch {
            val products = repository.fetchProductsFromAPI()
            if (products.isNotEmpty()) {
                repository.saveProductsToDatabase(products)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Productos cargados correctamente", Toast.LENGTH_LONG).show()
                    Log.d("MainActivity", "Productos guardados en la base de datos")
                }
            }
        }
    }
}