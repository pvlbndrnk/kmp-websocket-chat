@file:OptIn(
    ExperimentalForeignApi::class,
)

package com.pvlbndrnk.chat.session.data.local

import com.pvlbndrnk.chat.session.data.local.SessionStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFMutableDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.CoreFoundation.kCFTypeDictionaryKeyCallBacks
import platform.CoreFoundation.kCFTypeDictionaryValueCallBacks
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecItemNotFound
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleWhenUnlockedThisDeviceOnly
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

class IosSessionStore(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : SessionStore {

    private companion object {
        const val SERVICE = "com.pvlbndrnk.chat"
        const val ACCOUNT = "auth_token_store"
    }

    @OptIn(BetaInteropApi::class)
    override suspend fun read(): String? = withContext(coroutineDispatcher) {
        val dictionary = dictionary()
        try {
            CFDictionaryAddValue(dictionary, kSecReturnData, kCFBooleanTrue)
            CFDictionaryAddValue(dictionary, kSecMatchLimit, kSecMatchLimitOne)
            memScoped {
                val result = alloc<CFTypeRefVar>()
                result.value = null
                when (val status = SecItemCopyMatching(dictionary, result.ptr)) {
                    errSecItemNotFound -> null
                    errSecSuccess -> {
                        val data = CFBridgingRelease(result.value) as? NSData
                            ?: error("Keychain returned no token data")
                        NSString.create(data = data, encoding = NSUTF8StringEncoding)
                            ?.toString()
                            ?: error("Keychain token is not valid UTF-8")
                    }
                    else -> error("Keychain read failed: status $status")
                }
            }
        } finally {
            CFRelease(dictionary)
        }
    }

    @OptIn(BetaInteropApi::class)
    override suspend fun write(token: String) = withContext(coroutineDispatcher) {
        val data = NSString.create(string = token)
            .dataUsingEncoding(NSUTF8StringEncoding)
            ?: error("Could not encode token")
        val dataRef = checkNotNull(CFBridgingRetain(data))
        val dictionary = dictionary()
        val attributes = newContainer()
        try {
            CFDictionaryAddValue(attributes, kSecValueData, dataRef)
            when (val status = SecItemUpdate(dictionary, attributes)) {
                errSecSuccess -> Unit
                errSecItemNotFound -> {
                    CFDictionaryAddValue(dictionary, kSecValueData, dataRef)
                    CFDictionaryAddValue(
                        dictionary,
                        kSecAttrAccessible,
                        kSecAttrAccessibleWhenUnlockedThisDeviceOnly,
                    )
                    val addStatus = SecItemAdd(dictionary, null)
                    check(addStatus == errSecSuccess) {
                        "Keychain write failed: status $addStatus"
                    }
                }
                else -> error("Keychain update failed: status $status")
            }
        } finally {
            CFRelease(attributes)
            CFRelease(dictionary)
            CFRelease(dataRef)
        }
    }

    override suspend fun clear() = withContext(coroutineDispatcher) {
        val dictionary = dictionary()
        try {
            when (val status = SecItemDelete(dictionary)) {
                errSecSuccess, errSecItemNotFound -> Unit
                else -> error("Keychain delete failed: status $status")
            }
        } finally {
            CFRelease(dictionary)
        }
    }

    private fun dictionary(): CFMutableDictionaryRef {
        val dictionary = newContainer()
        val service = CFBridgingRetain(SERVICE)
        val account = CFBridgingRetain(ACCOUNT)

        try {
            CFDictionaryAddValue(dictionary, kSecClass, kSecClassGenericPassword)
            CFDictionaryAddValue(dictionary, kSecAttrService, service)
            CFDictionaryAddValue(dictionary, kSecAttrAccount, account)
        } finally {
            CFRelease(service)
            CFRelease(account)
        }
        return dictionary
    }

    private fun newContainer(): CFMutableDictionaryRef = memScoped {
        checkNotNull(
            CFDictionaryCreateMutable(
                null,
                0,
                kCFTypeDictionaryKeyCallBacks.ptr,
                kCFTypeDictionaryValueCallBacks.ptr
            )
        )
    }
}
