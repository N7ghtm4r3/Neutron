package com.tecknobit.neutroncore.l;

import com.tecknobit.apimanager.annotations.Structure;

@Structure
public abstract class LServerController {

    protected String userId;

    protected String userToken;

    public LServerController(String userId, String userToken) {
        this.userId = userId;
        this.userToken = userToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

}
