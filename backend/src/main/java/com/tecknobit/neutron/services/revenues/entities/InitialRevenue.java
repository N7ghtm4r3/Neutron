package com.tecknobit.neutron.services.revenues.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.neutron.services.users.entity.NeutronUser;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;

import static com.tecknobit.neutroncore.ContantsKt.*;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

/**
 * The {@code InitialRevenue} class is useful to represent the first revenue of a {@link ProjectRevenue}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem
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
     * Constructor to init the {@link InitialRevenue} class 
     *
     * @apiNote empty constructor required
     */
    public InitialRevenue() {
        this(null, 0, -1, null, null);
    }

    /**
     * Constructor to init the {@link InitialRevenue} class
     *
     * @param id The identifier of the revenue
     * @param value The amount value of the revenue
     * @param revenueDate The date when the revenue has been inserted or its value has been received by the user
     *
     */
    public InitialRevenue(String id, double value, long revenueDate) {
        this(id, value, revenueDate, null, null);
    }

    /**
     * Constructor to init the {@link InitialRevenue} class
     *
     * @param id The identifier of the revenue
     * @param value The amount value of the revenue
     * @param revenueDate The date when the revenue has been inserted
     * @param owner The owner of the revenue
     *
     */
    public InitialRevenue(String id, double value, long revenueDate, NeutronUser owner) {
        this(id, value, revenueDate, owner, null);
    }

    /**
     * Constructor to init the {@link InitialRevenue} class
     *
     * @param id The identifier of the revenue
     * @param value The amount value of the revenue
     * @param revenueDate The date when the revenue has been inserted
     * @param owner The owner of the revenue
     * @param projectRevenue The project where the revenue is attached
     *
     */
    public InitialRevenue(String id, double value, long revenueDate, NeutronUser owner, ProjectRevenue projectRevenue) {
        super(id, INITIAL_REVENUE_KEY, value, revenueDate, owner);
        this.projectRevenue = projectRevenue;
    }

    /**
     * Method to get {@link #title} instance 
     *
     * @return {@link #title} instance as {@link String}
     */
    @Override
    @JsonIgnore
    public String getTitle() {
        return super.getTitle();
    }

}
