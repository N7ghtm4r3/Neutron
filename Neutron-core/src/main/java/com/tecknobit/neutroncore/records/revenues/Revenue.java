package com.tecknobit.neutroncore.records.revenues;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.neutroncore.records.NeutronItem;
import com.tecknobit.neutroncore.records.User;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.tecknobit.apimanager.trading.TradingTools.roundValue;
import static com.tecknobit.neutroncore.records.User.OWNER_KEY;
import static com.tecknobit.neutroncore.records.revenues.InitialRevenue.INITIAL_REVENUE_KEY;

/**
 * The {@code Revenue} class is useful to represent a basic revenue of the user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronItem
 */
@Structure
@MappedSuperclass
public abstract class Revenue extends NeutronItem {

    /**
     * {@code REVENUES_KEY} the key for the <b>"revenues"</b> field
     */
    public static final String REVENUES_KEY = "revenues";

    /**
     * {@code REVENUE_KEY} the key for the <b>"revenue"</b> field
     */
    public static final String REVENUE_KEY = "revenue";

    /**
     * {@code REVENUE_IDENTIFIER_KEY} the key for the <b>"revenue_id"</b> field
     */
    public static final String REVENUE_IDENTIFIER_KEY = "revenue_id";

    /**
     * {@code REVENUE_TITLE_KEY} the key for the <b>"title"</b> field
     */
    public static final String REVENUE_TITLE_KEY = "title";

    /**
     * {@code REVENUE_VALUE_KEY} the key for the <b>"value"</b> field
     */
    public static final String REVENUE_VALUE_KEY = "value";

    /**
     * {@code REVENUE_DATE_KEY} the key for the <b>"revenue_date"</b> field
     */
    public static final String REVENUE_DATE_KEY = "revenue_date";

    /**
     * {@code title} the title of the revenue
     */
    @Column(
            name = REVENUE_TITLE_KEY,
            columnDefinition = "VARCHAR(30) NOT NULL"
    )
    protected final String title;

    /**
     * {@code value} the amount value of the revenue
     */
    @Column(name = REVENUE_VALUE_KEY)
    protected final double value;

    /**
     * {@code revenueDate} the date when the revenue has been inserted or its value has been received by the user
     */
    @Column(name = REVENUE_DATE_KEY)
    protected final long revenueDate;

    /**
     * {@code owner} the owner of the revenue
     */
    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = OWNER_KEY)
    @JsonIgnoreProperties({
            REVENUES_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected final User owner;

    /**
     * Constructor to init the {@link Revenue} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public Revenue() {
        this(null, null, 0, -1, null);
    }

    /**
     * Constructor to init the {@link Revenue} class
     *
     * @param id: identifier of the revenue
     * @param title: the title of the revenue
     * @param value: the amount value of the revenue
     * @param revenueDate: the date when the revenue has been inserted or its value has been received by the user
     * @param owner: the owner of the revenue
     *
     */
    public Revenue(String id, String title, double value, long revenueDate, User owner) {
        super(id);
        this.title = title;
        this.value = value;
        this.revenueDate = revenueDate;
        this.owner = owner;
    }

    /**
     * Constructor to init the {@link Revenue} class
     *
     * @param jRevenue: revenue details formatted as JSON
     *
     */
    public Revenue(JSONObject jRevenue) {
        super(jRevenue);
        title = hItem.getString(REVENUE_TITLE_KEY);
        value = hItem.getDouble(REVENUE_VALUE_KEY);
        revenueDate = hItem.getLong(REVENUE_DATE_KEY);
        owner = null;
    }

    /**
     * Method to get {@link #title} instance <br>
     * No-any params required
     *
     * @return {@link #title} instance as {@link String}
     */
    public String getTitle() {
        return title;
    }

    /**
     * Method to get {@link #value} instance <br>
     * No-any params required
     *
     * @return {@link #value} instance as double
     */
    public double getValue() {
        return roundValue(value, 2);
    }

    /**
     * Method to get {@link #revenueDate} instance <br>
     * No-any params required
     *
     * @return {@link #revenueDate} instance as long
     */
    @JsonGetter(REVENUE_DATE_KEY)
    public long getRevenueTimestamp() {
        return revenueDate;
    }

    /**
     * Method to get {@link #revenueDate} instance <br>
     * No-any params required
     *
     * @return {@link #revenueDate} instance as {@link String}
     */
    @JsonIgnore
    public String getRevenueDate() {
        return timeFormatter.formatAsString(revenueDate);
    }

    /**
     * Method to assemble and return an {@link ArrayList} of revenues
     *
     * @param jRevenues: revenues list details formatted as JSON
     *
     * @return the revenues list as {@link ArrayList} of {@link Revenue}
     */
    @Returner
    public static ArrayList<Revenue> returnRevenues(JSONArray jRevenues) {
        ArrayList<Revenue> revenues = new ArrayList<>();
        if (jRevenues == null)
            return revenues;
        for (int j = 0; j < jRevenues.length(); j++) {
            JSONObject jRevenue = jRevenues.getJSONObject(j);
            if (jRevenue.has(INITIAL_REVENUE_KEY))
                revenues.add(new ProjectRevenue(jRevenue));
            else
                revenues.add(new GeneralRevenue(jRevenue));
        }
        return revenues;
    }

}
