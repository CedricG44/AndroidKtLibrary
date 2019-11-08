package fr.cedric.garcia.library.services

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import fr.cedric.garcia.library.book.Book
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface HenriPotierService {

    @GET("books")
    fun getBooks(): Deferred<List<Book>>

    /*@GET("books/{isbns}/commercialOffers")
    fun getCommercialOffersByBook(): Deferred<List<CommercialOffer>>*/

    companion object {
        private const val baseUrl = "http://henri-potier.xebia.fr/"
        val service = Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
            .create(HenriPotierService::class.java)
    }
}
