package com.moladin.presentation

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.LoadState
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.moladin.domain.datasource.PeoplePagingDataSource
import com.moladin.domain.model.PeopleEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class PeopleListViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val pagingDataSource: PeoplePagingDataSource = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun testLoadPeople() {
        runTest(dispatcher) {
            val expectedLoadResult = PagingSource.LoadResult.Page(
                data = expectedWithPeople(),
                prevKey = 1,
                nextKey = 2
            )
            val expectedPeople = expectedWithPeople()
            whenever(
                pagingDataSource.load(
                    PagingSource.LoadParams.Prepend<Int>(
                        1,
                        6,
                        false
                    )
                )
            ).thenReturn(
                expectedLoadResult
            )
            val peopleListViewModel = PeopleListViewModel(pagingDataSource)
            val differ = AsyncPagingDataDiffer(
                diffCallback = MyDiffCallback(),
                updateCallback = NoopListCallback(),
                workerDispatcher = Dispatchers.Main
            )
            peopleListViewModel.viewState.onEach {
                differ.submitData(it)
                advanceUntilIdle()
                assertEquals(expectedPeople, differ.snapshot().items)
            }.launchIn(CoroutineScope(dispatcher))

        }

    }

    /**Should set loading state to notloading when the refresh is not loading*/
    @Test
    fun onAdapterLoadStateChangedWhenRefreshIsNotLoadingThenSetLoadingStateToNotLoading() =
        runTest {
            val viewModel = PeopleListViewModel(mock())
            viewModel.onAdapterLoadStateChanged(LoadState.NotLoading(true))
            val result = viewModel.loadingState.first()

            assertTrue((result is PeopleListViewModel.STATE.NOTLOADING))
        }

    /**Should set loading state to loading when the refresh is loading*/
    @Test
    fun onAdapterLoadStateChangedWhenRefreshIsLoadingThenSetLoadingStateToLoading() = runTest {
        val viewModel = PeopleListViewModel(mock())
        viewModel.onAdapterLoadStateChanged(LoadState.Loading)
        val result = viewModel.loadingState.first()

        assertTrue((result is PeopleListViewModel.STATE.LOADING))
    }

    /**Should set loading state to error when the refresh is error*/
    @Test
    fun onAdapterLoadStateChangedWhenRefreshIsErrorThenSetLoadingStateToError() = runTest {

        val viewModel = PeopleListViewModel(mock())
        viewModel.onAdapterLoadStateChanged(LoadState.Error(mock()))
        val result = viewModel.loadingState.first()
        assertTrue((result is PeopleListViewModel.STATE.ERROR))
    }

    private fun expectedWithPeople(): List<PeopleEntity.Data> {
        return listOf(
            PeopleEntity.Data(
                id = 1,
                email = "george.bluth@reqres.in",
                firstName = "George",
                lastName = "Bluth",
                avatar = "https://reqres.in/img/faces/1-image.jpg"
            )
        )
    }


    class NoopListCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
    }

    class MyDiffCallback : DiffUtil.ItemCallback<PeopleEntity.Data>() {
        override fun areItemsTheSame(
            oldItem: PeopleEntity.Data,
            newItem: PeopleEntity.Data
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PeopleEntity.Data,
            newItem: PeopleEntity.Data
        ): Boolean {
            return oldItem == newItem
        }
    }

}
