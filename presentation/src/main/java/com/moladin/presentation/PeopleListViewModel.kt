package com.moladin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.moladin.domain.datasource.PeoplePagingDataSource
import com.moladin.domain.model.PeopleEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PeopleListViewModel @Inject constructor(private val pagingDataSource: PeoplePagingDataSource) :
    ViewModel() {
    private val _viewState = MutableStateFlow(PagingData.empty<PeopleEntity.Data>())
    val viewState: MutableStateFlow<PagingData<PeopleEntity.Data>> = _viewState

    init {
        loadPeople()
    }

   private fun loadPeople() {
        Pager(config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { pagingDataSource }
        ).flow.cachedIn(viewModelScope).onEach {
            _viewState.value = it
        }.launchIn(viewModelScope)
    }


    companion object {
        private const val PAGE_SIZE = 6
    }
}