package com.tecknobit.neutron.revenues.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.neutron.users.entity.NeutronUser;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.neutron.revenues.entities.GeneralRevenue.GENERAL_REVENUES_KEY;

/**
 * The {@code GeneralRevenue} class is useful to represent a generic revenue inserted by the user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem
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
    public static final String REVENUE_LABELS_KEY = "revenue_labels";

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
     * Constructor to init the {@link GeneralRevenue} class 
     *
     * @apiNote empty constructor required
     */
    public GeneralRevenue() {
        this(null, null, 0, -1, new ArrayList<>(), null, null);
    }

    /**
     * Constructor to init the {@link GeneralRevenue} class
     *
     * @param id The identifier of the revenue
     * @param title The title of the revenue
     * @param value The amount value of the revenue
     * @param revenueDate The date when the revenue has been inserted or its value has been received by the user
     * @param labels The labels attached to that revenue
     * @param description The description of the revenue
     *
     */
    public GeneralRevenue(String id, String title, double value, long revenueDate, List<RevenueLabel> labels,
                          String description) {
        this(id, title, value, revenueDate, labels, description, null);
    }

    /**
     * Constructor to init the {@link GeneralRevenue} class
     *
     * @param id The identifier of the revenue
     * @param title The title of the revenue
     * @param value The amount value of the revenue
     * @param revenueDate The date when the revenue has been inserted or its value has been received by the user
     * @param labels The labels attached to that revenue
     * @param description The description of the revenue
     * @param owner The owner of the revenue
     *
     */
    public GeneralRevenue(String id, String title, double value, long revenueDate, List<RevenueLabel> labels,
                          String description, NeutronUser owner) {
        super(id, title, value, revenueDate, owner);
        this.labels = labels;
        this.description = description;
    }

    /**
     * Method to get {@link #labels} instance 
     *
     * @return {@link #labels} instance as {@link List} of {@link RevenueLabel}
     */
    public List<RevenueLabel> getLabels() {
        return labels;
    }

    /**
     * Method to get {@link #description} instance 
     *
     * @return {@link #description} instance as {@link String}
     */
    public String getDescription() {
        return description;
    }

}
