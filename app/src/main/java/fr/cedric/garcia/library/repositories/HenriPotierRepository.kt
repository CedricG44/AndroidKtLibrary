package fr.cedric.garcia.library.repositories

import arrow.core.Either
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.services.HenriPotierService

class HenriPotierRepository(private val service: HenriPotierService) {

    suspend fun getBooks(): Either<Throwable, List<Book>> {
        return try {
            Either.right(service.getBooks().await())
        } catch (ex: Exception) {
            Either.left(ex)
        }
    }
}
