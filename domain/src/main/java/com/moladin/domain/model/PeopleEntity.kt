package com.moladin.domain.model

data class PeopleEntity(
    val page: Int? = null,
    val perPage: Int? = null,
    val total: Int? = null,
    val totalPages: Int? = null,
    val data: List<Data> = arrayListOf(),
    val support: Support? = Support()
) {
    data class Data(
        val id: Int? = null,
        val email: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,
        val avatar: String? = null

    )

    data class Support(
        val url: String? = null,
        val text: String? = null
    )
}