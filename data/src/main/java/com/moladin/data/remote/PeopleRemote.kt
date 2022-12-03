package com.moladin.data.remote

import javax.inject.Inject

class PeopleRemote @Inject constructor(private val peopleService: PeopleService) {
    suspend fun getPeople(page: Int): PeopleResponse {
        return peopleService.getPeople(page)
    }
}
