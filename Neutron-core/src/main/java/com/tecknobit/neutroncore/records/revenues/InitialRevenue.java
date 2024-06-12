package com.tecknobit.neutroncore.records.revenues;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.neutroncore.records.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import static com.tecknobit.neutroncore.records.revenues.InitialRevenue.INITIAL_REVENUES_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECT_REVENUE_KEY;

@Entity
@Table(name = INITIAL_REVENUES_KEY)
public class InitialRevenue extends Revenue {

    public static final String INITIAL_REVENUES_KEY = "initial_revenues";

    public static final String INITIAL_REVENUE_KEY = "initial_revenue";

    public static final String INITIAL_REVENUE = "initialRevenue";

    @OneToOne
    @JoinColumn(name = PROJECT_REVENUE_KEY)
    @JsonIgnoreProperties({
            INITIAL_REVENUE,
            "hibernateLazyInitializer",
            "handler"
    })
    private final ProjectRevenue projectRevenue;

    public InitialRevenue() {
        this(null, 0, -1, null, null);
    }

    public InitialRevenue(String id, double value, long revenueDate) {
        this(id, value, revenueDate, null, null);
    }

    public InitialRevenue(String id, double value, long revenueDate, User owner) {
        this(id, value, revenueDate, owner, null);
    }

    public InitialRevenue(String id, double value, long revenueDate, User owner, ProjectRevenue projectRevenue) {
        super(id, INITIAL_REVENUE_KEY, value, revenueDate, owner);
        this.projectRevenue = projectRevenue;
    }

}
