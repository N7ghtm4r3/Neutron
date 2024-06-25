package com.tecknobit.neutroncore.records.revenues;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.neutroncore.records.User;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.json.JSONObject;

import static com.tecknobit.neutroncore.records.revenues.InitialRevenue.INITIAL_REVENUES_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECT_REVENUE_KEY;
import static com.tecknobit.neutroncore.records.revenues.Revenue.REVENUE_TITLE_KEY;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

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
    @OnDelete(action = CASCADE)
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

    public InitialRevenue(JSONObject jInitialRevenue) {
        super(jInitialRevenue);
        this.projectRevenue = null;
    }

    @Override
    @JsonIgnore
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    @JsonIgnore
    public String getRevenueDate() {
        return super.getRevenueDate();
    }

    @Returner
    public static InitialRevenue returnInitialRevenue(JSONObject jInitialRevenue) {
        if (jInitialRevenue == null)
            return null;
        return new InitialRevenue(jInitialRevenue);
    }

}
