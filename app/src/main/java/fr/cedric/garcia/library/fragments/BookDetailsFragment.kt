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
    private lateinit var synopsisView: TextView
    private lateinit var book: Book

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.book_details_fragment, container, false)
        book = arguments?.getParcelable(LibraryActivity.BOOK) ?: Book("", "", "", "", emptyList())
        Log.d("BookDetailsFragment", book.toString())

        titleView = view.findViewById(R.id.detailsTitleTextView)
        titleView.text = book.title

        coverImageView = view.findViewById(R.id.detailsCoverImageView)
        Picasso.get().load(book.cover).into(coverImageView)

        synopsisView = view.findViewById(R.id.detailsSynopsisTextView)
        book.synopsis.forEach {
            synopsisView.text = "${synopsisView.text}\n\n$it"
        }

        return view
    }
}
