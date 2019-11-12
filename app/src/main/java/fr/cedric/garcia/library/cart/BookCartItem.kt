package fr.cedric.garcia.library.cart

import fr.cedric.garcia.library.book.Book

data class BookCartItem(val book: Book, var quantity: Int = 0)
