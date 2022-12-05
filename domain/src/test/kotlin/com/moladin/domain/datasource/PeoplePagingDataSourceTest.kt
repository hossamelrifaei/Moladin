package com.moladin.domain.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.moladin.data.remote.PeopleRemote
import com.moladin.domain.model.PeopleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

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
        val closestPageToPosition:PagingSource.LoadResult.Page<Int, PeopleEntity.Data> = mock()
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
        val closestPageToPosition:PagingSource.LoadResult.Page<Int, PeopleEntity.Data> = mock()
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

}