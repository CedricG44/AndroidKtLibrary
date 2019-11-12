package fr.cedric.garcia.library

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
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
import kotlin.math.roundToInt

class ShoppingCartActivity : AppCompatActivity() {

    private val booksRepository = HenriPotierRepository(HenriPotierService.service)
    private lateinit var offers: CommercialOffer
    private lateinit var totalPriceWithoutOfferText: TextView
    private lateinit var effectiveOfferText: TextView
    private lateinit var totalPriceText: TextView
    private lateinit var offersSpinner: Spinner
    private var totalPriceWithoutOffer: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping_cart_activity)

        val shoppingCartRecyclerView = findViewById<RecyclerView>(R.id.shoppingCartView)
        shoppingCartRecyclerView.layoutManager = LinearLayoutManager(this)
        shoppingCartRecyclerView.adapter = ShoppingCartAdapter(this, ShoppingCart.getCart())

        totalPriceWithoutOfferText = findViewById(R.id.cartTotalPriceWithoutOffer)
        effectiveOfferText = findViewById(R.id.cartEffectiveOffer)
        totalPriceText = findViewById(R.id.cartTotalPrice)

        totalPriceWithoutOffer = totalPrice()
        totalPriceWithoutOfferText.text = getString(R.string.price, totalPriceWithoutOffer)

        offers = runBlocking { loadCommercialOffers(ShoppingCart.getCart().map { it.book }) }

        offersSpinner = findViewById(R.id.commercialOffersSpinner)
        offersSpinner.adapter = ArrayAdapter<String>(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            formatOffers(offers)
        )

        offersSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("selectedOffer", offers.offers[position].toString())
                updateTotalPriceWithOffer(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun totalPrice(): Double =
        ShoppingCart.getCart()
            .fold(0.toDouble()) { acc, cartItem -> acc + cartItem.quantity.times(cartItem.book.price.toDouble()) }

    private fun updateTotalPriceWithOffer(offerPosition: Int) {
        val offer = offers.offers[offerPosition]
        val effectiveOffer = when (offer.type) {
            "minus" -> offer.value.toDouble()
            "percentage" -> ((totalPriceWithoutOffer / 100) * offer.value)
            "slice" -> ((totalPriceWithoutOffer / offer.sliceValue!!).roundToInt() * offer.value).toDouble()
            else -> totalPriceWithoutOffer
        }
        effectiveOfferText.text = getString(R.string.price, effectiveOffer)
        totalPriceText.text = getString(R.string.price, totalPriceWithoutOffer - effectiveOffer)
    }

    private fun formatOffers(offers: CommercialOffer): List<String> =
        offers.offers.map {
            when (it.type) {
                "minus" -> getString(R.string.offer_minus, it.value)
                "percentage" -> getString(R.string.offer_percentage, it.value)
                "slice" -> getString(R.string.offer_slice, it.value, it.sliceValue)
                else -> getString(R.string.offer_empty)
            }
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
