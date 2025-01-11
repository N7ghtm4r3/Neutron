package com.tecknobit.neutron.revenues.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONObject;

import static com.tecknobit.neutron.revenues.entities.GeneralRevenue.REVENUE_SOURCE_KEY;
import static com.tecknobit.neutron.revenues.entities.Revenue.REVENUE_KEY;
import static com.tecknobit.neutron.revenues.entities.RevenueLabel.REVENUE_LABELS_KEY;

/**
 * The {@code RevenueLabel} class is useful to represent a label attached to a {@link GeneralRevenue}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see Revenue
 */
@Entity
@Table(name = REVENUE_LABELS_KEY)
public class RevenueLabel extends EquinoxItem {

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
     * Constructor to init the {@link RevenueLabel} class 
     *
     * @apiNote empty constructor required
     */
    public RevenueLabel() {
        this(null, null, null, null);
    }

    /**
     * Constructor to init the {@link RevenueLabel} class
     *
     * @param text The text of the label
     * @param color The color of the label
     *
     */
    public RevenueLabel(String text, String color) {
        this(null, text, color, null);
    }

    /**
     * Constructor to init the {@link RevenueLabel} class
     *
     * @param id The identifier of the label
     * @param text The text of the label
     * @param color The color of the label
     * @param source The general revenue where the label is attached
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

}
