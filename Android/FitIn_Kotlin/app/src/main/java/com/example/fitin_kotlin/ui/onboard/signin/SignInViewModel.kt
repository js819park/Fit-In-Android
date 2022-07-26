package com.example.fitin_kotlin.ui.onboard.signin

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitin_kotlin.data.local.EncryptedSharedPreferenceController
import com.example.fitin_kotlin.data.model.network.request.RequestSignIn
import com.example.fitin_kotlin.data.repository.NewsRepository
import com.example.fitin_kotlin.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val newsRepository: NewsRepository,
    private val prefs: EncryptedSharedPreferenceController
) : ViewModel(){

    val email: MutableLiveData<String> = MutableLiveData<String>()
    val password: MutableLiveData<String> = MutableLiveData<String>()

    private val _eventSignIn = MutableLiveData<Boolean>()
    val eventSignIn: LiveData<Boolean>
        get() = _eventSignIn

    fun onSignIn(view: View) {
        val requestSignIn = RequestSignIn(email.value, password.value)
        viewModelScope.launch {
            val signIn = userRepository.postSignIn(requestSignIn)
            when (signIn.isSuccessful) {
                true -> {
                    Log.e("token", "성공: " + signIn.body()?.accessToken)
                    prefs.setAccessToken(signIn.body()!!.accessToken)
                    prefs.setRefreshToken(signIn.body()!!.refreshToken)
                    newsRepository.callNews()
                }
                else -> {
                    Log.e("실패", "error " + signIn.message())
                }
            }
        }
        _eventSignIn.value = true
    }

    fun onEventSignInComplete() {
        _eventSignIn.value = false
    }

    private val _eventBack = MutableLiveData<Boolean>()
    val eventBack: LiveData<Boolean>
        get() = _eventBack

    fun onBack() {
        _eventBack.value = true
    }

    fun onBackComplete() {
        _eventBack.value = false
    }

}