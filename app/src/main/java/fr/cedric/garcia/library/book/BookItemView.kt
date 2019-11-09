package fr.cedric.garcia.library.book

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.cardview.widget.CardView
import fr.cedric.garcia.library.R

class BookItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private lateinit var titleView: TextView
    private lateinit var priceView: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        titleView = findViewById(R.id.titleTextView)
        priceView = findViewById(R.id.priceTextView)
    }

    fun bindView(book: Book) {
        titleView.text = book.title
        priceView.text = book.price
    }
}
