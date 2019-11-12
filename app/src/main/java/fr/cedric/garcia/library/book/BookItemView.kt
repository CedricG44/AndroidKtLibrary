package fr.cedric.garcia.library.book

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.squareup.picasso.Picasso
import fr.cedric.garcia.library.R
import fr.cedric.garcia.library.cart.BookCartItem
import fr.cedric.garcia.library.cart.ShoppingCart

class BookItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private lateinit var titleView: TextView
    private lateinit var priceView: TextView
    private lateinit var coverImageView: ImageView
    private lateinit var addToCartButton: ImageButton

    override fun onFinishInflate() {
        super.onFinishInflate()
        titleView = findViewById(R.id.titleTextView)
        priceView = findViewById(R.id.priceTextView)
        coverImageView = findViewById(R.id.coverImageView)
        addToCartButton = findViewById(R.id.addToCartButton)
    }

    fun bindView(book: Book) {
        titleView.text = book.title
        priceView.text = "${book.price} €"

        Picasso.get().load(book.cover).into(coverImageView)

        addToCartButton.setOnClickListener {
            ShoppingCart.addItem(BookCartItem(book))
            Toast.makeText(context, R.string.book_added_to_cart, Toast.LENGTH_SHORT).show()
        }
    }
}
