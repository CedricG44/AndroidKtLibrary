package fr.cedric.garcia.library.offer

data class Offer(val type: String, val sliceValue: Number?, val value: Number)

data class CommercialOffer(val offers: List<Offer>)
