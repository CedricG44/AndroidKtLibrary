package fr.cedric.garcia.library

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
    private var dualPane = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Paper.init(this)

        books = savedInstanceState?.getParcelableArrayList<Book>(BOOKS)?.toList()
            ?: runBlocking { loadBookList() }
        val offers = runBlocking { loadCommercialOffers(books) }

        val detailsFrameLayout = findViewById<View>(R.id.bookDetailsContainerFrameLayout)
        dualPane = detailsFrameLayout != null && detailsFrameLayout.visibility == View.VISIBLE

        val fragment = BookListFragment()
        val args = Bundle()
        args.putParcelableArrayList(BOOKS, ArrayList(books))
        fragment.arguments = args

        if (dualPane) {
            replaceFrameLayout(R.id.bookListContainerFrameLayout, fragment)
            replaceFrameLayout(R.id.bookDetailsContainerFrameLayout, BookDetailsFragment())
        } else {
            replaceFrameLayout(R.id.bookListContainerFrameLayout, fragment)
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(BOOKS, ArrayList(books))
        super.onSaveInstanceState(outState)
    }

    override fun onOpenBookDetails(book: Book) {
        val fragment = BookDetailsFragment()
        val args = Bundle()
        args.putParcelable(BOOK, book)
        fragment.arguments = args

        if (dualPane) {
            replaceFrameLayout(R.id.bookDetailsContainerFrameLayout, fragment)
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.bookListContainerFrameLayout, fragment)
                .addToBackStack(BookDetailsFragment::class.java.name)
                .commit()
        }
    }

    private fun replaceFrameLayout(id: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(id, fragment)
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
