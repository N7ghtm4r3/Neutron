package com.tecknobit.neutron.services.revenues.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem;
import com.tecknobit.neutron.services.users.entity.NeutronUser;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static com.tecknobit.apimanager.trading.TradingTools.roundValue;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.OWNER_KEY;
import static com.tecknobit.neutroncore.ContantsKt.*;

/**
 * The {@code Revenue} class is useful to represent a basic revenue of the user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem
 */
@Structure
@MappedSuperclass
public abstract class Revenue extends EquinoxItem {

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
    protected final NeutronUser owner;

    /**
     * Constructor to init the {@link Revenue} class
     */
    @EmptyConstructor
    public Revenue() {
        this(null, null, 0, -1, null);
    }

    /**
     * Constructor to init the {@link Revenue} class
     *
     * @param id Identifier of the revenue
     * @param title The title of the revenue
     * @param value The amount value of the revenue
     * @param revenueDate The date when the revenue has been inserted or its value has been received by the user
     * @param owner The owner of the revenue
     *
     */
    public Revenue(String id, String title, double value, long revenueDate, NeutronUser owner) {
        super(id);
        this.title = title;
        this.value = value;
        this.revenueDate = revenueDate;
        this.owner = owner;
    }

    /**
     * Method to get {@link #title} instance 
     *
     * @return {@link #title} instance as {@link String}
     */
    public String getTitle() {
        return title;
    }

    /**
     * Method to get {@link #value} instance 
     *
     * @return {@link #value} instance as double
     */
    public double getValue() {
        return roundValue(value, 2);
    }

    /**
     * Method to get {@link #revenueDate} instance 
     *
     * @return {@link #revenueDate} instance as long
     */
    @JsonGetter(REVENUE_DATE_KEY)
    public long getRevenueTimestamp() {
        return revenueDate;
    }

}
