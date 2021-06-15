package com.example.messenger.viewmodel

import android.content.Context
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.messenger.R
import com.example.messenger.model.Repository
import com.example.messenger.model.dataModel.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
): ViewModel(){

    init {
        getAllUsers()
    }

    val logOutLiveData: MutableLiveData<Boolean> = repository.loggOutLiveData

    val userListLiveData: MutableLiveData<List<User>> = repository.allUsersLiveData

    fun startChat(view: View,receiverUser: User){
        val bundle = bundleOf("receiverUser" to receiverUser)
        view.findNavController().navigate(R.id.action_userListFragment_to_chatFragment, bundle)
    }


    fun getAllUsers(){
        GlobalScope.launch(Dispatchers.IO) {
            repository.getAllUsers()
        }
    }

    fun logOut(){
        repository.loggOut()
    }
}