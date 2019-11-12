package fr.cedric.garcia.library.cart

import android.content.Context
import android.util.Log
import arrow.core.Option
import fr.cedric.garcia.library.book.Book
import io.paperdb.Paper

class ShoppingCart {

    companion object {
        private const val CART = "CART"

        fun addItem(bookCartItem: BookCartItem) {
            val cart = getCart()

            val targetItem = cart.singleOrNull { it.book.isbn == bookCartItem.book.isbn }
            if (targetItem == null) {
                bookCartItem.quantity++
                cart.add(bookCartItem)
            } else {
                targetItem.quantity++
            }

            saveCart(cart)
        }

        fun removeItem(bookCartItem: BookCartItem, context: Context) {
            val cart = getCart()

            val targetItem = cart.singleOrNull { it.book.isbn == bookCartItem.book.isbn }
            if (targetItem != null) {
                if (targetItem.quantity > 0) {
                    targetItem.quantity--
                } else {
                    cart.remove(targetItem)
                }
            }

            saveCart(cart)
        }

        fun getCart(): MutableList<BookCartItem> {
            return Paper.book().read(CART, mutableListOf())
        }

        fun getCartItem(book: Book): Option<BookCartItem> {
            val cart = getCart()
            return Option.fromNullable(cart.singleOrNull { it.book.isbn == book.isbn })
        }

        fun saveCart(cart: MutableList<BookCartItem>) {
            Paper.book().write(CART, cart)
            Log.d("cart", getCart().toString())
        }

        fun getCartSize(): Int {
            var size = 0
            getCart().forEach {
                size += it.quantity;
            }

            return size
        }
    }
}
