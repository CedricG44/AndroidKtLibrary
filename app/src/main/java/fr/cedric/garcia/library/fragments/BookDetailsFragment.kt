package fr.cedric.garcia.library.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.cedric.garcia.library.LibraryActivity
import fr.cedric.garcia.library.R
import fr.cedric.garcia.library.book.Book

class BookDetailsFragment : Fragment() {

    private lateinit var book: Book

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        book = arguments?.getParcelable(LibraryActivity.BOOK) ?: Book("", "", "", "")
        Log.d("BookDetailsFragment", book.toString())
        return inflater.inflate(R.layout.book_details_fragment, container, false)
    }
}
