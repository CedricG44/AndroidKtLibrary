package fr.cedric.garcia.library.cart

import android.util.Log
import arrow.core.Option
import fr.cedric.garcia.library.book.Book
import io.paperdb.Paper

/**
 * Library shopping cart.
 */
class ShoppingCart {

    companion object {
        private const val CART = "CART"

        /**
         * Add a [bookCartItem] to the cart.
         */
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

        /**
         * Remove a [bookCartItem] from the cart.
         */
        fun removeItem(bookCartItem: BookCartItem) {
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

        /**
         * Get current cart state.
         */
        fun getCart(): MutableList<BookCartItem> {
            return Paper.book().read(CART, mutableListOf())
        }

        /**
         * Get a specific [book] from the cart. May be absent.
         */
        fun getCartItem(book: Book): Option<BookCartItem> =
            Option.fromNullable(getCart().singleOrNull { it.book.isbn == book.isbn })

        /**
         * Override current cart state.
         */
        fun saveCart(cart: MutableList<BookCartItem>) {
            Paper.book().write(CART, cart)
            Log.d("cart", getCart().toString())
        }

        /**
         * Get current cart size.
         */
        fun getCartSize(): Int {
            var size = 0
            getCart().forEach {
                size += it.quantity;
            }

            return size
        }
    }
}
