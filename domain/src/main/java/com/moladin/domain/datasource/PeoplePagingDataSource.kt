package com.moladin.domain.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.moladin.data.remote.PeopleRemote
import com.moladin.domain.mapper.toEntity
import com.moladin.domain.model.PeopleEntity
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@ViewModelScoped
open class PeoplePagingDataSource @Inject constructor(private val peopleRemote: PeopleRemote) :
    PagingSource<Int, PeopleEntity.Data>() {

    override fun getRefreshKey(state: PagingState<Int, PeopleEntity.Data>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PeopleEntity.Data> {
        return try {
            val response = peopleRemote.getPeople(params.key ?: 1).toEntity()
            val next = (params.key ?: 1)
            LoadResult.Page(
                data = response.data,
                prevKey = if (next == 1) null else params.key,
                nextKey = if (next == response.totalPages) null else next + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}