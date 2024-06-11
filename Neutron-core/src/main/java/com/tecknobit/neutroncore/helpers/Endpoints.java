package com.tecknobit.neutroncore.helpers;

public class Endpoints {

    /**
     * {@code BASE_ENDPOINT} the base endpoint for the backend service
     */
    public static final String BASE_ENDPOINT = "/api/v1/";

    /**
     * {@code SIGN_UP_ENDPOINT} the endpoint to execute the sign-up auth action
     */
    public static final String SIGN_UP_ENDPOINT = "users/signUp";

    /**
     * {@code SIGN_IN_ENDPOINT} the endpoint to execute the sign-in auth action
     */
    public static final String SIGN_IN_ENDPOINT = "users/signIn";

    /**
     * {@code CHANGE_PROFILE_PIC_ENDPOINT} the endpoint to execute the change of the user profile pic
     */
    public static final String CHANGE_PROFILE_PIC_ENDPOINT = "/changeProfilePic";

    /**
     * {@code CHANGE_EMAIL_ENDPOINT} the endpoint to execute the change of the user email
     */
    public static final String CHANGE_EMAIL_ENDPOINT = "/changeEmail";

    /**
     * {@code CHANGE_PASSWORD_ENDPOINT} the endpoint to execute the change of the user password
     */
    public static final String CHANGE_PASSWORD_ENDPOINT = "/changePassword";

    /**
     * {@code CHANGE_LANGUAGE_ENDPOINT} the endpoint to execute the change of the user language
     */
    public static final String CHANGE_LANGUAGE_ENDPOINT = "/changeLanguage";

    private Endpoints() {}

}
