package com.datnht.android_extensions.secure

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec

@RequiresApi(Build.VERSION_CODES.M)
fun getInitializedCipherForEncryption(keyName: String): Cipher {
    val cipher = getCipher()
    val secretKey = generateSymmetricKey(keyName, useBiometricAuth = true)
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    return cipher
}

@RequiresApi(Build.VERSION_CODES.M)
fun getInitializedCipherForDecryption(keyName: String, initializationVector: ByteArray): Cipher {
    val cipher = getCipher()
    val secretKey = generateSymmetricKey(keyName, useBiometricAuth = true)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(initializationVector))
    return cipher
}

/**
 * Tao bio prompt
 * Lambda succceed khi bio thanh cong
 */
fun instanceOfBiometricPrompt(
    activity: AppCompatActivity,
    processSuccess: (BiometricPrompt.AuthenticationResult) -> Unit
): BiometricPrompt {
    val executor = ContextCompat.getMainExecutor(activity)

    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            super.onAuthenticationError(errorCode, errString)
            if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                // Nếu user chon negative button, có thể cho user tiếp tục login bằng cách nhập acc
                //loginWithPassword()
            }
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            processSuccess(result)
        }
    }

    val biometricPrompt = BiometricPrompt(activity, executor, callback)
    return biometricPrompt
}

fun createPromptInfoWithOptionNormal(): BiometricPrompt.PromptInfo {
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Title")
        .setSubtitle("sub title")
        .setDescription("des")
        // Authenticate without requiring the user to press a "confirm"
        // button after satisfying the biometric check
        .setConfirmationRequired(false)
        .setNegativeButtonText("Sử dụng login bằng acc")
        .build()
    return promptInfo
}

fun createPromptInfoWithOptionDevice(): BiometricPrompt.PromptInfo {
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Title")
        .setSubtitle("sub title")
        .setDescription("des")
        // Authenticate without requiring the user to press a "confirm"
        // button after satisfying the biometric check
        .setConfirmationRequired(false)
        .setDeviceCredentialAllowed(true)
        .build()
    return promptInfo
}