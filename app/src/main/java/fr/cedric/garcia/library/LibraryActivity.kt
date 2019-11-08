package fr.cedric.garcia.library

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.cedric.garcia.library.book.BookAdapter
import fr.cedric.garcia.library.repositories.HenriPotierRepository
import fr.cedric.garcia.library.services.HenriPotierService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryActivity : AppCompatActivity() {

    private val booksRepository = HenriPotierRepository(HenriPotierService.service)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listRecyclerView = findViewById<RecyclerView>(R.id.bookListView)
        listRecyclerView.layoutManager = LinearLayoutManager(this)


        CoroutineScope(Dispatchers.Main).launch {
            val books = booksRepository.getBooks()
            withContext(Dispatchers.IO) {
                books.forEach {
                    Log.d("book", it.toString())
                }
            }
            listRecyclerView.adapter = BookAdapter(this@LibraryActivity, books)
        }
    }
}
