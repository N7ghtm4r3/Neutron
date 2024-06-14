package com.tecknobit.neutroncore.records;

import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue;
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue;
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

    private final ArrayList<GeneralRevenue> generalRevenues;

    private final ArrayList<ProjectRevenue> projectRevenues;

    public <T> TransferPayload(Map<String, T> payload) {
        this(new JSONObject(payload));
    }

    public TransferPayload(JSONObject jPayload) {
        JsonHelper hPayload = new JsonHelper(jPayload);
        JSONObject jUserDetails = hPayload.getJSONObject(USER_DETAILS_KEY, new JSONObject());
        userDetails = new User(jUserDetails);
        serverSecret = hPayload.getString(SERVER_SECRET_KEY);
        revenues = returnRevenues(hPayload.getJSONArray(REVENUES_KEY));
        generalRevenues = new ArrayList<>();
        projectRevenues = new ArrayList<>();
        initSpecificRevenues();
    }

    private void initSpecificRevenues() {
        for (Revenue revenue : revenues) {
            if(revenue instanceof GeneralRevenue)
                generalRevenues.add((GeneralRevenue) revenue);
            else
                projectRevenues.add((ProjectRevenue) revenue);
        }
    }

    public User getUserDetails() {
        return userDetails;
    }

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

    public Map<String, String> getChangeCurrencyPayload() {
        HashMap<String, String> payload = new HashMap<>();
        payload.put(CURRENCY_KEY, userDetails.getCurrency().name());
        return payload;
    }

    public String getServerSecret() {
        return serverSecret;
    }

    public ArrayList<Revenue> getRevenues() {
        return revenues;
    }

    public ArrayList<GeneralRevenue> getGeneralRevenues() {
        return generalRevenues;
    }

    public ArrayList<ProjectRevenue> getProjectRevenues() {
        return projectRevenues;
    }

}
