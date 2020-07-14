package com.datnht.android_extensions.secure

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.nio.charset.Charset
import java.security.GeneralSecurityException
import java.security.Key
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@RequiresApi(Build.VERSION_CODES.M)
fun generateSymmetricKey(keyAlias: String, useBiometricAuth: Boolean = false): Key {

    // load all key
    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)
    keyStore.getKey(keyAlias, null)?.let { return it as SecretKey }
    // check if key with alias not exist
    if (keyStore.containsAlias(keyAlias).not()) {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpecBuilder =
            KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT // purposes of key is encrypt & decrypt
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC) // mode CBC support IV like salt of hashing
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setRandomizedEncryptionRequired(true) //different ciphertext for same plaintext on each call
                .setUserAuthenticationRequired(useBiometricAuth) //requires lock screen, invalidated if lock screen is disabled. Cái này là true thì sẽ enable bảo mật cùng phần cứng

        val keyGenParameterSpec = keyGenParameterSpecBuilder.build()
        keyGenerator.init(keyGenParameterSpec)

        // key is generated, auto save in store
        return keyGenerator.generateKey()
    }

    // we don't need password because the system will do this
    return keyStore.getKey(keyAlias, null)
}

/**
 * @param cipherAlgorithm: eg "AES/CBC/PKCS7Padding" which is spec when key is generated
 */
fun encrypt(cipherAlgorithm: String, plainText: ByteArray, key: Key): Pair<ByteArray, ByteArray>? =
    try {
        val cipher = getCipher(cipherAlgorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val cipherText = cipher.doFinal(plainText)
        Pair(cipherText, cipher.iv)
    } catch (exception: GeneralSecurityException) {
        null
    }

fun encrypt(plainText: String, cipher: Cipher): Pair<ByteArray, ByteArray>? =
    try {
        val cipherText = cipher.doFinal(plainText.toByteArray(Charset.forName("UTF-8")))
        Pair(cipherText, cipher.iv)
    } catch (exception: GeneralSecurityException) {
        null
    }

/**
 * @param cipherAlgorithm: eg "AES/CBC/PKCS7Padding" which is spec when key is generated
 */
fun decrypt(cipherAlgorithm: String, cipherText: ByteArray, key: Key, iv: IvParameterSpec? = null): ByteArray? {
    val cipher = getCipher(cipherAlgorithm)
    cipher.init(Cipher.DECRYPT_MODE, key, iv)
    val plainText = cipher.doFinal(cipherText)
    return plainText
}

fun decrypt(cipherText: ByteArray, cipher: Cipher): String? {
    val plainText = cipher.doFinal(cipherText)
    return String(plainText, Charset.forName("UTF-8"))
}

fun getSecretKey(alias: String): SecretKey? {

    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)

    // go to path of keystore with alias
    val secretKeyEntry = keyStore
        .getEntry(alias, null) as KeyStore.SecretKeyEntry

    // get key in block of path
    return secretKeyEntry.secretKey
}

fun getAllAlias(keyStore: KeyStore) = Collections.list(keyStore.aliases())

fun getCipher(cipherAlgorithm: String? = null): Cipher {
    return Cipher.getInstance(cipherAlgorithm ?: "AES/CBC/PKCS7Padding")
}