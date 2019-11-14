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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.book.CommercialOffer
import fr.cedric.garcia.library.book.Offer
import fr.cedric.garcia.library.cart.ShoppingCart
import fr.cedric.garcia.library.cart.ShoppingCartAdapter
import fr.cedric.garcia.library.repositories.HenriPotierRepository
import fr.cedric.garcia.library.services.HenriPotierService
import kotlinx.coroutines.*
import kotlin.math.roundToInt

/**
 * Shopping cart Activity.
 */
class ShoppingCartActivity : AppCompatActivity() {

    private val booksRepository = HenriPotierRepository(HenriPotierService.service)
    private var offers: MutableLiveData<CommercialOffer> = MutableLiveData()
    private var totalPriceWithoutOffer: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shopping_cart_activity)
        offers.postValue(CommercialOffer(emptyList()))

        // Load commercial offers
        loadCommercialOffers(ShoppingCart.getCart().map { it.book })
        totalPriceWithoutOffer = totalPrice()

        // Setup RecyclerView
        val shoppingCartRecyclerView = findViewById<RecyclerView>(R.id.shoppingCartView)
        shoppingCartRecyclerView.layoutManager = LinearLayoutManager(this)
        shoppingCartRecyclerView.adapter = ShoppingCartAdapter(this, ShoppingCart.getCart())

        val totalPriceWithoutOfferText = findViewById<TextView>(R.id.cartTotalPriceWithoutOffer)
        val effectiveOfferText = findViewById<TextView>(R.id.cartEffectiveOffer)
        val totalPriceText = findViewById<TextView>(R.id.cartTotalPrice)

        totalPriceWithoutOfferText.text = getString(R.string.price, totalPriceWithoutOffer)

        offers.observe(this, Observer {
            // Setup commercial offers dropdown menu
            val offersSpinner = findViewById<Spinner>(R.id.commercialOffersSpinner)
            offersSpinner.adapter = ArrayAdapter<String>(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                formatOffers(it)
            )

            // Setup commercial offer click listener
            offersSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedOffer = it.offers[position]
                    Log.d("selectedOffer", selectedOffer.toString())

                    // Update total price
                    val offer = calculateOffer(selectedOffer)
                    effectiveOfferText.text = getString(R.string.price, offer)
                    totalPriceText.text = getString(R.string.price, totalPriceWithoutOffer - offer)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        })
    }

    /**
     * Calculate total price base on shopping cart current state.
     */
    private fun totalPrice(): Double =
        ShoppingCart.getCart()
            .fold(0.toDouble()) { acc, cartItem -> acc + cartItem.quantity.times(cartItem.book.price.toDouble()) }

    /**
     * Calculate effective price giventan [offer].
     */
    private fun calculateOffer(offer: Offer): Double =
        when (offer.type) {
            "minus" -> offer.value.toDouble()
            "percentage" -> ((totalPriceWithoutOffer / 100) * offer.value)
            "slice" -> ((totalPriceWithoutOffer / offer.sliceValue!!).roundToInt() * offer.value).toDouble()
            else -> totalPriceWithoutOffer
        }

    /**
     * Format commercial [offers] with their specific denominations.
     */
    private fun formatOffers(offers: CommercialOffer): List<String> =
        offers.offers.map {
            when (it.type) {
                "minus" -> getString(R.string.offer_minus, it.value)
                "percentage" -> getString(R.string.offer_percentage, it.value)
                "slice" -> getString(R.string.offer_slice, it.value, it.sliceValue)
                else -> getString(R.string.offer_empty)
            }
        }

    /**
     * Load commercial offers from repository given a list of [books].
     */
    private fun loadCommercialOffers(books: List<Book>): Job =
        CoroutineScope(Dispatchers.Main).launch {
            val offersResult = booksRepository.getCommercialOffers(books.map { it.isbn })
            withContext(Dispatchers.IO) {
                offersResult.fold({
                    Log.e("getCommercialOffers", it.message, it)
                }, {
                    offers.postValue(it)
                    Log.d("offers", it.toString())
                })
            }
        }
}
