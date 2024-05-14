package com.szlazakm.safechat.client.data.services

import com.szlazakm.safechat.client.domain.Contact

interface ContactListener {

    fun onNewContact(contact: Contact)
}