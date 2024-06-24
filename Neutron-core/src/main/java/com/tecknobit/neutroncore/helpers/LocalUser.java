package com.tecknobit.neutroncore.helpers;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.neutroncore.records.User;

import static com.tecknobit.neutroncore.helpers.InputValidator.HOST_ADDRESS_KEY;
import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.*;
import static com.tecknobit.neutroncore.records.User.ApplicationTheme.Auto;
import static com.tecknobit.neutroncore.records.User.NeutronCurrency.DOLLAR;
import static com.tecknobit.neutroncore.records.User.UserStorage.*;

@Structure
public abstract class LocalUser {

    protected static final String PREFERENCES_FILE = "Neutron";

    protected static final String LOCAL_PROFILE_PIC_PATH = "local_profile_pic_path";

    protected String hostAddress;

    protected String userId;

    protected String userToken;

    protected String profilePic;

    protected String localProfilePicPath;

    protected String name;

    protected String surname;

    protected String email;

    protected String password;

    protected String language;

    protected NeutronCurrency currency;

    protected ApplicationTheme theme;

    protected UserStorage storage;

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
        String userStorage = getPreference(USER_STORAGE_KEY);
        if(userStorage != null)
            storage = valueOf(userStorage);
        else
            storage = Local;
    }

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
        setCurrency(DOLLAR);
        setTheme(Auto);
        UserStorage storage;
        if(hostAddress != null)
            storage = Online;
        else
            storage = Local;
        setStorage(storage);
        initLocalUser();
    }

    public void setHostAddress(String hostAddress) {
        setPreference(HOST_ADDRESS_KEY, hostAddress);
        this.hostAddress = hostAddress;
    }

    public String getHostAddress() {
        String hostAddress = getPreference(HOST_ADDRESS_KEY);
        if(hostAddress == null)
            return "";
        return hostAddress;
    }

    public void setUserId(String userId) {
        setPreference(IDENTIFIER_KEY, userId);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserToken(String userToken) {
        setPreference(TOKEN_KEY, userToken);
        this.userToken = userToken;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setProfilePic(String profilePic) {
        if (this.profilePic == null || !this.profilePic.equals(profilePic)) {
            if (profilePic == null) {
                this.profilePic = localProfilePicPath;
                setPreference(PROFILE_PIC_KEY, localProfilePicPath);
            } else {
                profilePic = hostAddress + "/" + profilePic;
                setPreference(PROFILE_PIC_KEY, profilePic);
                this.profilePic = profilePic;
            }
        }
    }

    public void setLocalProfilePicPath(String localProfilePicPath) {
        setPreference(LOCAL_PROFILE_PIC_PATH, localProfilePicPath);
        this.localProfilePicPath = localProfilePicPath;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setName(String name) {
        setPreference(NAME_KEY, name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSurname(String surname) {
        setPreference(SURNAME_KEY, surname);
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }

    public String getCompleteName() {
        return name + " " + surname;
    }

    public void setEmail(String email) {
        setPreference(EMAIL_KEY, email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        setPreference(PASSWORD_KEY, password);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setLanguage(String language) {
        setPreference(LANGUAGE_KEY, language);
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setCurrency(NeutronCurrency currency) {
        if(!this.currency.name().equals(currency.name())) {
            setPreference(CURRENCY_KEY, currency.name());
            this.currency = currency;
        }
    }

    public NeutronCurrency getCurrency() {
        return currency;
    }

    public void setTheme(ApplicationTheme theme) {
        setPreference(THEME_KEY, theme.name());
        this.theme = theme;
    }

    public ApplicationTheme getTheme() {
        return theme;
    }

    public void setStorage(UserStorage storage) {
        setPreference(USER_STORAGE_KEY, storage.name());
        this.storage = storage;
    }

    public UserStorage getStorage() {
        return storage;
    }

    protected abstract void setPreference(String key, String value);

    protected abstract String getPreference(String key);

    public boolean isAuthenticated() {
        return userId != null;
    }

    public abstract void clear();

    public User toUser() {
        return new User(
                userId,
                userToken,
                name,
                surname,
                email,
                password,
                profilePic,
                language,
                currency,
                theme,
                storage
        );
    }

}
