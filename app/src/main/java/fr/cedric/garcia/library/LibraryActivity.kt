package fr.cedric.garcia.library

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import arrow.core.Option
import arrow.core.some
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.cart.BookCartItem
import fr.cedric.garcia.library.cart.ShoppingCart
import fr.cedric.garcia.library.fragments.BookDetailsFragment
import fr.cedric.garcia.library.fragments.BookListFragment
import fr.cedric.garcia.library.repositories.HenriPotierRepository
import fr.cedric.garcia.library.services.HenriPotierService
import io.paperdb.Paper
import kotlinx.coroutines.*

/**
 * Main Activity.
 */
class LibraryActivity : AppCompatActivity(), BookListFragment.OnOpenBookDetailsListener {

    companion object {
        const val BOOK = "BOOK"
        const val BOOKS = "BOOKS"
        const val LIST_FRAGMENT_TAG = "LIST_FRAGMENT_TAG"
        const val DETAILS_FRAGMENT_TAG = "DETAILS_FRAGMENT_TAG"
    }

    private val booksRepository = HenriPotierRepository(HenriPotierService.service)
    private var books: List<Book> = emptyList()
    private var selectedBook: Option<Book> = Option.empty()
    private var dualPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init and empty cart
        Paper.init(this)
        ShoppingCart.saveCart(emptyList<BookCartItem>().toMutableList())

        // Handle saved book list and selected book
        books = savedInstanceState?.getParcelableArrayList<Book>(BOOKS)?.toList()
            ?: runBlocking { loadBookList() }
        selectedBook = savedInstanceState?.getParcelable<Book>(BOOK)?.some() ?: Option.empty()

        // Check dual-pane frame availability
        val detailsFrameLayout = findViewById<View>(R.id.bookDetailsContainerFrameLayout)
        dualPane = detailsFrameLayout != null && detailsFrameLayout.visibility == View.VISIBLE

        // Handle fragments states
        val listFragment = if (savedInstanceState == null) {
            createListFragment(books)
        } else {
            supportFragmentManager.findFragmentByTag(LIST_FRAGMENT_TAG) as BookListFragment
        }

        val detailsFragment = selectedBook.fold({
            BookDetailsFragment()
        }, {
            createDetailsFragment(it)
        })

        // Handle dual-pane horizontal mode
        if (dualPane) {
            replaceFrameLayout(R.id.bookListContainerFrameLayout, listFragment, LIST_FRAGMENT_TAG)
            replaceFrameLayout(
                R.id.bookDetailsContainerFrameLayout,
                detailsFragment,
                DETAILS_FRAGMENT_TAG
            )
        } else {
            replaceFrameLayout(R.id.bookListContainerFrameLayout, listFragment, LIST_FRAGMENT_TAG)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle shopping cart button action
        return when (item.itemId) {
            R.id.shoppingCart -> {
                startActivity(Intent(this, ShoppingCartActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save book list and selected book
        outState.putParcelableArrayList(BOOKS, ArrayList(books))
        selectedBook.fold({}, {
            outState.putParcelable(BOOK, it)
        })
        super.onSaveInstanceState(outState)
    }

    /**
     * Handle "open [book] details" event.
     */
    override fun onOpenBookDetails(book: Book) {
        selectedBook = book.some()
        val detailsFragment = createDetailsFragment(book)

        // Handle dual-pane horizontal mode
        if (dualPane) {
            replaceFrameLayout(
                R.id.bookDetailsContainerFrameLayout,
                detailsFragment,
                DETAILS_FRAGMENT_TAG
            )
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.bookListContainerFrameLayout, detailsFragment, DETAILS_FRAGMENT_TAG)
                .addToBackStack(BookDetailsFragment::class.java.name)
                .commit()
        }
    }

    /**
     * Create a BookListFragment given a list of [books].
     */
    private fun createListFragment(books: List<Book>): BookListFragment {
        val fragment = BookListFragment()
        val args = Bundle()
        args.putParcelableArrayList(BOOKS, ArrayList(books))
        fragment.arguments = args
        return fragment
    }

    /**
     * Create a BookDetailsFragment given a [book].
     */
    private fun createDetailsFragment(book: Book): BookDetailsFragment {
        val fragment = BookDetailsFragment()
        val args = Bundle()
        args.putParcelable(BOOK, book)
        fragment.arguments = args
        return fragment
    }

    /**
     * Replace FrameLayout with [id] by [fragment] givent its [tag].
     */
    private fun replaceFrameLayout(id: Int, fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(id, fragment, tag)
            .commit()
    }

    /**
     * Load book list from repository.
     */
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
