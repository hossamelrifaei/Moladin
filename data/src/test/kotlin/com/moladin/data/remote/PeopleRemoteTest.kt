package com.moladin.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

@ExperimentalCoroutinesApi
class PeopleRemoteTest {
    private val mockWebServer: MockWebServer = MockWebServer()
    private lateinit var peopleService: PeopleService
    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var peopleRemote: PeopleRemote
    private val moshi = MoshiConverterFactory.create(
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        peopleService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(moshi)
            .build()
            .create(PeopleService::class.java)


    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getPeopleWhenServiceReturnsSuccessResponseWithPeopleList() {
        prepareServer("people_api_response.json", 200)
        runTest {
            val page = 1

            val peopleResponse = expectedWithPeople()
            peopleRemote = PeopleRemote(peopleService)


            val result = peopleRemote.getPeople(page)

            assertEquals(result, peopleResponse)
        }
    }

    @Test
    fun getPeopleWhenServiceReturnsSuccessResponseWithoutPeopleList() {
        prepareServer("people_api_response_empty.json", 200)
        runTest {
            val page = 4

            val peopleResponse = emptyExpected()
            peopleRemote = PeopleRemote(peopleService)


            val result = peopleRemote.getPeople(page)

            assertEquals(result, peopleResponse)
        }
    }

    @Test
    fun getPeopleWhenServiceReturnsError() {
        prepareServer(null, 404)
        runTest {

            peopleRemote = PeopleRemote(peopleService)

            runCatching {
                peopleRemote.getPeople(1)
            }.onFailure {
                assertTrue(it is HttpException)
            }

        }
    }


    private fun prepareServer(fileName: String?, code: Int) {
        mockWebServer.enqueue(
            MockResponse()
                .apply {
                    setResponseCode(code)
                    fileName?.let { setBody(readResourceAsString(it)) }

                }
        )
    }

    private fun emptyExpected(): PeopleResponse {
        return PeopleResponse(
            page = 4,
            perPage = 6,
            total = 12,
            totalPages = 2,
            data = emptyList(),
            support = PeopleResponse.Support(
                url = "https://reqres.in/#support-heading",
                text = "To keep ReqRes free, contributions towards server costs are appreciated!"
            )
        )
    }

    private fun expectedWithPeople(): PeopleResponse {
        return PeopleResponse(
            page = 1,
            perPage = 6,
            total = 12,
            totalPages = 2,
            data = listOf(
                PeopleResponse.Data(
                    id = 1,
                    email = "george.bluth@reqres.in",
                    firstName = "George",
                    lastName = "Bluth",
                    avatar = "https://reqres.in/img/faces/1-image.jpg"
                )
            ),
            support = PeopleResponse.Support(
                url = "https://reqres.in/#support-heading",
                text = "To keep ReqRes free, contributions towards server costs are appreciated!"
            )
        )
    }

    private fun readResourceAsString(fileName: String): String {
        val url = javaClass.classLoader?.getResource(fileName)
        val file = File(url?.path.orEmpty())
        return file.readText()
    }
}