package fr.cedric.garcia.library.book

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.cedric.garcia.library.R

class BookAdapter(context: Context, private val books: List<Book>) :
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
        holder.item.bindView(books[position])
    }

    class BookViewHolder(val item: BookItemView) : RecyclerView.ViewHolder(item)
}
