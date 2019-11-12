package fr.cedric.garcia.library.cart

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.cardview.widget.CardView
import fr.cedric.garcia.library.R

class BookCartItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private lateinit var cartBookTitle: TextView
    private lateinit var cartBookPrice: TextView
    private lateinit var cartBookQuantity: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        cartBookTitle = findViewById(R.id.cartBookTitle)
        cartBookPrice = findViewById(R.id.cartBookPrice)
        cartBookQuantity = findViewById(R.id.cartBookQuantity)
    }

    fun bindView(bookItem: BookCartItem) {
        cartBookTitle.text = bookItem.book.title
        cartBookPrice.text = "${bookItem.book.price} €"
        cartBookQuantity.text = "${bookItem.quantity} x "
    }
}
