package com.szlazakm.safechat.client.presentation.components.starter

import androidx.lifecycle.ViewModel
import com.szlazakm.safechat.client.data.repositories.UserRepository
import com.szlazakm.safechat.client.domain.LocalUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import javax.inject.Inject

@HiltViewModel
class StarterViewModel  @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    suspend fun isUserCreated(): Boolean {
        return (Dispatchers.IO) {
            userRepository.isUserCreated()
        }
    }

    // Probably should be removed
    suspend fun loadLocalUserData() {
        return (Dispatchers.IO) {
            val user = userRepository.getLocalUser()
            if(user != null) {
                LocalUserData.getInstance().setUserData(user)
            }
        }
    }
}