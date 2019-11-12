package fr.cedric.garcia.library.book

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Offer(val type: String, val sliceValue: Number?, val value: Number) : Parcelable

@Parcelize
data class CommercialOffer(val offers: List<Offer>) : Parcelable
