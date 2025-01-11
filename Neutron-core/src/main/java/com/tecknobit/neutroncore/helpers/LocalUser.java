package com.tecknobit.neutroncore.helpers;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.formatters.JsonHelper;

import static com.tecknobit.neutroncore.helpers.InputValidator.HOST_ADDRESS_KEY;
import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.*;
import static com.tecknobit.neutroncore.records.User.ApplicationTheme.Auto;
import static com.tecknobit.neutroncore.records.User.NeutronCurrency.DOLLAR;

/**
 * The {@code LocalUser} class is useful to represent a user in the client application
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Structure
public abstract class LocalUser {

    /**
     * {@code PREFERENCES_FILE} the name of the preferences file
     */
    protected static final String PREFERENCES_FILE = "Neutron";

    /**
     * {@code hostAddress} the host address which the user communicate
     */
    protected String hostAddress;

    /**
     * {@code userId} the identifier of the user
     */
    protected String userId;

    /**
     * {@code userToken} the token of the user
     */
    protected String userToken;

    /**
     * {@code profilePic} the profile pick of the user
     */
    protected String profilePic;

    /**
     * {@code name} the name of the user
     */
    protected String name;

    /**
     * {@code surname} the surname of the user
     */
    protected String surname;

    /**
     * {@code email} the email of the user
     */
    protected String email;

    /**
     * {@code password} the password of the user
     */
    protected String password;

    /**
     * {@code language} the language of the user
     */
    protected String language;

    /**
     * {@code currency} the currency of the user
     */
    protected NeutronCurrency currency;

    /**
     * {@code theme} the theme of the user
     */
    protected ApplicationTheme theme;

    /**
     * Method to init the local user session 
     *
     */
    protected void initLocalUser() {
        hostAddress = getHostAddress();
        userId = getPreference(IDENTIFIER_KEY);
        userToken = getPreference(TOKEN_KEY);
        profilePic = getPreference(PROFILE_PIC_KEY);
        name = getPreference(NAME_KEY);
        surname = getPreference(SURNAME_KEY);
        email = getPreference(EMAIL_KEY);
        password = getPreference(PASSWORD_KEY);
        language = getPreference(LANGUAGE_KEY);
        currency = NeutronCurrency.getInstance(getPreference(CURRENCY_KEY));
        theme = ApplicationTheme.getInstance(getPreference(THEME_KEY));
    }

    /**
     * Method to insert and init a new local user
     *
     * @param hostAddress The host address which the user communicate
     * @param name The name of the user
     * @param surname The surname of the user
     * @param email The email of the user
     * @param password The password of the user
     * @param language The language of the user
     * @param hResponse The payload response received from an authentication request
     */
    public void insertNewUser(String hostAddress, String name, String surname, String email, String password,
                              String language, JsonHelper hResponse) {
        setHostAddress(hostAddress);
        setUserId(hResponse.getString(IDENTIFIER_KEY));
        setUserToken(hResponse.getString(TOKEN_KEY));
        setProfilePic(hResponse.getString(PROFILE_PIC_KEY));
        setName(name);
        setSurname(surname);
        setEmail(email);
        setPassword(password);
        setLanguage(language);
        setCurrency(NeutronCurrency.valueOf(hResponse.getString(CURRENCY_KEY, DOLLAR.name())));
        setTheme(Auto);
        initLocalUser();
    }

    /**
     * Method to set the {@link #hostAddress} instance <br>
     * @param hostAddress The host address which the user communicate
     *
     */
    public void setHostAddress(String hostAddress) {
        setPreference(HOST_ADDRESS_KEY, hostAddress);
        this.hostAddress = hostAddress;
    }

    /**
     * Method to get {@link #hostAddress} instance 
     *
     * @return {@link #hostAddress} instance as {@link String}
     */
    public String getHostAddress() {
        String hostAddress = getPreference(HOST_ADDRESS_KEY);
        if(hostAddress == null)
            return "";
        return hostAddress;
    }

    /**
     * Method to set the {@link #userId} instance <br>
     * @param userId The identifier of the user
     *
     */
    public void setUserId(String userId) {
        setPreference(IDENTIFIER_KEY, userId);
        this.userId = userId;
    }

    /**
     * Method to get {@link #userId} instance 
     *
     * @return {@link #userId} instance as {@link String}
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Method to set the {@link #userToken} instance <br>
     * @param userToken The token of the user
     *
     */
    public void setUserToken(String userToken) {
        setPreference(TOKEN_KEY, userToken);
        this.userToken = userToken;
    }

    /**
     * Method to get {@link #userToken} instance 
     *
     * @return {@link #userToken} instance as {@link String}
     */
    public String getUserToken() {
        return userToken;
    }

    /**
     * Method to set the {@link #profilePic} instance <br>
     * @param profilePic The profile pic of the user
     *
     */
    public void setProfilePic(String profilePic) {
        if (this.profilePic == null || !this.profilePic.equals(profilePic)) {
            profilePic = hostAddress + "/" + profilePic;
            setPreference(PROFILE_PIC_KEY, profilePic);
            this.profilePic = profilePic;
        }
    }

    /**
     * Method to get {@link #profilePic} instance 
     *
     * @return {@link #profilePic} instance as {@link String}
     */
    public String getProfilePic() {
        return profilePic;
    }

    /**
     * Method to set the {@link #name} instance <br>
     * @param name The name of the user
     *
     */
    public void setName(String name) {
        setPreference(NAME_KEY, name);
        this.name = name;
    }

    /**
     * Method to get {@link #name} instance 
     *
     * @return {@link #name} instance as {@link String}
     */
    public String getName() {
        return name;
    }

    /**
     * Method to set the {@link #surname} instance <br>
     * @param surname The surname of the user
     *
     */
    public void setSurname(String surname) {
        setPreference(SURNAME_KEY, surname);
        this.surname = surname;
    }

    /**
     * Method to get {@link #surname} instance 
     *
     * @return {@link #surname} instance as {@link String}
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Method to get the complete name of the user 
     *
     * @return the complete name of the user as {@link String}
     */
    public String getCompleteName() {
        return name + " " + surname;
    }

    /**
     * Method to set the {@link #email} instance <br>
     * @param email The email of the user
     *
     */
    public void setEmail(String email) {
        setPreference(EMAIL_KEY, email);
        this.email = email;
    }

    /**
     * Method to get {@link #email} instance 
     *
     * @return {@link #email} instance as {@link String}
     */
    public String getEmail() {
        return email;
    }

    /**
     * Method to set the {@link #password} instance <br>
     * @param password The password of the user
     *
     */
    public void setPassword(String password) {
        setPreference(PASSWORD_KEY, password);
        this.password = password;
    }

    /**
     * Method to get {@link #password} instance 
     *
     * @return {@link #password} instance as {@link String}
     */
    public String getPassword() {
        return password;
    }

    /**
     * Method to set the {@link #language} instance <br>
     * @param language The language of the user
     *
     */
    public void setLanguage(String language) {
        setPreference(LANGUAGE_KEY, language);
        this.language = language;
    }

    /**
     * Method to get {@link #language} instance 
     *
     * @return {@link #language} instance as {@link String}
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Method to set the {@link #currency} instance <br>
     * @param currency The currency of the user
     *
     */
    public void setCurrency(NeutronCurrency currency) {
        if(!this.currency.name().equals(currency.name())) {
            setPreference(CURRENCY_KEY, currency.name());
            this.currency = currency;
        }
    }

    /**
     * Method to get {@link #currency} instance 
     *
     * @return {@link #currency} instance as {@link NeutronCurrency}
     */
    public NeutronCurrency getCurrency() {
        return currency;
    }

    /**
     * Method to set the {@link #theme} instance <br>
     * @param theme The theme of the user
     *
     */
    public void setTheme(ApplicationTheme theme) {
        setPreference(THEME_KEY, theme.name());
        this.theme = theme;
    }

    /**
     * Method to get {@link #theme} instance 
     *
     * @return {@link #theme} instance as {@link ApplicationTheme}
     */
    public ApplicationTheme getTheme() {
        return theme;
    }

    /**
     * Method to store and set a preference
     *
     * @param key The key of the preference
     * @param value The value of the preference
     */
    protected abstract void setPreference(String key, String value);

    /**
     * Method to get a stored preference
     *
     * @param key The key of the preference to get
     * @return the preference stored as {@link String}
     */
    protected abstract String getPreference(String key);

    /**
     * Method to get whether the user is already authenticated in a session 
     *
     * @return whether the user is already authenticated in a session as boolean
     */
    public boolean isAuthenticated() {
        return userId != null;
    }

    /**
     * Method to clear the current local user session 
     */
    public abstract void clear();

}
