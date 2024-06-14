package com.tecknobit.neutroncore.records;

import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.neutroncore.records.revenues.Revenue;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tecknobit.apimanager.apis.ServerProtector.SERVER_SECRET_KEY;
import static com.tecknobit.neutroncore.records.User.*;
import static com.tecknobit.neutroncore.records.revenues.Revenue.REVENUES_KEY;
import static com.tecknobit.neutroncore.records.revenues.Revenue.returnRevenues;

public class TransferPayload {

    public static final String USER_DETAILS_KEY = "user_details";

    private final User userDetails;

    private final String serverSecret;

    private final ArrayList<Revenue> revenues;

    public <T> TransferPayload(Map<String, T> payload) {
        this(new JSONObject(payload));
    }

    public TransferPayload(JSONObject jPayload) {
        JsonHelper hPayload = new JsonHelper(jPayload);
        JSONObject jUserDetails = hPayload.getJSONObject(USER_DETAILS_KEY, new JSONObject());
        userDetails = new User(jUserDetails);
        serverSecret = jUserDetails.getString(SERVER_SECRET_KEY);
        revenues = returnRevenues(hPayload.getJSONArray(REVENUES_KEY));
    }

    public User getUserDetails() {
        return userDetails;
    }

    /**
     * {
     * "server_secret": "c2d4dbdcab5a4c598ff90ad43963f7f4",
     * "name": "{{$randomFirstName}}",
     * "surname": "{{$randomLastName}}",
     * "email": "{{$randomEmail}}",
     * "password": "{{$randomPassword}}",
     * "language": "it"
     * }
     */

    public Map<String, String> getSignUpPayload() {
        HashMap<String, String> payload = new HashMap<>();
        payload.put(SERVER_SECRET_KEY, serverSecret);
        payload.put(NAME_KEY, userDetails.getName());
        payload.put(SURNAME_KEY, userDetails.getSurname());
        payload.put(EMAIL_KEY, userDetails.getEmail());
        payload.put(PASSWORD_KEY, userDetails.getPassword());
        payload.put(LANGUAGE_KEY, userDetails.getLanguage());
        return payload;
    }

    public String getServerSecret() {
        return serverSecret;
    }

    public ArrayList<Revenue> getRevenues() {
        return revenues;
    }

}
