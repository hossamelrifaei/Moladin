package com.moladin.domain.mapper

import com.moladin.data.remote.PeopleResponse
import com.moladin.domain.model.PeopleEntity

fun PeopleResponse.toEntity(): PeopleEntity {
    return PeopleEntity(
        page = page,
        perPage = perPage,
        total = total,
        totalPages = totalPages,
        data = data.map { it.toModel() },
        support = support?.toModel()
    )
}

fun PeopleResponse.Data.toModel(): PeopleEntity.Data {
    return PeopleEntity.Data(
        id = id,
        email = email,
        firstName = firstName,
        lastName = lastName,
        avatar = avatar
    )
}

fun PeopleResponse.Support.toModel(): PeopleEntity.Support {
    return PeopleEntity.Support(
        url = url,
        text = text
    )
}