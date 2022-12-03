package com.moladin.presentation

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.moladin.domain.model.PeopleEntity
import com.moladin.presentation.databinding.PersonListItemBinding
import com.moladin.ui_common.ui.load

class PersonVH(private val binding: PersonListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(person: PeopleEntity.Data) {
        binding.apply {
            personImg.load(person.avatar)
            personNameTv.text = "${person.firstName} ${person.lastName}"
        }

    }
}
