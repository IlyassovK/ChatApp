package com.example.messenger.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.model.Repository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginSignupViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    val userLiveData: MutableLiveData<FirebaseUser?> = repository.userLiveData

    fun isUserSignedIn(): Boolean {
        return repository.getCurrentUserId() != null
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun register(email: String, password: String, fullName: String){
        repository.register(email, password, fullName)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun login(email: String, password: String){
        repository.login(email, password)
    }
}