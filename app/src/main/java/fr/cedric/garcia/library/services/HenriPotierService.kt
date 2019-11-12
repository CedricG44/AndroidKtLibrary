package fr.cedric.garcia.library.services

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.book.CommercialOffer
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Books API service Retrofit interface.
 */
interface HenriPotierService {

    companion object {
        val service: HenriPotierService = Retrofit
            .Builder()
            .baseUrl("http://henri-potier.xebia.fr/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(HenriPotierService::class.java)
    }

    /**
     * Get books.
     */
    @GET("books")
    fun getBooksAsync(): Deferred<List<Book>>

    /**
     * Get commercial offers given a list of [isbn].
     */
    @GET("books/{isbn}/commercialOffers")
    fun getCommercialOffersAsync(@Path("isbn") isbn: String): Deferred<CommercialOffer>
}
