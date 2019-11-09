package fr.cedric.garcia.library

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.book.BookAdapter
import fr.cedric.garcia.library.repositories.HenriPotierRepository
import fr.cedric.garcia.library.services.HenriPotierService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryActivity : AppCompatActivity() {

    private val booksRepository = HenriPotierRepository(HenriPotierService.service)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bookListRecyclerView = findViewById<RecyclerView>(R.id.bookListView)
        bookListRecyclerView.layoutManager = LinearLayoutManager(this)
        createBookList(bookListRecyclerView)
    }

    private fun createBookList(bookList: RecyclerView) =
        CoroutineScope(Dispatchers.Main).launch {
            val books = booksRepository.getBooks()
            var bookAdapter = BookAdapter(this@LibraryActivity, emptyList())

            withContext(Dispatchers.IO) {
                bookAdapter = BookAdapter(
                    this@LibraryActivity,
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
}
