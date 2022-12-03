package com.moladin.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.moladin.domain.model.PeopleEntity
import com.moladin.presentation.databinding.PersonListItemBinding
import javax.inject.Inject

class PeoplePagingAdapter @Inject constructor(
) : PagingDataAdapter<PeopleEntity.Data, PersonVH>(PERSON_DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonVH {
        val binding =
            PersonListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PersonVH(binding)
    }

    override fun onBindViewHolder(holder: PersonVH, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }


    companion object PERSON_DIFF_CALLBACK : DiffUtil.ItemCallback<PeopleEntity.Data>() {
        override fun areItemsTheSame(oldItem: PeopleEntity.Data, newItem: PeopleEntity.Data) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PeopleEntity.Data, newItem: PeopleEntity.Data) =
            oldItem == newItem
    }

}