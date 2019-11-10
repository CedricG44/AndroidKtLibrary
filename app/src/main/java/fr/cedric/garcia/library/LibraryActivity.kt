package fr.cedric.garcia.library

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.fragments.BookDetailsFragment
import fr.cedric.garcia.library.fragments.BookListFragment
import fr.cedric.garcia.library.repositories.HenriPotierRepository
import fr.cedric.garcia.library.services.HenriPotierService
import io.paperdb.Paper
import kotlinx.coroutines.*


class LibraryActivity : AppCompatActivity(), BookListFragment.OnOpenBookDetailsListener {

    companion object {
        const val BOOK = "BOOK"
        const val BOOKS = "BOOKS"
    }

    private val booksRepository = HenriPotierRepository(HenriPotierService.service)
    private lateinit var books: List<Book>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Paper.init(this)

        books = runBlocking { loadBookList() }

        val fragment = BookListFragment()
        val args = Bundle()
        args.putParcelableArrayList(BOOKS, ArrayList(books))
        fragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFrameLayout, fragment)
            .commit()

        /*CoroutineScope(Dispatchers.Main).launch {
            val offer = booksRepository.getCommercialOffers(
                listOf(
                    "c8fabf68-8374-48fe-a7ea-a00ccd07afff",
                    "a460afed-e5e7-4e39-a39d-c885c05db861",
                    "bbcee412-be64-4a0c-bf1e-315977acd924"
                )
            )

            withContext(Dispatchers.IO) {
                offer.fold({
                    Log.e("getCommercialOffer", it.message, it)
                }, {
                    Log.d("offer", it.toString())
                    it
                })
            }
        }*/
    }

    override fun onOpenBookDetails(book: Book) {
        val fragment = BookDetailsFragment()
        val args = Bundle()
        args.putParcelable(BOOK, book)
        fragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFrameLayout, fragment)
            .addToBackStack(BookDetailsFragment::class.java.name)
            .commit()
    }

    private suspend fun loadBookList(): List<Book> =
        coroutineScope {
            val books = async { booksRepository.getBooks() }
            withContext(Dispatchers.IO) {
                books.await().fold({
                    Log.e("getBooks", it.message, it)
                    emptyList<Book>()
                }, {
                    it.forEach { book ->
                        Log.d("book", book.toString())
                    }
                    it
                })
            }
        }
}
