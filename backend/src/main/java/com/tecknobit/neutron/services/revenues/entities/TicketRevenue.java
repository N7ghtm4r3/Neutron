package com.tecknobit.neutron.services.revenues.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tecknobit.neutron.services.users.entity.NeutronUser;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;

import java.util.List;

import static com.tecknobit.neutroncore.ContantsKt.*;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

/**
 * The {@code TicketRevenue} class is useful to represent a ticket added to a {@link ProjectRevenue}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem
 * @see Revenue
 * @see GeneralRevenue
 */
@Entity
@Table(name = TICKET_REVENUES_KEY)
@DiscriminatorValue("ticket")
public class TicketRevenue extends GeneralRevenue {

    /**
     * {@code PENDING_TICKET_LABEL} the label used when the ticket is pending
     */
    @Transient
    private static final RevenueLabel PENDING_TICKET_LABEL = new RevenueLabel(
            "Ticket",
            PENDING_TICKET_LABEL_COLOR
    );

    /**
     * {@code CLOSED_TICKET_LABEL} the label used when the ticket is closed
     */
    @Transient
    private static final RevenueLabel CLOSED_TICKET_LABEL = new RevenueLabel(
            "Ticket",
            CLOSED_TICKET_LABEL_COLOR
    );

    /**
     * {@code projectRevenue} the project where the ticket is attached
     */
    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = PROJECT_REVENUE_KEY)
    @JsonIgnoreProperties({
            TICKETS_KEY,
            "hibernateLazyInitializer",
            "handler"
    })
    @OnDelete(action = CASCADE)
    private final ProjectRevenue projectRevenue;

    /**
     * {@code closingDate} the date when the ticket has been closed
     */
    @Column(name = CLOSING_DATE_KEY)
    private long closingDate;

    /**
     * Constructor to init the {@link TicketRevenue} class 
     *
     * @apiNote empty constructor required
     */
    public TicketRevenue() {
        this(null, null, 0, -1, null, -1, null, null);
    }

    /**
     * Constructor to init the {@link TicketRevenue} class
     *
     * @param id The identifier of the ticket
     * @param title The title of the ticket
     * @param value The amount value of the ticket
     * @param openingDate The date when the ticket has been inserted
     * @param description The description of the revenue
     *
     */
    public TicketRevenue(String id, String title, double value, long openingDate, String description) {
        this(id, title, value, openingDate, description, null);
    }

    /**
     * Constructor to init the {@link TicketRevenue} class
     *
     * @param id The identifier of the ticket
     * @param title The title of the ticket
     * @param value The amount value of the ticket
     * @param openingDate The date when the ticket has been inserted
     * @param description The description of the revenue
     * @param owner The owner of the ticket
     *
     */
    public TicketRevenue(String id, String title, double value, long openingDate, String description, NeutronUser owner) {
        this(id, title, value, openingDate, description, -1, null, null);
    }

    /**
     * Constructor to init the {@link TicketRevenue} class
     *
     * @param id The identifier of the ticket
     * @param title The title of the ticket
     * @param value The amount value of the ticket
     * @param openingDate The date when the ticket has been inserted
     * @param description The description of the revenue
     * @param closingDate The date when the ticket has been closed
     *
     */
    public TicketRevenue(String id, String title, double value, long openingDate, String description, long closingDate) {
        this(id, title, value, openingDate, description, closingDate, null, null);
    }

    /**
     * Constructor to init the {@link TicketRevenue} class
     *
     * @param id The identifier of the ticket
     * @param title The title of the ticket
     * @param value The amount value of the ticket
     * @param openingDate The date when the ticket has been inserted
     * @param description The description of the revenue
     * @param closingDate The date when the ticket has been closed
     * @param owner The owner of the ticket
     * @param projectRevenue The project where the ticket is attached
     *
     */
    public TicketRevenue(String id, String title, double value, long openingDate, String description, long closingDate,
                         NeutronUser owner, ProjectRevenue projectRevenue) {
        super(id, title, value, openingDate, List.of(CLOSED_TICKET_LABEL), description, owner);
        this.closingDate = closingDate;
        this.projectRevenue = null;
    }

    /**
     * Method to get {@link #closingDate-revenueDate} instance 
     *
     * @return {@link #closingDate-revenueDate} instance as long
     */
    @JsonIgnore
    public long getDuration() {
        return closingDate - revenueDate;
    }

    /**
     * Method to get {@link #closingDate} instance 
     *
     * @return {@link #closingDate} instance as long
     */
    @JsonGetter(CLOSING_DATE_KEY)
    public long getClosingTimestamp() {
        return closingDate;
    }

    /**
     * Method to set the {@link #closingDate} instance <br>
     *
     * @param closingDate The date when the ticket has been closed
     */
    public void setClosingDate(long closingDate) {
        this.closingDate = closingDate;
    }

    /**
     * Method to get {@link #closingDate} instance 
     *
     * @return {@link #closingDate} instance as {@link String}
     */
    @JsonIgnore
    public String getClosingDate() {
        return timeFormatter.formatAsString(closingDate);
    }

    /**
     * Method to get whether the ticket is closed
     *
     * @return whether the ticket is closed as boolean
     */
    @JsonIgnore
    public boolean isClosed() {
        return closingDate != -1;
    }

    /**
     * Method to get the label to indicate the ticket status 
     *
     * @return the label to indicate the ticket status as {@link RevenueLabel}
     */
    public RevenueLabel getCurrentLabel() {
        if(isClosed())
            return CLOSED_TICKET_LABEL;
        else
            return PENDING_TICKET_LABEL;
    }

}
