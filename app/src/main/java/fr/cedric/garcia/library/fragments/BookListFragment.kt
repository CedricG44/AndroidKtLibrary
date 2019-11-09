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
import fr.cedric.garcia.library.R
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.book.BookAdapter
import fr.cedric.garcia.library.repositories.HenriPotierRepository
import fr.cedric.garcia.library.services.HenriPotierService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookListFragment : Fragment() {

    private val booksRepository = HenriPotierRepository(HenriPotierService.service)
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
        val bookListRecyclerView = view.findViewById<RecyclerView>(R.id.bookListView)
        bookListRecyclerView.layoutManager = LinearLayoutManager(bookListContext)
        createBookList(bookListRecyclerView)
        return view
    }

    private fun createBookList(bookList: RecyclerView) =
        CoroutineScope(Dispatchers.Main).launch {
            val books = booksRepository.getBooks()
            var bookAdapter = BookAdapter(bookListContext, emptyList())

            withContext(Dispatchers.IO) {
                bookAdapter = BookAdapter(
                    bookListContext,
                    books.fold({
                        Log.e("getBooks", it.message, it)
                        emptyList<Book>()
                    }, {
                        it.forEach { book ->
                            Log.d("book", book.toString())
                        }
                        it
                    })
                )
            }

            bookList.adapter = bookAdapter
        }

    interface OnOpenBookDetailsListener {
        fun onOpenBookDetails()
    }
}
