package com.tecknobit.neutroncore.records.revenues;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.neutroncore.records.NeutronItem;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.tecknobit.neutroncore.records.revenues.GeneralRevenue.REVENUE_SOURCE_KEY;
import static com.tecknobit.neutroncore.records.revenues.Revenue.REVENUE_KEY;
import static com.tecknobit.neutroncore.records.revenues.RevenueLabel.REVENUE_LABELS_KEY;

@Entity
@Table(name = REVENUE_LABELS_KEY)
public class RevenueLabel extends NeutronItem {

    public static final String REVENUE_LABELS_KEY = "revenues_label";

    public static final String REVENUE_LABEL_TEXT_KEY = "text";

    public static final String REVENUE_LABEL_COLOR_KEY = "color";

    @Column(name = REVENUE_LABEL_TEXT_KEY)
    private final String text;

    @Column(name = REVENUE_LABEL_COLOR_KEY)
    private final String color;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = REVENUE_KEY)
    @JsonIgnoreProperties({
            REVENUE_SOURCE_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final GeneralRevenue source;

    public RevenueLabel() {
        this(null, null, null, null);
    }

    public RevenueLabel(String text, String color) {
        this(null, text, color, null);
    }

    public RevenueLabel(String id, String text, String color, GeneralRevenue source) {
        super(id);
        this.text = text;
        this.color = color;
        this.source = source;
    }

    public RevenueLabel(JSONObject jRevenueLabel) {
        super(jRevenueLabel);
        text = hItem.getString(REVENUE_LABEL_TEXT_KEY);
        color = hItem.getString(REVENUE_LABEL_COLOR_KEY);
        source = null;
    }

    public String getText() {
        return text;
    }

    public String getColor() {
        return color;
    }

    @Returner
    public static ArrayList<RevenueLabel> returnLabels(JSONArray jLabels) {
        ArrayList<RevenueLabel> labels = new ArrayList<>();
        if (jLabels == null)
            return labels;
        for (int j = 0; j < jLabels.length(); j++)
            labels.add(new RevenueLabel(jLabels.getJSONObject(j)));
        return labels;
    }

    @Override
    public String toString() {
        return new JSONObject(this).toString(4);
    }

}
