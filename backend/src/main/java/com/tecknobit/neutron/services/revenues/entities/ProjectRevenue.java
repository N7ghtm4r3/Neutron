package com.tecknobit.neutron.services.revenues.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.apimanager.trading.TradingTools;
import com.tecknobit.equinoxbackend.annotations.EmptyConstructor;
import com.tecknobit.neutron.services.users.entity.NeutronUser;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.neutroncore.ContantsKt.*;

/**
 * The {@code ProjectRevenue} class is useful to represent a project where it is possible have multiple revenues grouped
 * of the project itself
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem
 * @see Revenue
 */
@Entity
@Table(
        name = PROJECT_REVENUES_KEY,
        uniqueConstraints = @UniqueConstraint(
                columnNames = REVENUE_TITLE_KEY
        )
)
@AttributeOverride(
        name = REVENUE_VALUE_KEY,
        column = @Column(
                columnDefinition = "REAL DEFAULT 0",
                insertable = false
        )
)
public class ProjectRevenue extends Revenue {

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
     * Constructor to init the {@link ProjectRevenue} class
     */
    @EmptyConstructor
    public ProjectRevenue() {
        this(null, null, -1, null, new ArrayList<>());
    }

    /**
     * Constructor to init the {@link ProjectRevenue} class
     *
     * @param id The identifier of the project
     * @param title The title of the project
     * @param revenueDate The last date of a revenue
     * @param initialRevenue The initial revenue attached to the project
     * @param tickets The ticket attached to the project
     *
     */
    public ProjectRevenue(String id, String title, long revenueDate, InitialRevenue initialRevenue,
                          List<TicketRevenue> tickets) {
        this(id, title, revenueDate, initialRevenue, tickets, null);
    }

    /**
     * Constructor to init the {@link ProjectRevenue} class
     *
     * @param id The identifier of the project
     * @param title The title of the project
     * @param revenueDate The last date of a revenue
     * @param initialRevenue The initial revenue attached to the project
     * @param tickets The ticket attached to the project
     * @param owner The owner of the project
     *
     */
    public ProjectRevenue(String id, String title, long revenueDate, InitialRevenue initialRevenue,
                          List<TicketRevenue> tickets, NeutronUser owner) {
        super(id, title, -1, revenueDate, owner);
        this.initialRevenue = initialRevenue;
        this.tickets = tickets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getValue() {
        double value = initialRevenue.getValue();
        for (TicketRevenue ticket : tickets)
            value += ticket.getValue();
        return TradingTools.roundValue(value, 2);
    }

    /**
     * Method to get {@link #initialRevenue} instance 
     *
     * @return {@link #initialRevenue} instance as {@link InitialRevenue}
     */
    @JsonGetter(INITIAL_REVENUE_KEY)
    public InitialRevenue getInitialRevenue() {
        return initialRevenue;
    }

    /**
     * Method to get {@link #tickets} instance 
     *
     * @return {@link #tickets} instance as {@link List} of {@link TicketRevenue}
     */
    public List<TicketRevenue> getTickets() {
        return tickets;
    }

    /**
     * Method to check whether a {@link TicketRevenue} is attached to the current project
     *
     * @param ticketTitle The title of the ticket to check
     * @return whether the ticket is attached as boolean
     */
    public boolean hasTicket(String ticketTitle) {
        for (TicketRevenue ticket : tickets)
            if (ticket.getTitle().equals(ticketTitle))
                return true;
        return false;
    }

    /**
     * Method to check whether a {@link TicketRevenue} is attached to the current project
     *
     * @param ticketId The identifier of the ticket to check
     * @return whether the ticket is attached as {@link TicketRevenue}, if not exists null
     */
    public TicketRevenue hasTicketById(String ticketId) {
        for (TicketRevenue ticket : tickets)
            if (ticket.getId().equals(ticketId))
                return ticket;
        return null;
    }

}
