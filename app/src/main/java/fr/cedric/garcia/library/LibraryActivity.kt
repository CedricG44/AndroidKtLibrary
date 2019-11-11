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
        const val LIST_FRAGMENT_TAG = "LIST_FRAGMENT_TAG"
        const val DETAILS_FRAGMENT_TAG = "DETAILS_FRAGMENT_TAG"
    }

    private val booksRepository = HenriPotierRepository(HenriPotierService.service)
    private lateinit var books: List<Book>
    private lateinit var listFragment: BookListFragment
    private lateinit var detailsFragment: BookDetailsFragment
    private var dualPane = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Paper.init(this)

        books = savedInstanceState?.getParcelableArrayList<Book>(BOOKS)?.toList()
            ?: runBlocking { loadBookList() }
        val offers = runBlocking { loadCommercialOffers(books) }

        // Check dual-pane frame availability
        val detailsFrameLayout = findViewById<View>(R.id.bookDetailsContainerFrameLayout)
        dualPane = detailsFrameLayout != null && detailsFrameLayout.visibility == View.VISIBLE

        // Handle fragments states
        detailsFragment = BookDetailsFragment()
        listFragment = if (savedInstanceState == null) {
            createListFragment(books)
        } else {
            supportFragmentManager.findFragmentByTag(LIST_FRAGMENT_TAG) as BookListFragment
        }

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

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(BOOKS, ArrayList(books))
        super.onSaveInstanceState(outState)
    }

    override fun onOpenBookDetails(book: Book) {
        detailsFragment = createDetailsFragment(book)

        if (dualPane) {
            replaceFrameLayout(
                R.id.bookDetailsContainerFrameLayout,
                detailsFragment,
                DETAILS_FRAGMENT_TAG
            )
        } else {
            supportFragmentManager.beginTransaction()
                .add(R.id.bookListContainerFrameLayout, detailsFragment, DETAILS_FRAGMENT_TAG)
                .addToBackStack(BookDetailsFragment::class.java.name)
                .commit()
        }
    }

    private fun createListFragment(books: List<Book>): BookListFragment {
        val fragment = BookListFragment()
        val args = Bundle()
        args.putParcelableArrayList(BOOKS, ArrayList(books))
        fragment.arguments = args
        return fragment
    }

    private fun createDetailsFragment(book: Book): BookDetailsFragment {
        val fragment = BookDetailsFragment()
        val args = Bundle()
        args.putParcelable(BOOK, book)
        fragment.arguments = args
        return fragment
    }

    private fun replaceFrameLayout(id: Int, fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(id, fragment, tag)
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
