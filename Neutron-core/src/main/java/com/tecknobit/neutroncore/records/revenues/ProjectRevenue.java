package com.tecknobit.neutroncore.records.revenues;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.neutroncore.records.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.neutroncore.records.revenues.InitialRevenue.INITIAL_REVENUE_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.*;

@Entity
@Table(name = PROJECT_REVENUES_KEY)
public class ProjectRevenue extends Revenue {

    public static final String PROJECT_REVENUES_KEY = "project_revenues";

    public static final String PROJECT_LABEL_COLOR = "#a68cef";

    public static final String PROJECT_REVENUE = "projectRevenue";

    public static final String PROJECT_REVENUE_KEY = "project_revenue";

    public static final String TICKETS_KEY = "tickets";

    @OneToOne(
            mappedBy = PROJECT_REVENUE
    )
    @JsonIgnoreProperties({
            PROJECT_REVENUE,
            "hibernateLazyInitializer",
            "handler"
    })
    private final InitialRevenue initialRevenue;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = PROJECT_REVENUE
    )
    @JsonIgnoreProperties({
            PROJECT_REVENUE,
            "hibernateLazyInitializer",
            "handler"
    })
    private final List<TicketRevenue> tickets;

    public ProjectRevenue() {
        this(null, null, -1, null, new ArrayList<>());
    }

    public ProjectRevenue(String id, String title, long revenueDate, InitialRevenue initialRevenue,
                          List<TicketRevenue> tickets) {
        this(id, title, revenueDate, initialRevenue, tickets, null);
    }

    public ProjectRevenue(String id, String title, long revenueDate, InitialRevenue initialRevenue,
                          List<TicketRevenue> tickets, User owner) {
        super(id, title, -1, revenueDate, owner);
        this.initialRevenue = initialRevenue;
        this.tickets = tickets;
    }

    @Override
    public double getValue() {
        double totalValue = initialRevenue.getValue();
        for (TicketRevenue ticket : tickets)
            totalValue += ticket.getValue();
        return totalValue;
    }

    @JsonGetter(INITIAL_REVENUE_KEY)
    public InitialRevenue getInitialRevenue() {
        return initialRevenue;
    }

    public List<TicketRevenue> getTickets() {
        return tickets;
    }

}
