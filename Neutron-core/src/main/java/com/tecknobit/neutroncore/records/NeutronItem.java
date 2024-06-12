package com.tecknobit.neutroncore.records;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.apimanager.formatters.TimeFormatter;
import jakarta.persistence.*;
import org.json.JSONObject;

@Structure
@MappedSuperclass
public abstract class NeutronItem {

    public static final String IDENTIFIER_KEY = "id";

    @Transient
    protected final TimeFormatter timeFormatter = TimeFormatter.getInstance();

    @Id
    @Column(name = IDENTIFIER_KEY)
    private final String id;

    @Transient
    protected final JsonHelper hItem;

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
