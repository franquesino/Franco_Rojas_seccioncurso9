package com.example.franco_rojas_seccioncurso9.adapter

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.franco_rojas_seccioncurso9.R
import com.example.franco_rojas_seccioncurso9.model.Product
import com.squareup.picasso.Picasso
import android.view.Menu

class ProductAdapter(
    private val productList: MutableList<Product>,
    private val onDeleteProduct: (Product) -> Unit,
    private val onAddProduct: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {
        val productImage: ImageView = view.findViewById(R.id.productImage)
        val productTitle: TextView = view.findViewById(R.id.productTitle)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val productRating: TextView = view.findViewById(R.id.productRating)

        lateinit var product: Product

        init {
            view.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu?.add(Menu.NONE, 101, 0, "Eliminar producto")?.setOnMenuItemClickListener {
                onDeleteProduct(product)
                true
            }
            menu?.add(Menu.NONE, 102, 1, "Agregar producto")?.setOnMenuItemClickListener {
                onAddProduct(product)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.product = product
        holder.productTitle.text = product.title


        holder.productPrice.text = holder.itemView.context.getString(R.string.product_price, product.price)
        holder.productRating.text = holder.itemView.context.getString(R.string.product_rating, product.rating)

        Picasso.get().load(product.image).into(holder.productImage)
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
