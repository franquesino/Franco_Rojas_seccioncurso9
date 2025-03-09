package com.example.franco_rojas_seccioncurso9

import android.widget.EditText
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
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
    }

    private fun loadProducts() {
        CoroutineScope(Dispatchers.IO).launch {
            productList = repository.getAllProducts().toMutableList()
            runOnUiThread {
                adapter = ProductAdapter(productList, this@MainActivity::deleteProduct)
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
            val position = productList.indexOf(product)
            productList.remove(product)

            runOnUiThread {
                adapter.notifyItemRemoved(position) // Optimiza el refresh
                Toast.makeText(this@MainActivity, "Producto eliminado", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Producto eliminado: ${product.title}")
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
            R.id.add_product -> {
                showAddProductDialog()
                true
            }
            R.id.view_chart -> {
                startActivity(Intent(this, ChartActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddProductDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Agregar Producto")

        // Inflar el layout personalizado
        val view = layoutInflater.inflate(R.layout.dialog_add_product, null)
        builder.setView(view)

        // Referencias a los campos de entrada
        val productNameInput = view.findViewById<EditText>(R.id.productNameInput)
        val productPriceInput = view.findViewById<EditText>(R.id.productPriceInput)
        val productRatingInput = view.findViewById<EditText>(R.id.productRatingInput)
        val productImageInput = view.findViewById<EditText>(R.id.productImageInput)

        builder.setPositiveButton("Agregar") { _, _ ->
            val name = productNameInput.text.toString().trim()
            val price = productPriceInput.text.toString().trim().toDoubleOrNull()
            val rating = productRatingInput.text.toString().trim().toDoubleOrNull()
            val imageUrl = productImageInput.text.toString().trim()

            if (name.isEmpty() || price == null || rating == null || imageUrl.isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese datos válidos", Toast.LENGTH_SHORT).show()
            } else {
                val newProduct = Product(
                    id = System.currentTimeMillis().toInt(),
                    title = name,
                    price = price,
                    rating = rating,
                    image = imageUrl
                )
                addProduct(newProduct)
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    private fun addProduct(product: Product) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.insertProduct(product)  // Cambiado de addProductToDatabase a insertProduct
            productList.add(product)

            runOnUiThread {
                adapter.notifyItemInserted(productList.size - 1) // Optimiza el refresh
                Toast.makeText(this@MainActivity, "Producto agregado", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Producto agregado: ${product.title}")
            }
        }
    }



    // 📌 MENÚ CONTEXTUAL PARA PRODUCTOS
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val focusedView = recyclerView.focusedChild ?: return false
        val position = (recyclerView.getChildViewHolder(focusedView) as ProductAdapter.ProductViewHolder).bindingAdapterPosition


        val product = productList[position]

        return when (item.itemId) {
            R.id.add_product -> {
                showAddProductDialog()
                Log.d("MainActivity", "Opción de agregar producto seleccionada.")
                true
            }
            R.id.delete_product -> {
                deleteProduct(product)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}
