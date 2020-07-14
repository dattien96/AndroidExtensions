package com.datnht.android_extensions.secure

import android.content.Context
import android.security.KeyPairGeneratorSpec
import java.math.BigInteger
import java.security.GeneralSecurityException
import java.security.Key
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

/**
 * Use asymmetric - RSA with KeyPair
 * Need a certificate for a public key
 */
fun generateAsymmetricKey(keyAlias: String, context: Context): KeyStore.Entry {

    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)

    if (keyStore.containsAlias(keyAlias).not()) {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        endDate.add(Calendar.YEAR, 25)
        val keyPairGeneratorSpec = KeyPairGeneratorSpec.Builder(context.applicationContext)
            .setAlias(keyAlias)
            .setSubject(X500Principal("CN=$keyAlias"))
            .setSerialNumber(BigInteger.valueOf(123456))
            .setStartDate(startDate.time)
            .setEndDate(endDate.time)
            .build()

        val keyPairGenerator = KeyPairGenerator.getInstance(
            "RSA",
            "AndroidKeyStore"
        )
        keyPairGenerator.initialize(keyPairGeneratorSpec)
        keyPairGenerator.generateKeyPair()
    }
    return keyStore.getEntry(keyAlias, null)
}

/**
 * eg: "RSA/ECB/PKCS1Padding"
 *
 * Note: I should notice that Java does not implement ECB with RSA cipher even when you use RSA/ECB/PKCS1Padding.
 * Java only encrypts/decrypts a single block by using the RSA algorithm and ECB is one of the block cipher modes
 * which is being used for ciphers like AES
 */
fun encryptRSA(cipherAlgorithm: String, plainText: ByteArray, key: Key): ByteArray? {
    return try {
        val cipher = getAsymmetricCipher(cipherAlgorithm)
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val cipherText = cipher.doFinal(plainText)
        cipherText
    } catch (exception: GeneralSecurityException) {
        exception.printStackTrace()
        null
    }
}

fun decryptRSA(cipherAlgorithm: String, cipherText: ByteArray, key: Key): ByteArray? {
    val cipher = getAsymmetricCipher(cipherAlgorithm)
    cipher.init(Cipher.DECRYPT_MODE, key)
    val plainText = cipher.doFinal(cipherText)
    return plainText
}

fun getAsymmetricCipher(cipherAlgorithm: String? = null): Cipher {
    return Cipher.getInstance(cipherAlgorithm ?: "RSA/ECB/PKCS1Padding")
}