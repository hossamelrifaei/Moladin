package com.moladin.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PeopleResponse(
    @Json(name = "page") var page: Int? = null,
    @Json(name = "per_page") var perPage: Int? = null,
    @Json(name = "total") var total: Int? = null,
    @Json(name = "total_pages") var totalPages: Int? = null,
    @Json(name = "data") var data: List<Data> = emptyList(),
    @Json(name = "support") var support: Support? = Support()
) {

    data class Data(
        @Json(name = "id") var id: Int? = null,
        @Json(name = "email") var email: String? = null,
        @Json(name = "first_name") var firstName: String? = null,
        @Json(name = "last_name") var lastName: String? = null,
        @Json(name = "avatar") var avatar: String? = null

    )

    data class Support(
        @Json(name = "url") var url: String? = null,
        @Json(name = "text") var text: String? = null
    )
}
