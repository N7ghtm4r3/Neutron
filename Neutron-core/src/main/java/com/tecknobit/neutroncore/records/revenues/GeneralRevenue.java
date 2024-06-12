package com.tecknobit.neutroncore.records.revenues;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.neutroncore.records.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.neutroncore.records.revenues.GeneralRevenue.GENERAL_REVENUES_KEY;

@Entity
@Table(name = GENERAL_REVENUES_KEY)
@DiscriminatorValue("general")
public class GeneralRevenue extends Revenue {

    public static final String GENERAL_REVENUES_KEY = "general_revenues";

    public static final String REVENUE_LABELS_KEY = "labels";

    public static final String REVENUE_DESCRIPTION_KEY = "description";

    public static final String REVENUE_SOURCE_KEY = "source";

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

    @Column(name = REVENUE_DESCRIPTION_KEY)
    protected final String description;

    public GeneralRevenue() {
        this(null, null, 0, -1, new ArrayList<>(), null, null);
    }

    public GeneralRevenue(String id, String title, double value, long revenueDate, List<RevenueLabel> labels,
                          String description, User owner) {
        super(id, title, value, revenueDate, owner);
        this.labels = labels;
        this.description = description;
    }

    public List<RevenueLabel> getLabels() {
        return labels;
    }

    public String getDescription() {
        return description;
    }

}
