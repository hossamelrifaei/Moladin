package com.moladin.presentation


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.moladin.presentation.databinding.FragmentPeopleListBinding
import com.moladin.ui_common.ui.makeGone
import com.moladin.ui_common.ui.makeVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PeopleListFragment : Fragment(R.layout.fragment_people_list) {

    private lateinit var peopleListAdapter: PeoplePagingAdapter
    private val viewModel: PeopleListViewModel by viewModels()

    private var _binding: FragmentPeopleListBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentPeopleListBinding.bind(view)

        setUpRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collectLatest { viewState ->
                    peopleListAdapter.submitData(viewState)
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        peopleListAdapter = PeoplePagingAdapter()
        binding.peopleList.adapter = peopleListAdapter
        binding.btnRefresh.setOnClickListener {
            peopleListAdapter.retry()
        }

        peopleListAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.NotLoading -> {
                    binding.tvError.makeGone()
                    binding.btnRefresh.makeGone()
                    binding.progress.makeGone()
                }
                LoadState.Loading -> {
                    binding.tvError.makeGone()
                    binding.btnRefresh.makeGone()
                    binding.progress.makeVisible()
                }
                is LoadState.Error -> {
                    binding.tvError.makeVisible()
                    binding.btnRefresh.makeVisible()
                    binding.progress.makeGone()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
