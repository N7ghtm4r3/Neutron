package com.tecknobit.neutroncore.records;

import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.apimanager.formatters.TimeFormatter;
import org.json.JSONObject;

public abstract class NeutronItem {

    public static final String IDENTIFIER_KEY = "id";

    protected final TimeFormatter timeFormatter = TimeFormatter.getInstance();

    private final String id;

    private final JsonHelper hItem;

    public NeutronItem(String id) {
        this.id = id;
        hItem = null;
    }

    public NeutronItem(JSONObject jItem) {
        hItem = new JsonHelper(jItem);
        id = hItem.getString(IDENTIFIER_KEY);
    }

    public String getId() {
        return id;
    }

}
