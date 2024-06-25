package com.tecknobit.neutroncore.helpers.local

import com.tecknobit.apimanager.apis.APIRequest.SHA256_ALGORITHM
import com.tecknobit.apimanager.apis.APIRequest.base64Digest
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.neutroncore.records.User.*

interface LUserController : LNeutronController {

    companion object {

        const val CREATE_USERS_TABLE: String = (
                "CREATE TABLE IF NOT EXISTS " + USERS_KEY +
                    " (\n" +
                        IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                        CURRENCY_KEY + " TEXT CHECK (" + CURRENCY_KEY +
                            " IN (" +
                                "'EURO', " +
                                "'DOLLAR', " +
                                "'POUND_STERLING', " +
                                "'JAPANESE_YEN', " +
                                "'CHINESE_YEN'" +
                            ")" +
                        ")" + " DEFAULT 'DOLLAR'" + ",\n" +
                        EMAIL_KEY + " VARCHAR(75) UNIQUE NOT NULL" + ",\n" +
                        LANGUAGE_KEY + " VARCHAR(2) DEFAULT 'en'" + ",\n" +
                        NAME_KEY + " VARCHAR(20) NOT NULL" + ",\n" +
                        PASSWORD_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        PROFILE_PIC_KEY + " TEXT NOT NULL" + ",\n" +
                        SURNAME_KEY + " VARCHAR(30) NOT NULL" + ",\n" +
                        TOKEN_KEY + " VARCHAR(32) UNIQUE NOT NULL"
                + ");")

        const val SIGN_UP_QUERY: String = ("INSERT INTO " + USERS_KEY +
                " (" +
                    IDENTIFIER_KEY + "," +
                    EMAIL_KEY + "," +
                    LANGUAGE_KEY + "," +
                    NAME_KEY + "," +
                    PASSWORD_KEY + "," +
                    PROFILE_PIC_KEY + "," +
                    SURNAME_KEY + "," +
                    TOKEN_KEY +
                " ) VALUES (" +
                    "?" + "," +
                    "?" + "," +
                    "?" + "," +
                    "?" + "," +
                    "?" + "," +
                    "?" + "," +
                    "?" + "," +
                    "?"
                + ")")

        const val SIGN_IN_QUERY: String = "SELECT * FROM " + USERS_KEY +
                " WHERE " + EMAIL_KEY + "=?" + " AND " + PASSWORD_KEY + "=?"

        const val CHANGE_USER_INFO_QUERY: String = "UPDATE " + USERS_KEY +
                " SET %s = ? WHERE " + IDENTIFIER_KEY + "=?" + " AND " + TOKEN_KEY + "=?"

        const val DELETE_USER: String = "DELETE FROM " + USERS_KEY +
                " WHERE " + IDENTIFIER_KEY + "=?" + " AND " + TOKEN_KEY + "=?"

    }

    fun signUp(
        name: String,
        surname: String,
        email: String,
        password: String,
        language: String,
        onSuccess: (JsonHelper) -> Unit = {},
        onFailure: (JsonHelper) -> Unit = {}
    )

    fun signIn(
        email: String,
        password: String,
        onSuccess: (JsonHelper) -> Unit = {},
        onFailure: (JsonHelper) -> Unit = {}
    )

    fun changeProfilePic(
        newProfilePic: String,
        onSuccess: (JsonHelper) -> Unit = {},
        onFailure: (JsonHelper) -> Unit = {}
    ) {
        changeUserInfo(
            key = PROFILE_PIC_KEY,
            newInfo = newProfilePic,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun changeEmail(
        newEmail: String,
        onSuccess: (JsonHelper) -> Unit = {},
        onFailure: (JsonHelper) -> Unit = {}
    ) {
        changeUserInfo(
            key = EMAIL_KEY,
            newInfo = newEmail,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun changePassword(
        newPassword: String,
        onSuccess: (JsonHelper) -> Unit = {},
        onFailure: (JsonHelper) -> Unit = {}
    ) {
        changeUserInfo(
            key = PASSWORD_KEY,
            newInfo = hash(newPassword),
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun changeLanguage(
        newLanguage: String,
        onSuccess: (JsonHelper) -> Unit = {},
        onFailure: (JsonHelper) -> Unit = {}
    ) {
        changeUserInfo(
            key = LANGUAGE_KEY,
            newInfo = newLanguage,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun changeCurrency(
        newCurrency: NeutronCurrency,
        onSuccess: (JsonHelper) -> Unit = {},
        onFailure: (JsonHelper) -> Unit = {}
    ) {
        changeUserInfo(
            key = CURRENCY_KEY,
            newInfo = newCurrency.name,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun changeUserInfo(
        key: String,
        newInfo: String,
        onSuccess: (JsonHelper) -> Unit = {},
        onFailure: (JsonHelper) -> Unit = {}
    )

    fun deleteAccount(
        onSuccess: (JsonHelper) -> Unit = {},
        onFailure: (JsonHelper) -> Unit = {}
    )

    fun hash(
        value: String
    ) : String {
        return base64Digest(value, SHA256_ALGORITHM)
    }

}
