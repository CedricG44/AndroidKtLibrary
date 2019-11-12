package fr.cedric.garcia.library.repositories

import arrow.core.Either
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.book.CommercialOffer
import fr.cedric.garcia.library.services.HenriPotierService
import kotlinx.coroutines.Deferred

/**
 * Books repository.
 */
class HenriPotierRepository(private val service: HenriPotierService) {

    /**
     * Get books from service.
     */
    suspend fun getBooks(): Either<Throwable, List<Book>> = callService(service.getBooksAsync())

    /**
     * Get commercial offers from service given a list of [isbn].
     */
    suspend fun getCommercialOffers(isbn: List<String>): Either<Throwable, CommercialOffer> =
        callService(service.getCommercialOffersAsync(isbn.joinToString(",")))

    /**
     * Generic service call given the [deferred] function to call.
     */
    private suspend fun <K> callService(deferred: Deferred<K>): Either<Throwable, K> {
        // Handling errors with Either Monad
        return try {
            Either.right(deferred.await())
        } catch (ex: Exception) {
            Either.left(ex)
        }
    }
}
