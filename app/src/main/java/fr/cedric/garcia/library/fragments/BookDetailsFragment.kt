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

    private lateinit var titleView: TextView
    private lateinit var coverImageView: ImageView
    private lateinit var synopsisTitleView: TextView
    private lateinit var synopsisView: TextView
    private lateinit var book: Book

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.book_details_fragment, container, false)
        book = arguments?.getParcelable(LibraryActivity.BOOK) ?: Book("", "", "", "", emptyList())

        titleView = view.findViewById(R.id.detailsTitleTextView)
        coverImageView = view.findViewById(R.id.detailsCoverImageView)
        synopsisTitleView = view.findViewById(R.id.detailsSynopsisTitleTextView)
        synopsisView = view.findViewById(R.id.detailsSynopsisTextView)

        if (!book.title.isBlank()) {
            Log.d("BookDetailsFragment", book.toString())

            titleView.text = book.title
            Picasso.get().load(book.cover).into(coverImageView)
            synopsisTitleView.text = getString(R.string.synopsis_title)
            synopsisView.text = book.synopsis.reduce { acc, s ->
                "$acc\n\n$s"
            }
        } else {
            titleView.text = getString(R.string.no_book_selected)
        }

        return view
    }
}
