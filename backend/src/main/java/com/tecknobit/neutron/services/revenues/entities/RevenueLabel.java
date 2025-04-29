package com.tecknobit.neutron.services.revenues.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper.ComplexBatchItem;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONObject;

import java.util.List;

import static com.tecknobit.neutroncore.ContantsKt.*;

/**
 * The {@code RevenueLabel} class is useful to represent a label attached to a {@link GeneralRevenue}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see EquinoxItem
 * @see Revenue
 * @see ComplexBatchItem
 */
@Entity
@Table(name = LABELS_KEY)
public class RevenueLabel extends EquinoxItem {

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
     * {@code source} the general revenues where the label is attached
     */
    @ManyToMany(
            cascade = CascadeType.ALL,
            mappedBy = LABELS_KEY
    )
    @JsonIgnoreProperties({
            REVENUE_SOURCE_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final List<GeneralRevenue> source;

    /**
     * Constructor to init the {@link RevenueLabel} class 
     *
     * @apiNote empty constructor required
     */
    @EmptyConstructor
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
    public RevenueLabel(String id, String text, String color, List<GeneralRevenue> source) {
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
     * Method to get {@link #text} instance 
     *
     * @return {@link #text} instance as {@link String}
     */
    public String getText() {
        return text;
    }

    /**
     * Method to get {@link #color} instance 
     *
     * @return {@link #color} instance as {@link String}
     */
    public String getColor() {
        return color;
    }

}
