package com.example.givetakeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.givetakeapp.MainApp
import com.example.givetakeapp.SharedData
import com.example.givetakeapp.data.User
import com.example.givetakeapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    private fun getUser() {
        var user: User;
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        viewModelScope.launch {
            runBlocking {
                user = MainApp.database.userDao().getUserByEmail(SharedData.myVariable)
            }
            _user.emit(Resource.Success(user))
        }
    }

    fun logout() {
        auth.signOut()
    }
}