package fr.cedric.garcia.library.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.cedric.garcia.library.LibraryActivity
import fr.cedric.garcia.library.R
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.book.BookAdapter

/**
 * Book list Fragment.
 */
class BookListFragment : Fragment() {

    private lateinit var listener: OnOpenBookDetailsListener
    private lateinit var bookListContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        bookListContext = context
        listener = context as OnOpenBookDetailsListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.book_list_fragment, container, false)
        val books =
            arguments?.getParcelableArrayList<Book>(LibraryActivity.BOOKS)?.toList() ?: emptyList()

        // Setup RecyclerView and CardView click listener
        val bookListRecyclerView = view.findViewById<RecyclerView>(R.id.bookListView)
        bookListRecyclerView.layoutManager = LinearLayoutManager(bookListContext)
        bookListRecyclerView.adapter =
            BookAdapter(bookListContext, books) { book ->
                Log.d("BookListFragment", "Clicked on book  $book")
                listener.onOpenBookDetails(book)
            }

        return view
    }

    /**
     * Open book details event handler interface.
     */
    interface OnOpenBookDetailsListener {
        fun onOpenBookDetails(book: Book)
    }
}
