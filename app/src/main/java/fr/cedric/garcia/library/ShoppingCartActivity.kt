package fr.cedric.garcia.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.cedric.garcia.library.cart.ShoppingCart
import fr.cedric.garcia.library.cart.ShoppingCartAdapter

class ShoppingCartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping_cart_activity)

        val shoppingCartRecyclerView = findViewById<RecyclerView>(R.id.shoppingCartView)
        shoppingCartRecyclerView.layoutManager = LinearLayoutManager(this)
        shoppingCartRecyclerView.adapter = ShoppingCartAdapter(this, ShoppingCart.getCart())

        val totalPrice = ShoppingCart.getCart()
            .fold(0.toDouble()) { acc, cartItem -> acc + cartItem.quantity.times(cartItem.book.price.toDouble()) }
    }
}
