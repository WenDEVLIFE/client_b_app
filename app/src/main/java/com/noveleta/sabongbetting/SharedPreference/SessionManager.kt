package com.noveleta.sabongbetting.SharedPreference

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "app_prefs"
    private const val KEY_SESSION_ID = "php_session_id"

    private const val KEY_ACCOUNT_ID = "accountID"
    private const val KEY_CNAME = "cname"
    private const val KEY_SYSTEMNAME = "systemName"
    private const val KEY_ROLE_ID = "roleID"
    private const val KEY_BET_TYPE_ID = "betTypeId"
    private const val KEY_ODDS_SETTINGS = "oddsSettings"
    private const val KEY_SPECIAL_TELLER = "specialTeller"
    private const val KEY_PAYOUT_SETTINGS = "payoutSettings"
    
    private const val KEY_IP_ADDRESS = "ipaddress"
    private const val KEY_PORT = "portaddress"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var sessionId: String?
        get() = prefs.getString(KEY_SESSION_ID, null)
        set(value) = prefs.edit().putString(KEY_SESSION_ID, value).apply()
        
    var ipAddress: String?
        get() = prefs.getString(KEY_IP_ADDRESS, null)
        set(value) = prefs.edit().putString(KEY_IP_ADDRESS, value).apply()

    var portAddress: String?
        get() = prefs.getString(KEY_PORT, null)
        set(value) = prefs.edit().putString(KEY_PORT, value).apply()

    val isLoggedIn: Boolean
        get() = !sessionId.isNullOrEmpty()

    fun saveUserData(
        accountID: String,
        cname: String,
        roleID: String,
        betTypeId: String,
        oddsSettings: String,
        specialTeller: String,
        payoutSettings: String
    ) {
        prefs.edit().apply {
            putString(KEY_ACCOUNT_ID, accountID)
            putString(KEY_CNAME, cname)
            putString(KEY_ROLE_ID, roleID)
            putString(KEY_BET_TYPE_ID, betTypeId)
            putString(KEY_ODDS_SETTINGS, oddsSettings)
            putString(KEY_SPECIAL_TELLER, specialTeller)
            putString(KEY_PAYOUT_SETTINGS, payoutSettings)
            apply()
        }
    }

    val accountID: String?
        get() = prefs.getString(KEY_ACCOUNT_ID, null)

    val cname: String?
        get() = prefs.getString(KEY_CNAME, null)

    val roleID: String?
        get() = prefs.getString(KEY_ROLE_ID, null)
        
    val systemName: String?
        get() = prefs.getString(KEY_SYSTEMNAME, null)    

    val betTypeId: String?
        get() = prefs.getString(KEY_BET_TYPE_ID, null)

    val oddsSettings: String?
        get() = prefs.getString(KEY_ODDS_SETTINGS, null)

    val specialTeller: String?
        get() = prefs.getString(KEY_SPECIAL_TELLER, null)

    val payoutSettings: String?
        get() = prefs.getString(KEY_PAYOUT_SETTINGS, null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
