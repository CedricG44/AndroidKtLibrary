package fr.cedric.garcia.library.repositories

import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.services.HenriPotierService

class HenriPotierRepository(private val service: HenriPotierService) {

    suspend fun getBooks(): List<Book> {
        return service.getBooks().await()
    }
}
