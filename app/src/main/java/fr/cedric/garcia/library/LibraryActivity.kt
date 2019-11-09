package fr.cedric.garcia.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.cedric.garcia.library.fragments.BookDetailsFragment
import fr.cedric.garcia.library.fragments.BookListFragment

class LibraryActivity : AppCompatActivity(), BookListFragment.OnOpenBookDetailsListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFrameLayout, BookListFragment())
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

    override fun onOpenBookDetails() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFrameLayout, BookDetailsFragment())
            .addToBackStack(BookDetailsFragment::class.java.name)
            .commit()
    }
}
