package com.example.givetakeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.givetakeapp.MainApp
import com.example.givetakeapp.SharedData
import com.example.givetakeapp.data.User
import com.example.givetakeapp.util.RegisterValidation
import com.example.givetakeapp.util.Resource
import com.example.givetakeapp.util.validateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    app: Application
) : AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()
    val updateInfo = _updateInfo.asStateFlow()

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

    fun updateUser(user: User) {
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()

        if (!areInputsValid) {
            viewModelScope.launch {
                _user.emit(Resource.Error("Check your inputs"))
            }
            return
        }

        viewModelScope.launch {
            _updateInfo.emit(Resource.Loading())
        }

        viewModelScope.launch {
            runBlocking {
                // user = MainApp.database.userDao().getAllUsers()

                MainApp.database.userDao().insertUser(user)
            }
            SharedData.myVariable = user.email
            _updateInfo.emit(Resource.Success(user))
        }
    }
}
