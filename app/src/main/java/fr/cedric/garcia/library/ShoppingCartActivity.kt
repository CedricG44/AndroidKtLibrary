package fr.cedric.garcia.library

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.book.CommercialOffer
import fr.cedric.garcia.library.cart.ShoppingCart
import fr.cedric.garcia.library.cart.ShoppingCartAdapter
import fr.cedric.garcia.library.repositories.HenriPotierRepository
import fr.cedric.garcia.library.services.HenriPotierService
import kotlinx.coroutines.*

class ShoppingCartActivity : AppCompatActivity() {

    private val booksRepository = HenriPotierRepository(HenriPotierService.service)
    private lateinit var offers: CommercialOffer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping_cart_activity)

        val shoppingCartRecyclerView = findViewById<RecyclerView>(R.id.shoppingCartView)
        shoppingCartRecyclerView.layoutManager = LinearLayoutManager(this)
        shoppingCartRecyclerView.adapter = ShoppingCartAdapter(this, ShoppingCart.getCart())

        val totalPrice = ShoppingCart.getCart()
            .fold(0.toDouble()) { acc, cartItem -> acc + cartItem.quantity.times(cartItem.book.price.toDouble()) }

        offers = runBlocking { loadCommercialOffers(ShoppingCart.getCart().map { it.book }) }
    }

    private suspend fun loadCommercialOffers(books: List<Book>): CommercialOffer =
        coroutineScope {
            val offers = async { booksRepository.getCommercialOffers(books.map { it.isbn }) }
            withContext(Dispatchers.IO) {
                offers.await().fold({
                    Log.e("getCommercialOffers", it.message, it)
                    CommercialOffer(emptyList())
                }, {
                    Log.d("offers", it.toString())
                    it
                })
            }
        }
}
