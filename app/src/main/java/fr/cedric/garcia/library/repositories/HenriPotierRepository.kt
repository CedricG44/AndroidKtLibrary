package fr.cedric.garcia.library.repositories

import arrow.core.Either
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.book.CommercialOffer
import fr.cedric.garcia.library.services.HenriPotierService
import kotlinx.coroutines.Deferred

class HenriPotierRepository(private val service: HenriPotierService) {

    suspend fun getBooks(): Either<Throwable, List<Book>> = callService(service.getBooksAsync())

    suspend fun getCommercialOffers(isbn: List<String>): Either<Throwable, CommercialOffer> =
        callService(service.getCommercialOffersAsync(isbn.joinToString(",")))

    private suspend fun <K> callService(deferred: Deferred<K>): Either<Throwable, K> {
        return try {
            Either.right(deferred.await())
        } catch (ex: Exception) {
            Either.left(ex)
        }
    }
}
