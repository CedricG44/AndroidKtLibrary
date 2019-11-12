package fr.cedric.garcia.library.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import fr.cedric.garcia.library.LibraryActivity
import fr.cedric.garcia.library.R
import fr.cedric.garcia.library.book.Book

class BookDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.book_details_fragment, container, false)
        val book =
            arguments?.getParcelable(LibraryActivity.BOOK) ?: Book("", "", "", "", emptyList())

        val titleView = view.findViewById<TextView>(R.id.detailsTitleTextView)
        val coverImageView = view.findViewById<ImageView>(R.id.detailsCoverImageView)
        val synopsisTitleView = view.findViewById<TextView>(R.id.detailsSynopsisTitleTextView)
        val synopsisView = view.findViewById<TextView>(R.id.detailsSynopsisTextView)

        if (!book.title.isBlank()) {
            Log.d("BookDetailsFragment", book.toString())

            titleView.text = book.title
            Picasso.get().load(book.cover).into(coverImageView)
            synopsisTitleView.text = getString(R.string.synopsis_title)
            synopsisView.text = book.synopsis.joinToString("\n\n")
        } else {
            titleView.text = getString(R.string.no_book_selected)
        }

        return view
    }
}
