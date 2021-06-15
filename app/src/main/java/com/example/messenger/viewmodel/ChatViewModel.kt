package com.example.messenger.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.model.Repository
import com.example.messenger.model.dataModel.Message
import com.example.messenger.view.ui.ChatFragment
import com.google.firebase.auth.FirebaseUser
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: Repository,
): ViewModel() {

    val messagesLiveData: MutableLiveData<MutableList<Message>> = repository.messagesLiveData

      fun getMessages(receiverUserId: String){
        repository.getMessages(receiverUserId)
    }

    suspend fun sendMessage(messageText: String, receiverUserId: String){
        repository.addMessageToDb(messageText, receiverUserId)
    }

}