package com.tecknobit.neutroncore.helpers;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.HashMap;

/**
 * The {@code InputValidator} class is useful to validate the inputs
 *
 * @author N7ghtm4r3 - Tecknobit
 */
public class InputValidator {

    /**
     * {@code WRONG_NAME_MESSAGE} error message used when the name inserted is not valid
     */
    public static final String WRONG_NAME_MESSAGE = "Name is not valid";

    /**
     * {@code NAME_MAX_LENGTH} the max valid length for the username
     */
    public static final int NAME_MAX_LENGTH = 20;

    /**
     * {@code WRONG_SURNAME_MESSAGE} error message used when the surname inserted is not valid
     */
    public static final String WRONG_SURNAME_MESSAGE = "Surname is not valid";

    /**
     * {@code SURNAME_MAX_LENGTH} the max valid length for the surname
     */
    public static final int SURNAME_MAX_LENGTH = 30;

    /**
     * {@code WRONG_EMAIL_MESSAGE} error message used when the email inserted is not valid
     */
    public static final String WRONG_EMAIL_MESSAGE = "Email is not valid";

    /**
     * {@code EMAIL_MAX_LENGTH} the max valid length for the email
     */
    public static final int EMAIL_MAX_LENGTH = 75;

    /**
     * {@code WRONG_PASSWORD_MESSAGE} error message used when the password inserted is not valid
     */
    public static final String WRONG_PASSWORD_MESSAGE = "Password is not valid";

    /**
     * {@code PASSWORD_MIN_LENGTH} the min valid length for the password
     */
    public static final int PASSWORD_MIN_LENGTH = 8;

    /**
     * {@code PASSWORD_MAX_LENGTH} the max valid length for the password
     */
    public static final int PASSWORD_MAX_LENGTH = 32;

    /**
     * {@code WRONG_LANGUAGE_MESSAGE} error message used when the language inserted is not valid
     */
    public static final String WRONG_LANGUAGE_MESSAGE = "Language is not supported";

    /**
     * {@code DEFAULT_LANGUAGE} default language used
     */
    public static final String DEFAULT_LANGUAGE = "en";

    /**
     * {@code emailValidator} helper to validate the emails values
     */
    private static final EmailValidator emailValidator = EmailValidator.getInstance();

    /**
     * {@code urlValidator} helper to validate the urls values
     */
    private static final UrlValidator urlValidator = UrlValidator.getInstance();

    /**
     * {@code LANGUAGES_SUPPORTED} list of the supported languages
     */
    public static final HashMap<String, String> LANGUAGES_SUPPORTED = new HashMap<>();

    static {
        LANGUAGES_SUPPORTED.put("it", "Italiano");
        LANGUAGES_SUPPORTED.put("en", "English");
        LANGUAGES_SUPPORTED.put("fr", "Francais");
        LANGUAGES_SUPPORTED.put("es", "Espanol");
    }

    /**
     * Constructor to init the {@link InputValidator} class <br>
     *
     * No-any params required
     */
    private InputValidator() {
    }

    /**
     * Method to validate a host
     *
     * @param host: host value to check the validity
     *
     * @return whether the host is valid or not as {@code boolean}
     */
    public static boolean isHostValid(String host) {
        return urlValidator.isValid(host);
    }

    /**
     * Method to validate a server secret
     *
     * @param serverSecret: name value to check the validity
     *
     * @return whether the server secret is valid or not as {@code boolean}
     */
    public static boolean isServerSecretValid(String serverSecret) {
        return isInputValid(serverSecret);
    }

    /**
     * Method to validate a name
     *
     * @param name: name value to check the validity
     *
     * @return whether the name is valid or not as {@code boolean}
     */
    public static boolean isNameValid(String name) {
        return isInputValid(name) && name.length() <= NAME_MAX_LENGTH;
    }

    /**
     * Method to validate a surname
     *
     * @param surname: surname value to check the validity
     *
     * @return whether the surname is valid or not as {@code boolean}
     */
    public static boolean isSurnameValid(String surname) {
        return isInputValid(surname) && surname.length() <= SURNAME_MAX_LENGTH;
    }

    /**
     * Method to validate an email
     *
     * @param email: password value to check the validity
     *
     * @return whether the email is valid or not as {@code boolean}
     */
    public static boolean isEmailValid(String email) {
        return emailValidator.isValid(email) && email.length() <= EMAIL_MAX_LENGTH;
    }

    /**
     * Method to validate a password
     *
     * @param password: password value to check the validity
     *
     * @return whether the password is valid or not as {@code boolean}
     */
    public static boolean isPasswordValid(String password) {
        int passwordLength = password.length();
        return isInputValid(password) && passwordLength >= PASSWORD_MIN_LENGTH && passwordLength <= PASSWORD_MAX_LENGTH;
    }

    /**
     * Method to validate a language
     *
     * @param language: language value to check the validity
     *
     * @return whether the language is valid or not as {@code boolean}
     */
    public static boolean isLanguageValid(String language) {
        return language != null && (LANGUAGES_SUPPORTED.containsKey(language) || LANGUAGES_SUPPORTED.containsValue(language));
    }

    /**
     * Method to validate an input
     *
     * @param field: field value to check the validity
     *
     * @return whether the field is valid or not as {@code boolean}
     */
    private static boolean isInputValid(String field) {
        return field != null && !field.isEmpty();
    }

}
