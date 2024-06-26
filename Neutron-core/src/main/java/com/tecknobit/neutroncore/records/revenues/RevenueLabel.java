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

/**
 * The {@code RevenueLabel} class is useful to represent a label attached to a {@link GeneralRevenue}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronItem
 * @see Revenue
 */
@Entity
@Table(name = REVENUE_LABELS_KEY)
public class RevenueLabel extends NeutronItem {

    /**
     * {@code REVENUE_LABELS_KEY} the key for the <b>"revenue_labels"</b> field
     */
    public static final String REVENUE_LABELS_KEY = "revenue_labels";

    /**
     * {@code REVENUE_LABEL_TEXT_KEY} the key for the <b>"text"</b> field
     */
    public static final String REVENUE_LABEL_TEXT_KEY = "text";

    /**
     * {@code REVENUE_LABEL_COLOR_KEY} the key for the <b>"color"</b> field
     */
    public static final String REVENUE_LABEL_COLOR_KEY = "color";

    /**
     * {@code text} the text of the label
     */
    @Column(name = REVENUE_LABEL_TEXT_KEY)
    private final String text;

    /**
     * {@code color} the color of the label
     */
    @Column(name = REVENUE_LABEL_COLOR_KEY)
    private final String color;

    /**
     * {@code source} the general revenue where the label is attached
     */
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

    /**
     * Constructor to init the {@link RevenueLabel} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public RevenueLabel() {
        this(null, null, null, null);
    }

    /**
     * Constructor to init the {@link RevenueLabel} class
     *
     * @param text: the text of the label
     * @param color: the color of the label
     *
     */
    public RevenueLabel(String text, String color) {
        this(null, text, color, null);
    }

    /**
     * Constructor to init the {@link RevenueLabel} class
     *
     * @param id: the identifier of the label
     * @param text: the text of the label
     * @param color: the color of the label
     * @param source: the general revenue where the label is attached
     *
     */
    public RevenueLabel(String id, String text, String color, GeneralRevenue source) {
        super(id);
        this.text = text;
        this.color = color;
        this.source = source;
    }

    /**
     * Constructor to init the {@link RevenueLabel} class
     *
     * @param jRevenueLabel: label details formatted as JSON
     *
     */
    public RevenueLabel(JSONObject jRevenueLabel) {
        super(jRevenueLabel);
        text = hItem.getString(REVENUE_LABEL_TEXT_KEY);
        color = hItem.getString(REVENUE_LABEL_COLOR_KEY);
        source = null;
    }

    /**
     * Method to get {@link #text} instance <br>
     * No-any params required
     *
     * @return {@link #text} instance as {@link String}
     */
    public String getText() {
        return text;
    }

    /**
     * Method to get {@link #color} instance <br>
     * No-any params required
     *
     * @return {@link #color} instance as {@link String}
     */
    public String getColor() {
        return color;
    }

    /**
     * Method to assemble and return an {@link ArrayList} of labels
     *
     * @param jLabels: labels list details formatted as JSON
     *
     * @return the labels list as {@link ArrayList} of {@link RevenueLabel}
     */
    @Returner
    public static ArrayList<RevenueLabel> returnLabels(JSONArray jLabels) {
        ArrayList<RevenueLabel> labels = new ArrayList<>();
        if (jLabels == null)
            return labels;
        for (int j = 0; j < jLabels.length(); j++)
            labels.add(new RevenueLabel(jLabels.getJSONObject(j)));
        return labels;
    }

}
