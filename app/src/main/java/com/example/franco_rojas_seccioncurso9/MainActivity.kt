package com.example.franco_rojas_seccioncurso9

import android.os.Bundle
import android.util.Log
import android.content.Intent

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.franco_rojas_seccioncurso9.adapter.ProductAdapter
import com.example.franco_rojas_seccioncurso9.model.Product
import com.example.franco_rojas_seccioncurso9.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var repository: ProductRepository
    private var productList = mutableListOf<Product>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.itemAnimator = DefaultItemAnimator()

        registerForContextMenu(recyclerView)

        repository = ProductRepository(this)

        loadProducts()

        val fab: FloatingActionButton = findViewById(R.id.fab_view_chart)
        fab.setOnClickListener {
            val intent = Intent(this, ChartActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            productList = repository.getAllProducts().toMutableList()
            runOnUiThread {
                adapter = ProductAdapter(productList, this@MainActivity::deleteProduct, this@MainActivity::addProduct)
                recyclerView.adapter = adapter

                if (productList.isNotEmpty()) {
                    Toast.makeText(this@MainActivity, "Productos cargados correctamente", Toast.LENGTH_SHORT).show()
                    Log.d("MainActivity", "Productos cargados correctamente en la base de datos.")
                }
            }
        }
    }

    private fun deleteProduct(product: Product) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteProductFromDatabase(product.id)
            productList.remove(product)
            runOnUiThread {
                adapter.notifyDataSetChanged()
                Toast.makeText(this@MainActivity, "Producto eliminado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addProduct(product: Product) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.saveProductsToDatabase(listOf(product))
            productList.add(product)
            runOnUiThread {
                adapter.notifyDataSetChanged()
                Toast.makeText(this@MainActivity, "Producto agregado", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.view_chart -> {
                val intent = Intent(this, ChartActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun showAddProductDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Producto")
        builder.setMessage("Funcionalidad en desarrollo")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}
