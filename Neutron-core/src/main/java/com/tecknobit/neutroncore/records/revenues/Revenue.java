package com.tecknobit.neutroncore.records.revenues;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.neutroncore.records.NeutronItem;
import com.tecknobit.neutroncore.records.User;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import static com.tecknobit.neutroncore.records.User.OWNER_KEY;

@Structure
@MappedSuperclass
public abstract class Revenue extends NeutronItem {

    public static final String REVENUES_KEY = "revenues";

    public static final String REVENUE_KEY = "revenue";

    public static final String REVENUE_IDENTIFIER_KEY = "revenue_id";

    public static final String REVENUE_TITLE_KEY = "title";

    public static final String REVENUE_VALUE_KEY = "value";

    public static final String REVENUE_DATE_KEY = "revenue_date";

    @Column(
            name = REVENUE_TITLE_KEY,
            columnDefinition = "VARCHAR(30) NOT NULL"
    )
    protected final String title;

    @Column(name = REVENUE_VALUE_KEY)
    protected final double value;

    @Column(name = REVENUE_DATE_KEY)
    protected final long revenueDate;

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

    public Revenue() {
        this(null, null, 0, -1, null);
    }

    public Revenue(String id, String title, double value, long revenueDate, User owner) {
        super(id);
        this.title = title;
        this.value = value;
        this.revenueDate = revenueDate;
        this.owner = null;
    }

    public String getTitle() {
        return title;
    }

    public double getValue() {
        return value;
    }

    @JsonGetter(REVENUE_DATE_KEY)
    public long getRevenueTimestamp() {
        return revenueDate;
    }

    @Transient
    public String getRevenueDate() {
        return timeFormatter.formatAsString(revenueDate);
    }

}
