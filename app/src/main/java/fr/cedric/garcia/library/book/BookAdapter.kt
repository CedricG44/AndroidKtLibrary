package fr.cedric.garcia.library.book

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.cedric.garcia.library.R

/**
 * Book adapter for book RecyclerView.
 */
class BookAdapter(
    context: Context,
    private val books: List<Book>,
    private val bookClickListener: (Book) -> Unit
) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(
            inflater.inflate(
                R.layout.book_item_view,
                parent,
                false
            ) as BookItemView
        )
    }

    override fun getItemCount(): Int = books.count()

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        // Bind book item view and click listener
        holder.item.bindView(books[position])
        holder.item.setOnClickListener { bookClickListener(books[position]) }
    }

    class BookViewHolder(val item: BookItemView) : RecyclerView.ViewHolder(item)
}
