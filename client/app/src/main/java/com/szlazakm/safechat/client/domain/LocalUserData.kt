package com.szlazakm.safechat.client.domain

import com.szlazakm.safechat.client.data.entities.UserEntity

class LocalUserData {

    companion object {

        private var instance: LocalUserData? = null

        fun getInstance(): LocalUserData {
            if (instance == null) {
                instance = LocalUserData()
            }
            return instance as LocalUserData
        }
    }

    private var phoneNumber: String? = null
    private var firstName: String? = null
    private var lastName: String? = null

    fun getPhoneNumber(): String? {
        return phoneNumber
    }

    fun setUserData(user: UserEntity) {
        phoneNumber = user.phoneNumber
        firstName = user.firstName
        lastName = user.lastName
    }
}