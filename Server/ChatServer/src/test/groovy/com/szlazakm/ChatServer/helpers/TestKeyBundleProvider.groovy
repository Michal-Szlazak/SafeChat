package com.szlazakm.ChatServer.helpers

import com.szlazakm.chatserver.dtos.response.KeyBundleDTO
import com.szlazakm.chatserver.entities.OPK
import com.szlazakm.chatserver.entities.SPK
import com.szlazakm.chatserver.entities.User

class TestKeyBundleProvider {

    static getKeyBundle(User user, SPK spk, OPK opk) {
        KeyBundleDTO.builder()
                .identityKey(user.getIdentityKey())
                .signedPreKeyId(spk.getKeyId())
                .signedPreKey(spk.getSignedPreKey())
                .signature(spk.getSignature())
                .onetimePreKeyId(opk.getKeyId())
                .onetimePreKey(opk.getPreKey())
                .build();
    }

    static getKeyBundleWithNullOpk(User user, SPK spk) {
        KeyBundleDTO.builder()
                .identityKey(user.getIdentityKey())
                .signedPreKeyId(spk.getKeyId())
                .signedPreKey(spk.getSignedPreKey())
                .signature(spk.getSignature())
                .onetimePreKeyId(null)
                .onetimePreKey(null)
                .build();
    }

}
