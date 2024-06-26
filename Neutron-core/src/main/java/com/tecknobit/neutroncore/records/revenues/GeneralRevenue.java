package com.tecknobit.neutroncore.records.revenues;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.neutroncore.records.NeutronItem;
import com.tecknobit.neutroncore.records.User;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.neutroncore.records.revenues.GeneralRevenue.GENERAL_REVENUES_KEY;
import static com.tecknobit.neutroncore.records.revenues.RevenueLabel.returnLabels;

/**
 * The {@code GeneralRevenue} class is useful to represent a generic revenue inserted by the user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronItem
 * @see Revenue
 */
@Entity
@Table(name = GENERAL_REVENUES_KEY)
@DiscriminatorValue("general")
public class GeneralRevenue extends Revenue {

    /**
     * {@code GENERAL_REVENUES_KEY} the key for the <b>"general_revenues"</b> field
     */
    public static final String GENERAL_REVENUES_KEY = "general_revenues";

    /**
     * {@code REVENUE_LABELS_KEY} the key for the <b>"labels"</b> field
     */
    public static final String REVENUE_LABELS_KEY = "labels";

    /**
     * {@code REVENUE_DESCRIPTION_KEY} the key for the <b>"description"</b> field
     */
    public static final String REVENUE_DESCRIPTION_KEY = "description";

    /**
     * {@code REVENUE_SOURCE_KEY} the key for the <b>"source"</b> field
     */
    public static final String REVENUE_SOURCE_KEY = "source";

    /**
     * {@code labels} the labels attached to that revenue
     */
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = REVENUE_SOURCE_KEY
    )
    @Column(name = REVENUE_LABELS_KEY)
    @JsonIgnoreProperties({
            REVENUE_SOURCE_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    protected final List<RevenueLabel> labels;

    /**
     * {@code description} the description of the revenue
     */
    @Column(name = REVENUE_DESCRIPTION_KEY)
    protected final String description;

    /**
     * Constructor to init the {@link GeneralRevenue} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public GeneralRevenue() {
        this(null, null, 0, -1, new ArrayList<>(), null, null);
    }

    /**
     * Constructor to init the {@link GeneralRevenue} class
     *
     * @param id: identifier of the revenue
     * @param title: the title of the revenue
     * @param value: the amount value of the revenue
     * @param revenueDate: the date when the revenue has been inserted or its value has been received by the user
     * @param labels: the labels attached to that revenue
     * @param description: the description of the revenue
     *
     */
    public GeneralRevenue(String id, String title, double value, long revenueDate, List<RevenueLabel> labels,
                          String description) {
        this(id, title, value, revenueDate, labels, description, null);
    }

    /**
     * Constructor to init the {@link GeneralRevenue} class
     *
     * @param id: identifier of the revenue
     * @param title: the title of the revenue
     * @param value: the amount value of the revenue
     * @param revenueDate: the date when the revenue has been inserted or its value has been received by the user
     * @param labels: the labels attached to that revenue
     * @param description: the description of the revenue
     * @param owner: the owner of the revenue
     *
     */
    public GeneralRevenue(String id, String title, double value, long revenueDate, List<RevenueLabel> labels,
                          String description, User owner) {
        super(id, title, value, revenueDate, owner);
        this.labels = labels;
        this.description = description;
    }

    /**
     * Constructor to init the {@link GeneralRevenue} class
     *
     * @param jGeneralRevenue: revenue details formatted as JSON
     *
     */
    public GeneralRevenue(JSONObject jGeneralRevenue) {
        super(jGeneralRevenue);
        labels = returnLabels(hItem.getJSONArray(REVENUE_LABELS_KEY));
        description = hItem.getString(REVENUE_DESCRIPTION_KEY);
    }

    /**
     * Method to get {@link #labels} instance <br>
     * No-any params required
     *
     * @return {@link #labels} instance as {@link List} of {@link RevenueLabel}
     */
    public List<RevenueLabel> getLabels() {
        return labels;
    }

    /**
     * Method to get {@link #description} instance <br>
     * No-any params required
     *
     * @return {@link #description} instance as {@link String}
     */
    public String getDescription() {
        return description;
    }

}
