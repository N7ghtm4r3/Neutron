package com.tecknobit.neutroncore.records.revenues;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.neutroncore.records.User;
import jakarta.persistence.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.apimanager.trading.TradingTools.roundValue;
import static com.tecknobit.neutroncore.records.revenues.InitialRevenue.INITIAL_REVENUE_KEY;
import static com.tecknobit.neutroncore.records.revenues.InitialRevenue.returnInitialRevenue;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECT_REVENUES_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.REVENUE_VALUE_KEY;
import static com.tecknobit.neutroncore.records.revenues.TicketRevenue.returnTickets;

@Entity
@Table(name = PROJECT_REVENUES_KEY)
@AttributeOverride(
        name = REVENUE_VALUE_KEY,
        column = @Column(
                columnDefinition = "REAL DEFAULT 0",
                insertable = false
        )
)
public class ProjectRevenue extends Revenue {

    public static final String PROJECTS_KEY = "/projects/";

    public static final String IS_PROJECT_REVENUE_KEY = "is_project_revenue";

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
            fetch = FetchType.EAGER,
            mappedBy = PROJECT_REVENUE
    )
    @JsonIgnoreProperties({
            PROJECT_REVENUE,
            "hibernateLazyInitializer",
            "handler"
    })
    @OrderBy(REVENUE_DATE_KEY + " DESC")
    private final List<TicketRevenue> tickets;

    public ProjectRevenue() {
        this(null, null, -1, null, new ArrayList<>());
    }

    public ProjectRevenue(String id, String title, long revenueDate, InitialRevenue initialRevenue) {
        this(id, title, revenueDate, initialRevenue, List.of(), null);
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

    public ProjectRevenue(JSONObject jProjectRevenue) {
        super(jProjectRevenue);
        initialRevenue = returnInitialRevenue(hItem.getJSONObject(INITIAL_REVENUE_KEY));
        tickets = returnTickets(hItem.getJSONArray(TICKETS_KEY));
    }

    @Override
    @JsonIgnore
    public double getValue() {
        double totalValue = initialRevenue.getValue();
        for (TicketRevenue ticket : tickets)
            totalValue += ticket.getValue();
        return roundValue(totalValue, 2);
    }

    @JsonGetter(INITIAL_REVENUE_KEY)
    public InitialRevenue getInitialRevenue() {
        return initialRevenue;
    }

    public List<TicketRevenue> getTickets() {
        return tickets;
    }

    public boolean hasTicket(String ticketTitle) {
        for (TicketRevenue ticket : tickets)
            if (ticket.getTitle().equals(ticketTitle))
                return true;
        return false;
    }

}
