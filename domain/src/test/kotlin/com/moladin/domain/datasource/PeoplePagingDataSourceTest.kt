package com.moladin.domain.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.moladin.data.remote.PeopleRemote
import com.moladin.data.remote.PeopleResponse
import com.moladin.domain.model.PeopleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import retrofit2.HttpException

@ExperimentalCoroutinesApi
class PeoplePagingDataSourceTest {

    private val peopleRemote: PeopleRemote = mock()
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var peoplePagingDataSource: PeoplePagingDataSource

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        peoplePagingDataSource = PeoplePagingDataSource(peopleRemote)

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**Should return the next key when the anchor position is not null and the closest page to position is not null and the prev key is not null*/
    @Test
    fun getRefreshKeyWhenAnchorPositionIsNotNullAndClosestPageToPositionIsNotNullAndPrevKeyIsNotNullThenReturnNextKey() {
        val state: PagingState<Int, PeopleEntity.Data> = mock()
        val anchorPosition = 1
        val closestPageToPosition: PagingSource.LoadResult.Page<Int, PeopleEntity.Data> = mock()
        val prevKey = 1
        val nextKey = 2
        whenever(state.anchorPosition).thenReturn(anchorPosition)
        whenever(state.closestPageToPosition(anchorPosition)).thenReturn(closestPageToPosition)
        whenever(closestPageToPosition.prevKey).thenReturn(prevKey)
        whenever(closestPageToPosition.nextKey).thenReturn(nextKey)

        val result = peoplePagingDataSource.getRefreshKey(state)

        assertEquals(nextKey, result)
        verifyNoMoreInteractions(peopleRemote)
    }

    /**Should return the previous key when the anchor position is not null and the closest page to position is not null and the next key is not null*/
    @Test
    fun getRefreshKeyWhenAnchorPositionIsNotNullAndClosestPageToPositionIsNotNullAndNextKeyIsNotNullThenReturnPreviousKey() {
        val state: PagingState<Int, PeopleEntity.Data> = mock()
        val anchorPosition = 1
        val closestPageToPosition: PagingSource.LoadResult.Page<Int, PeopleEntity.Data> = mock()
        val previousKey = 1
        val nextKey = 2
        whenever(state.anchorPosition).thenReturn(anchorPosition)
        whenever(state.closestPageToPosition(anchorPosition)).thenReturn(closestPageToPosition)
        whenever(closestPageToPosition.prevKey).thenReturn(previousKey)
        whenever(closestPageToPosition.nextKey).thenReturn(nextKey)

        val result = peoplePagingDataSource.getRefreshKey(state)

        assertEquals(previousKey + 1, result)
        verifyNoMoreInteractions(peopleRemote)
    }

    /**Should return an error when the request is failed*/
    @Test
    fun loadWhenRequestIsFailedThenReturnError() = runTest {
        val params: PagingSource.LoadParams<Int> = mock()
        val exception: HttpException = mock()
        whenever(params.key).thenReturn(1)
        whenever(peopleRemote.getPeople(1)).thenThrow(exception)

        val result = peoplePagingDataSource.load(params)

        assertTrue(result is PagingSource.LoadResult.Error)
        verify(peopleRemote).getPeople(any())
        verifyNoMoreInteractions(peopleRemote)
    }

    /**Should return a page of data when the request is successful*/
    @Test
    fun loadWhenRequestIsSuccessfulThenReturnPageOfData() = runTest {
        val params: PagingSource.LoadParams<Int> = PagingSource.LoadParams.Refresh(3, 1, false)
        val peopleEntity = PeopleEntity(
            page = 1,
            perPage = 1,
            total = 2,
            totalPages = 2,
            data = listOf(
                PeopleEntity.Data(
                    id = 1,
                    email = "george.bluth@reqres.in",
                    firstName = "George",
                    lastName = "Bluth",
                    avatar = "https://reqres.in/img/faces/1-image.jpg"
                )
            ),
            support = PeopleEntity.Support("", "")
        )

        whenever(peopleRemote.getPeople(3)).thenReturn(expectedWithPeople())

        val result = peoplePagingDataSource.load(params)

        assertEquals(
            result,
            PagingSource.LoadResult.Page(data = peopleEntity.data, prevKey = 3, nextKey = 4)
        )

    }

    private fun expectedWithPeople(): PeopleResponse {
        return PeopleResponse(
            page = 3,
            perPage = 1,
            total = 3,
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
}