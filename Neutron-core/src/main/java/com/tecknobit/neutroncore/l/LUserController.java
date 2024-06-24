package com.tecknobit.neutroncore.l;

import com.tecknobit.apimanager.annotations.Structure;
import org.json.JSONObject;

import java.io.File;

import static com.tecknobit.neutroncore.records.User.*;
import static com.tecknobit.neutroncore.records.User.CURRENCY_KEY;

@Structure
public abstract class LUserController extends LServerController {

    public static final String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + USERS_KEY +
            " (\n" +
                IDENTIFIER_KEY + "VARCHAR(32) PRIMARY KEY" + ",\n" +
                CURRENCY_KEY + "ENUM( " +
                    "'EURO', " +
                    "'DOLLAR', " +
                    "'POUND_STERLING', " +
                    "'JAPANESE_YEN', " +
                    "'CHINESE_YEN'" +
                    ")" +
                " NOT NULL DEFAULT DOLLAR" + ",\n" +
                EMAIL_KEY + "VARCHAR(75) UNIQUE NOT NULL" + ",\n" +
                LANGUAGE_KEY + "VARCHAR(2) NOT NULL DEFAULT en" + ",\n" +
                NAME_KEY + "VARCHAR(20) NOT NULL" + ",\n" +
                PASSWORD_KEY + "VARCHAR(32) NOT NULL" + ",\n" +
                PROFILE_PIC_KEY + "TEXT NOT NULL" + ",\n" +
                SURNAME_KEY + "VARCHAR(30) NOT NULL" + ",\n" +
                TOKEN_KEY + "VARCHAR(32) UNIQUE NOT NULL"
            + ");";

    public LUserController(String userId, String userToken) {
        super(userId, userToken);
    }

    public abstract JSONObject signUp(String name, String surname, String email, String password);

    public abstract JSONObject signIn(String email, String password);

    public abstract void changeProfilePic(File newProfilePic);

    public void changeEmail(String newEmail) {
        changeUserInfo(EMAIL_KEY, newEmail);
    }

    public void changePassword(String newPassword) {
        changeUserInfo(PASSWORD_KEY, newPassword);
    }

    public void changeLanguage(String newLanguage) {
        changeUserInfo(LANGUAGE_KEY, newLanguage);
    }

    public void changeCurrency(NeutronCurrency newCurrency) {
        changeUserInfo(CURRENCY_KEY, newCurrency.name());
    }

    protected abstract void changeUserInfo(String key, String newInfo);

    public abstract void deleteAccount();

}
