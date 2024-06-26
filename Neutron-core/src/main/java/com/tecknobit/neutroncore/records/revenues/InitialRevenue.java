package com.tecknobit.neutroncore.records.revenues;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.neutroncore.records.NeutronItem;
import com.tecknobit.neutroncore.records.User;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.json.JSONObject;

import static com.tecknobit.neutroncore.records.revenues.InitialRevenue.INITIAL_REVENUES_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECT_REVENUE_KEY;
import static com.tecknobit.neutroncore.records.revenues.Revenue.REVENUE_TITLE_KEY;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

/**
 * The {@code InitialRevenue} class is useful to represent the first revenue of a {@link ProjectRevenue}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronItem
 * @see Revenue
 */
@Entity
@Table(name = INITIAL_REVENUES_KEY)
@AttributeOverride(
        name = REVENUE_TITLE_KEY,
        column = @Column(
                columnDefinition = "TEXT DEFAULT NULL",
                insertable = false
        )
)
public class InitialRevenue extends Revenue {

    /**
     * {@code INITIAL_REVENUES_KEY} the key for the <b>"initial_revenues"</b> field
     */
    public static final String INITIAL_REVENUES_KEY = "initial_revenues";

    /**
     * {@code INITIAL_REVENUE_KEY} the key for the <b>"initial_revenue"</b> field
     */
    public static final String INITIAL_REVENUE_KEY = "initial_revenue";

    /**
     * {@code INITIAL_REVENUE} the key for the <b>"initialRevenue"</b> field
     */
    public static final String INITIAL_REVENUE = "initialRevenue";

    /**
     * {@code projectRevenue} the project where the revenue is attached
     */
    @OneToOne
    @JoinColumn(name = PROJECT_REVENUE_KEY)
    @JsonIgnoreProperties({
            INITIAL_REVENUE,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = CASCADE)
    private final ProjectRevenue projectRevenue;

    /**
     * Constructor to init the {@link InitialRevenue} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public InitialRevenue() {
        this(null, 0, -1, null, null);
    }

    /**
     * Constructor to init the {@link InitialRevenue} class
     *
     * @param id: identifier of the revenue
     * @param value: the amount value of the revenue
     * @param revenueDate: the date when the revenue has been inserted or its value has been received by the user
     *
     */
    public InitialRevenue(String id, double value, long revenueDate) {
        this(id, value, revenueDate, null, null);
    }

    /**
     * Constructor to init the {@link InitialRevenue} class
     *
     * @param id: identifier of the revenue
     * @param value: the amount value of the revenue
     * @param revenueDate: the date when the revenue has been inserted
     * @param owner: the owner of the revenue
     *
     */
    public InitialRevenue(String id, double value, long revenueDate, User owner) {
        this(id, value, revenueDate, owner, null);
    }

    /**
     * Constructor to init the {@link InitialRevenue} class
     *
     * @param id: identifier of the revenue
     * @param value: the amount value of the revenue
     * @param revenueDate: the date when the revenue has been inserted
     * @param owner: the owner of the revenue
     * @param projectRevenue: the project where the revenue is attached
     *
     */
    public InitialRevenue(String id, double value, long revenueDate, User owner, ProjectRevenue projectRevenue) {
        super(id, INITIAL_REVENUE_KEY, value, revenueDate, owner);
        this.projectRevenue = projectRevenue;
    }

    /**
     * Constructor to init the {@link InitialRevenue} class
     *
     * @param jInitialRevenue: revenue details formatted as JSON
     *
     */
    public InitialRevenue(JSONObject jInitialRevenue) {
        super(jInitialRevenue);
        this.projectRevenue = null;
    }

    /**
     * Method to get {@link #title} instance <br>
     * No-any params required
     *
     * @return {@link #title} instance as {@link String}
     */
    @Override
    @JsonIgnore
    public String getTitle() {
        return super.getTitle();
    }

    /**
     * Method to get {@link #revenueDate} instance <br>
     * No-any params required
     *
     * @return {@link #revenueDate} instance as {@link String}
     */
    @Override
    @JsonIgnore
    public String getRevenueDate() {
        return super.getRevenueDate();
    }

    /**
     * Method to assemble and return a {@link InitialRevenue} instance
     *
     * @param jInitialRevenue: initial revenue details formatted as JSON
     *
     * @return the initial revenue instance as {@link InitialRevenue}
     */
    @Returner
    public static InitialRevenue returnInitialRevenue(JSONObject jInitialRevenue) {
        if (jInitialRevenue == null)
            return null;
        return new InitialRevenue(jInitialRevenue);
    }

}
