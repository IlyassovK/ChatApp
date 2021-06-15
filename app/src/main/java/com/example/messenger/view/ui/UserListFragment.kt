package com.example.messenger.view.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Adapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.R
import com.example.messenger.databinding.FragmentLoginBinding
import com.example.messenger.databinding.FragmentUserListBinding
import com.example.messenger.model.adapters.UserListAdapter
import com.example.messenger.viewmodel.LoginSignupViewModel
import com.example.messenger.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserListFragment : Fragment(R.layout.fragment_user_list) {

    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var userAdapter: UserListAdapter

    private lateinit var binding: FragmentUserListBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        binding = FragmentUserListBinding.bind(view)
        setupRecyclerView()
        subscribeToObservers(view)

        userAdapter.setItemClickListener {
            mainViewModel.startChat(view, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.buttonLogOut -> {
                mainViewModel.logOut()
                true
            }else -> {
                false
            }
        }
    }

    private fun setupRecyclerView() = binding.rvAllUser.apply {
        adapter = userAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers(view: View){
        mainViewModel.userListLiveData.observe(viewLifecycleOwner){ result ->
            result?.let {
                userAdapter.users = it
            }
        }

        mainViewModel.logOutLiveData.observe(viewLifecycleOwner){
            it?.let { isLogOut ->
                if(isLogOut){
                    view.findNavController().navigate(R.id.action_global_logOut);
                }
            }
        }

    }
}