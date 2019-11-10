package fr.cedric.garcia.library

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.fragments.BookDetailsFragment
import fr.cedric.garcia.library.fragments.BookListFragment
import fr.cedric.garcia.library.offer.CommercialOffer
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

    private suspend fun loadCommercialOffers(books: List<Book>): CommercialOffer =
        coroutineScope {
            val offers = async { booksRepository.getCommercialOffers(books.map { it.isbn }) }
            withContext(Dispatchers.IO) {
                offers.await().fold({
                    Log.e("getCommercialOffers", it.message, it)
                    CommercialOffer(emptyList())
                }, {
                    Log.d("offers", it.toString())
                    it
                })
            }
        }
}
