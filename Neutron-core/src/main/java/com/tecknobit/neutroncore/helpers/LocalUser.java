package com.tecknobit.neutroncore.helpers;

import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.neutroncore.records.User.UserStorage;

import static com.tecknobit.neutroncore.helpers.InputValidator.HOST_ADDRESS_KEY;
import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.*;
import static com.tecknobit.neutroncore.records.User.UserStorage.*;
import static com.tecknobit.neutroncore.records.User.UserStorage.Local;

public abstract class LocalUser {

    protected static final String PREFERENCES_FILE = "Neutron";

    protected String hostAddress;

    protected String userId;

    protected String userToken;

    protected String profilePic;

    protected UserStorage storage;

    protected void initLocalUser() {
        hostAddress = getHostAddress();
        userId = getPreference(IDENTIFIER_KEY);
        userToken = getPreference(TOKEN_KEY);
        profilePic = getPreference(PROFILE_PIC_KEY);
        String userStorage = getPreference(USER_STORAGE_KEY);
        if(userStorage != null)
            storage = valueOf(userStorage);
        else
            storage = Local;
    }

    public void insertNewUser(String hostAddress, JsonHelper hResponse) {
        setHostAddress(hostAddress);
        setUserId(hResponse.getString(IDENTIFIER_KEY));
        setUserToken(hResponse.getString(TOKEN_KEY));
        setProfilePic(hResponse.getString(PROFILE_PIC_KEY));
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
        profilePic = hostAddress + "/" +  profilePic;
        setPreference(PROFILE_PIC_KEY, profilePic);
        this.profilePic = profilePic;
    }

    public String getProfilePic() {
        return profilePic;
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

}
