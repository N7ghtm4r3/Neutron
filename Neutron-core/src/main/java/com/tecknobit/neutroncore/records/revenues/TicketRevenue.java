package com.tecknobit.neutroncore.records.revenues;

import com.fasterxml.jackson.annotation.*;
import com.tecknobit.apimanager.annotations.Returner;
import com.tecknobit.neutroncore.records.NeutronItem;
import com.tecknobit.neutroncore.records.User;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECT_REVENUE_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.TICKETS_KEY;
import static com.tecknobit.neutroncore.records.revenues.TicketRevenue.TICKET_REVENUES_KEY;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

/**
 * The {@code TicketRevenue} class is useful to represent a ticket added to a {@link ProjectRevenue}
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronItem
 * @see Revenue
 * @see GeneralRevenue
 */
@Entity
@Table(name = TICKET_REVENUES_KEY)
@DiscriminatorValue("ticket")
public class TicketRevenue extends GeneralRevenue {

    /**
     * {@code TICKET_IDENTIFIER_KEY} the key for the <b>"ticket_id"</b> field
     */
    public static final String TICKET_IDENTIFIER_KEY = "ticket_id";

    /**
     * {@code TICKET_REVENUES_KEY} the key for the <b>"ticket_revenues"</b> field
     */
    public static final String TICKET_REVENUES_KEY = "ticket_revenues";

    /**
     * {@code PENDING_TICKET_LABEL_COLOR} the color used to indicate a ticket still in pending
     */
    public static final String PENDING_TICKET_LABEL_COLOR = "#B5A422";

    /**
     * {@code CLOSED_TICKET_LABEL_COLOR} the color used to indicate a ticket already closed
     */
    public static final String CLOSED_TICKET_LABEL_COLOR = "#12b543";

    /**
     * {@code CLOSING_DATE_KEY} the key for the <b>"closing_date"</b> field
     */
    public static final String CLOSING_DATE_KEY = "closing_date";

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
     * Constructor to init the {@link TicketRevenue} class <br>
     *
     * No-any params required
     *
     * @apiNote empty constructor required
     */
    public TicketRevenue() {
        this(null, null, 0, -1, null, -1, null, null);
    }

    /**
     * Constructor to init the {@link TicketRevenue} class
     *
     * @param id: identifier of the ticket
     * @param title: the title of the ticket
     * @param value: the amount value of the ticket
     * @param openingDate: the date when the ticket has been inserted
     * @param description: the description of the revenue
     *
     */
    public TicketRevenue(String id, String title, double value, long openingDate, String description) {
        this(id, title, value, openingDate, description, null);
    }

    /**
     * Constructor to init the {@link TicketRevenue} class
     *
     * @param id: identifier of the ticket
     * @param title: the title of the ticket
     * @param value: the amount value of the ticket
     * @param openingDate: the date when the ticket has been inserted
     * @param description: the description of the revenue
     * @param owner: the owner of the ticket
     *
     */
    public TicketRevenue(String id, String title, double value, long openingDate, String description, User owner) {
        this(id, title, value, openingDate, description, -1, null, null);
    }

    /**
     * Constructor to init the {@link TicketRevenue} class
     *
     * @param id: identifier of the ticket
     * @param title: the title of the ticket
     * @param value: the amount value of the ticket
     * @param openingDate: the date when the ticket has been inserted
     * @param description: the description of the revenue
     * @param closingDate: the date when the ticket has been closed
     *
     */
    public TicketRevenue(String id, String title, double value, long openingDate, String description, long closingDate) {
        this(id, title, value, openingDate, description, closingDate, null, null);
    }

    /**
     * Constructor to init the {@link TicketRevenue} class
     *
     * @param id: identifier of the ticket
     * @param title: the title of the ticket
     * @param value: the amount value of the ticket
     * @param openingDate: the date when the ticket has been inserted
     * @param description: the description of the revenue
     * @param closingDate: the date when the ticket has been closed
     * @param owner: the owner of the ticket
     * @param projectRevenue: the project where the ticket is attached
     *
     */
    public TicketRevenue(String id, String title, double value, long openingDate, String description, long closingDate,
                         User owner, ProjectRevenue projectRevenue) {
        super(id, title, value, openingDate, List.of(CLOSED_TICKET_LABEL), description, owner);
        this.closingDate = closingDate;
        this.projectRevenue = null;
    }

    /**
     * Constructor to init the {@link TicketRevenue} class
     *
     * @param jTicketRevenue: ticket details formatted as JSON
     *
     */
    public TicketRevenue(JSONObject jTicketRevenue) {
        super(jTicketRevenue);
        closingDate = hItem.getLong(CLOSING_DATE_KEY, -1);
        projectRevenue = null;
    }

    /**
     * Method to get {@link #closingDate-revenueDate} instance <br>
     * No-any params required
     *
     * @return {@link #closingDate-revenueDate} instance as long
     */
    @JsonIgnore
    public long getDuration() {
        return closingDate - revenueDate;
    }

    /**
     * Method to get {@link #closingDate} instance <br>
     * No-any params required
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
     * @param closingDate: the date when the ticket has been closed
     */
    public void setClosingDate(long closingDate) {
        this.closingDate = closingDate;
    }

    /**
     * Method to get {@link #closingDate} instance <br>
     * No-any params required
     *
     * @return {@link #closingDate} instance as {@link String}
     */
    @JsonIgnore
    public String getClosingDate() {
        return timeFormatter.formatAsString(closingDate);
    }

    /**
     * Method to get whether the ticket is closed<br>
     * No-any params required
     *
     * @return whether the ticket is closed as boolean
     */
    @JsonIgnore
    public boolean isClosed() {
        return closingDate != -1;
    }

    /**
     * Method to get {@link #labels} instance <br>
     * No-any params required
     *
     * @return {@link #labels} instance as {@link List} of {@link RevenueLabel}
     */
    @Override
    @JsonIgnore
    public List<RevenueLabel> getLabels() {
        return super.getLabels();
    }

    /**
     * Method to assemble and return an {@link ArrayList} of tickets
     *
     * @param jTickets: tickets list details formatted as JSON
     *
     * @return the tickets list as {@link ArrayList} of {@link TicketRevenue}
     */
    @Returner
    public static ArrayList<TicketRevenue> returnTickets(JSONArray jTickets) {
        ArrayList<TicketRevenue> tickets = new ArrayList<>();
        if (jTickets == null)
            return tickets;
        for (int j = 0; j < jTickets.length(); j++)
            tickets.add(new TicketRevenue(jTickets.getJSONObject(j)));
        return tickets;
    }

    /**
     * Method to get the label to indicate the ticket status <br>
     * No-any params required
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
