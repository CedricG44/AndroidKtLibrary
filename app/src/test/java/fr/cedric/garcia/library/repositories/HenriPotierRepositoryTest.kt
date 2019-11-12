package fr.cedric.garcia.library.repositories

import arrow.core.Either
import fr.cedric.garcia.library.book.Book
import fr.cedric.garcia.library.book.CommercialOffer
import fr.cedric.garcia.library.book.Offer
import fr.cedric.garcia.library.services.HenriPotierService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Books repository tests.
 */
class HenriPotierRepositoryTest {

    @RelaxedMockK
    private lateinit var service: HenriPotierService

    private lateinit var repository: HenriPotierRepository

    private val books = listOf(
        Book(
            "c8fabf68-8374-48fe-a7ea-a00ccd07afff",
            "Henri Potier à l'école des sorciers",
            "35",
            "http://henri-potier.xebia.fr/hp0.jpg",
            listOf("Description")
        ),
        Book(
            "a460afed-e5e7-4e39-a39d-c885c05db861",
            "Henri Potier et la Chambre des secrets",
            "30",
            "http://henri-potier.xebia.fr/hp1.jpg",
            listOf("Description")
        )
    )

    private val isbn = listOf(
        "c8fabf68-8374-48fe-a7ea-a00ccd07afff",
        "a460afed-e5e7-4e39-a39d-c885c05db861"
    )

    private val formattedIsbn = isbn.joinToString(separator = ",")

    private val offers =
        CommercialOffer(
            listOf(
                Offer(
                    "percentage",
                    null,
                    10
                ), Offer("minus", null, 5)
            )
        )

    private val exception = Exception("Error")

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
        every { runBlocking { service.getBooksAsync().await() } } throws exception
        assertEquals(runBlocking { repository.getBooks() }, Either.left(exception))
    }

    @Test
    fun getEmptyCommercialOffers() {
        every {
            runBlocking {
                service.getCommercialOffersAsync(formattedIsbn).await()
            }
        } returns CommercialOffer(emptyList())
        assertEquals(
            runBlocking { repository.getCommercialOffers(isbn) },
            Either.right(CommercialOffer(emptyList()))
        )
    }

    @Test
    fun getCommercialOffers() {
        every {
            runBlocking {
                service.getCommercialOffersAsync(formattedIsbn).await()
            }
        } returns offers
        assertEquals(
            runBlocking { repository.getCommercialOffers(isbn) },
            Either.right(offers)
        )
    }

    @Test
    fun getCommercialOffersError() {
        every {
            runBlocking {
                service.getCommercialOffersAsync(formattedIsbn).await()
            }
        } throws exception
        assertEquals(runBlocking { repository.getCommercialOffers(isbn) }, Either.left(exception))
    }
}
