package com.example.franco_rojas_seccioncurso9.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.franco_rojas_seccioncurso9.database.DatabaseHelper
import com.example.franco_rojas_seccioncurso9.model.Product
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

class ProductRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun fetchProductsFromAPI(): List<Product> {
        val productList = mutableListOf<Product>()
        val urlString = "https://fakestoreapi.com/products"

        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val scanner = Scanner(connection.inputStream)
            val response = StringBuilder()

            while (scanner.hasNext()) {
                response.append(scanner.nextLine())
            }

            val jsonArray = JSONArray(response.toString())

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val product = Product(
                    id = jsonObject.getInt("id"),
                    title = jsonObject.getString("title"),
                    price = jsonObject.getDouble("price"),
                    rating = jsonObject.getJSONObject("rating").getDouble("rate"),
                    image = jsonObject.getString("image")
                )
                productList.add(product)
            }

        } catch (e: Exception) {
            Log.e("ProductRepository", "Error fetching products", e)
        }

        return productList
    }

    fun saveProductsToDatabase(products: List<Product>) {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        for (product in products) {
            val values = ContentValues().apply {
                put("id", product.id)
                put("title", product.title)
                put("price", product.price)
                put("rating", product.rating)
                put("image", product.image)
            }
            db.insertWithOnConflict("products", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
        db.close()
    }

    fun getAllProducts(): List<Product> {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val productList = mutableListOf<Product>()
        val cursor = db.rawQuery("SELECT * FROM products", null)

        while (cursor.moveToNext()) {
            val product = Product(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
                image = cursor.getString(cursor.getColumnIndexOrThrow("image"))
            )
            productList.add(product)
        }
        cursor.close()
        db.close()
        return productList
    }

    fun deleteProductFromDatabase(productId: Int) {
        val db = dbHelper.writableDatabase
        db.delete("products", "id=?", arrayOf(productId.toString()))
        db.close()
    }

    fun insertProduct(product: Product) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id", product.id)
            put("title", product.title)
            put("price", product.price)
            put("rating", product.rating)
            put("image", product.image)
        }
        db.insert("products", null, values)
        db.close()
    }


}