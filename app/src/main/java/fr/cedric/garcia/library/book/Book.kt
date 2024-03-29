package fr.cedric.garcia.library.book

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Book model class.
 */
@Parcelize
data class Book(
    val isbn: String,
    val title: String,
    val price: String,
    val cover: String,
    val synopsis: List<String>
) :
    Parcelable
