package com.example

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.URLEncoder

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("pay_me_prefs", Context.MODE_PRIVATE)

    // UI States
    private val _cashTag = MutableStateFlow(sharedPrefs.getString("cash_tag", "YourCashTag") ?: "YourCashTag")
    val cashTag: StateFlow<String> = _cashTag.asStateFlow()

    private val _businessName = MutableStateFlow(sharedPrefs.getString("business_name", "Support My Business") ?: "Support My Business")
    val businessName: StateFlow<String> = _businessName.asStateFlow()

    private val _tagline = MutableStateFlow(sharedPrefs.getString("tagline", "Thanks for your support!") ?: "Thanks for your support!")
    val tagline: StateFlow<String> = _tagline.asStateFlow()

    private val _amount = MutableStateFlow(sharedPrefs.getString("amount", "") ?: "")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _note = MutableStateFlow(sharedPrefs.getString("note", "") ?: "")
    val note: StateFlow<String> = _note.asStateFlow()

    // Temporary values for Editing/Preferences to separate live card state from unsaved drafts if necessary, 
    // or we can update in real-time. Updating in real-time is extremely satisfying!
    fun updateCashTag(newTag: String) {
        // Strip out '$' if user types it manually
        val cleanTag = newTag.replace("$", "").trim()
        _cashTag.value = cleanTag
        sharedPrefs.edit().putString("cash_tag", cleanTag).apply()
    }

    fun updateBusinessName(newName: String) {
        _businessName.value = newName
        sharedPrefs.edit().putString("business_name", newName).apply()
    }

    fun updateTagline(newTagline: String) {
        _tagline.value = newTagline
        sharedPrefs.edit().putString("tagline", newTagline).apply()
    }

    fun updateAmount(newAmount: String) {
        // Simple validation for decimal/integer characters
        val cleanAmount = newAmount.filter { it.isDigit() || it == '.' }
        _amount.value = cleanAmount
        sharedPrefs.edit().putString("amount", cleanAmount).apply()
    }

    fun updateNote(newNote: String) {
        _note.value = newNote
        sharedPrefs.edit().putString("note", newNote).apply()
    }

    // Helper: Build the Cash App Link
    fun getCashAppLink(): String {
        val tag = _cashTag.value.ifEmpty { "YourCashTag" }
        val amt = _amount.value.trim()
        val memo = _note.value.trim()

        // Base URL: https://cash.app/$cashtag
        var url = "https://cash.app/\$$tag"
        
        if (amt.isNotEmpty()) {
            url += "/$amt"
        }
        
        if (memo.isNotEmpty()) {
            try {
                // Cash App handles memo parameters via query string optionally
                url += "?utm_source=pay_me_app&memo=${URLEncoder.encode(memo, "UTF-8")}"
            } catch (e: Exception) {
                // Fallback
            }
        }
        return url
    }

    // Helper: Build the QR Code URL using a high-quality free API
    fun getQrCodeUrl(): String {
        val payUrl = getCashAppLink()
        return try {
            val encodedUrl = URLEncoder.encode(payUrl, "UTF-8")
            // Use qrserver API which is extremely fast and reliable
            "https://api.qrserver.com/v1/create-qr-code/?size=512x512&data=$encodedUrl&color=00D632&bgcolor=FFFFFF&qzone=2"
        } catch (e: Exception) {
            ""
        }
    }

    // Reset fields to defaults
    fun resetToDefaults() {
        updateCashTag("YourCashTag")
        updateBusinessName("Support My Business")
        updateTagline("Thanks for your support!")
        updateAmount("")
        updateNote("")
    }
}
