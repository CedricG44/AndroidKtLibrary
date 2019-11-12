package fr.cedric.garcia.library.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.cedric.garcia.library.R

/**
 * Book item adapter for shopping cart RecyclerView.
 */
class ShoppingCartAdapter(
    context: Context,
    private val bookItems: List<BookCartItem>
) :
    RecyclerView.Adapter<ShoppingCartAdapter.BookCartItemViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookCartItemViewHolder {
        return BookCartItemViewHolder(
            inflater.inflate(
                R.layout.book_cart_item_view,
                parent,
                false
            ) as BookCartItemView
        )
    }

    override fun getItemCount(): Int = bookItems.count()

    override fun onBindViewHolder(holder: BookCartItemViewHolder, position: Int) {
        // Bind book cart item view
        holder.item.bindView(bookItems[position])
    }

    class BookCartItemViewHolder(val item: BookCartItemView) : RecyclerView.ViewHolder(item)
}
