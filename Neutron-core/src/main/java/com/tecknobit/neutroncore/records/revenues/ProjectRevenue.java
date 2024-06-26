package com.tecknobit.neutroncore.records.revenues;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.neutroncore.records.NeutronItem;
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

/**
 * The {@code ProjectRevenue} class is useful to represent a project where it is possible have multiple revenues grouped
 * of the project itself
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronItem
 * @see Revenue
 */
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

    /**
     * {@code PROJECTS_KEY} the key for the <b>"/projects/"</b> field
     */
    public static final String PROJECTS_KEY = "/projects/";

    /**
     * {@code IS_PROJECT_REVENUE_KEY} the key for the <b>"is_project_revenue"</b> field
     */
    public static final String IS_PROJECT_REVENUE_KEY = "is_project_revenue";

    /**
     * {@code PROJECT_REVENUES_KEY} the key for the <b>"project_revenues"</b> field
     */
    public static final String PROJECT_REVENUES_KEY = "project_revenues";

    /**
     * {@code PROJECT_LABEL_COLOR} the color used to indicate the label of a project
     */
    public static final String PROJECT_LABEL_COLOR = "#a68cef";

    /**
     * {@code PROJECT_REVENUE} the key for the <b>"projectRevenue"</b> field
     */
    public static final String PROJECT_REVENUE = "projectRevenue";

    /**
     * {@code PROJECT_REVENUE_KEY} the key for the <b>"project_revenue"</b> field
     */
    public static final String PROJECT_REVENUE_KEY = "project_revenue";

    /**
     * {@code TICKETS_KEY} the key for the <b>"tickets"</b> field
     */
    public static final String TICKETS_KEY = "tickets";

    /**
     * {@code initialRevenue} the initial revenue attached to the project
     */
    @OneToOne(
            mappedBy = PROJECT_REVENUE
    )
    @JsonIgnoreProperties({
            PROJECT_REVENUE,
            "hibernateLazyInitializer",
            "handler"
    })
    private final InitialRevenue initialRevenue;

    /**
     * {@code tickets} the ticket attached to the project
     */
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

    /**
     * Constructor to init the {@link ProjectRevenue} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public ProjectRevenue() {
        this(null, null, -1, null, new ArrayList<>());
    }

    /**
     * Constructor to init the {@link ProjectRevenue} class
     *
     * @param id: identifier of the project
     * @param title: the title of the project
     * @param revenueDate: the date when the revenue has been created the project
     * @param initialRevenue: the initial revenue attached to the project
     */
    public ProjectRevenue(String id, String title, long revenueDate, InitialRevenue initialRevenue) {
        this(id, title, revenueDate, initialRevenue, List.of(), null);
    }

    /**
     * Constructor to init the {@link ProjectRevenue} class
     *
     * @param id: identifier of the project
     * @param title: the title of the project
     * @param revenueDate: the date when the revenue has been created the project
     * @param initialRevenue: the initial revenue attached to the project
     * @param tickets: the ticket attached to the project
     *
     */
    public ProjectRevenue(String id, String title, long revenueDate, InitialRevenue initialRevenue,
                          List<TicketRevenue> tickets) {
        this(id, title, revenueDate, initialRevenue, tickets, null);
    }

    /**
     * Constructor to init the {@link ProjectRevenue} class
     *
     * @param id: identifier of the project
     * @param title: the title of the project
     * @param revenueDate: the date when the revenue has been created the project
     * @param initialRevenue: the initial revenue attached to the project
     * @param tickets: the ticket attached to the project
     * @param owner: the owner of the project
     *
     */
    public ProjectRevenue(String id, String title, long revenueDate, InitialRevenue initialRevenue,
                          List<TicketRevenue> tickets, User owner) {
        super(id, title, -1, revenueDate, owner);
        this.initialRevenue = initialRevenue;
        this.tickets = tickets;
    }

    /**
     * Constructor to init the {@link ProjectRevenue} class
     *
     * @param jProjectRevenue: project details formatted as JSON
     *
     */
    public ProjectRevenue(JSONObject jProjectRevenue) {
        super(jProjectRevenue);
        initialRevenue = returnInitialRevenue(hItem.getJSONObject(INITIAL_REVENUE_KEY));
        tickets = returnTickets(hItem.getJSONArray(TICKETS_KEY));
    }

    /**
     * Method to get the total value of the project <br>
     * No-any params required
     *
     * @return the total value of the project as double
     */
    @Override
    @JsonIgnore
    public double getValue() {
        double totalValue = initialRevenue.getValue();
        for (TicketRevenue ticket : tickets)
            totalValue += ticket.getValue();
        return roundValue(totalValue, 2);
    }

    /**
     * Method to get {@link #initialRevenue} instance <br>
     * No-any params required
     *
     * @return {@link #initialRevenue} instance as {@link InitialRevenue}
     */
    @JsonGetter(INITIAL_REVENUE_KEY)
    public InitialRevenue getInitialRevenue() {
        return initialRevenue;
    }

    /**
     * Method to get {@link #tickets} instance <br>
     * No-any params required
     *
     * @return {@link #tickets} instance as {@link List} of {@link TicketRevenue}
     */
    public List<TicketRevenue> getTickets() {
        return tickets;
    }

    /**
     * Method to check whether a {@link TicketRevenue} is attached to the current project
     *
     * @param ticketTitle: the title of the ticket to check
     * @return whether the ticket is attached as boolean
     */
    public boolean hasTicket(String ticketTitle) {
        for (TicketRevenue ticket : tickets)
            if (ticket.getTitle().equals(ticketTitle))
                return true;
        return false;
    }

}
