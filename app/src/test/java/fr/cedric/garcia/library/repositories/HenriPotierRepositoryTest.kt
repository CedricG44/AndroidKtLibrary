package fr.cedric.garcia.library.repositories

import arrow.core.Either
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.services.HenriPotierService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HenriPotierRepositoryTest {

    @RelaxedMockK
    private lateinit var service: HenriPotierService

    private lateinit var repository: HenriPotierRepository

    private val books = listOf(
        Book(
            "c8fabf68-8374-48fe-a7ea-a00ccd07afff",
            "Henri Potier à l'école des sorciers",
            "35",
            "http://henri-potier.xebia.fr/hp0.jpg"
        ),
        Book(
            "a460afed-e5e7-4e39-a39d-c885c05db861",
            "Henri Potier et la Chambre des secrets",
            "30",
            "http://henri-potier.xebia.fr/hp1.jpg"
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = HenriPotierRepository(service)
    }

    @Test
    fun getEmptyListOfBooks() {
        every { runBlocking { service.getBooksAsync().await() } } returns emptyList()
        assertEquals(runBlocking { repository.getBooks() }, Either.right(emptyList<Book>()))
    }

    @Test
    fun getListOfBooks() {
        every { runBlocking { service.getBooksAsync().await() } } returns books
        assertEquals(runBlocking { repository.getBooks() }, Either.right(books))
    }

    @Test
    fun getListOfBooksError() {
        val exception = Exception("Error")
        every { runBlocking { service.getBooksAsync().await() } } throws exception
        assertEquals(runBlocking { repository.getBooks() }, Either.left(exception))
    }
}
