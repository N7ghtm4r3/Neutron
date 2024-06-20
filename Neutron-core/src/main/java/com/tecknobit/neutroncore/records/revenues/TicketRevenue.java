package com.tecknobit.neutroncore.records.revenues;

import com.fasterxml.jackson.annotation.*;
import com.tecknobit.apimanager.annotations.Returner;
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

@Entity
@Table(name = TICKET_REVENUES_KEY)
@DiscriminatorValue("ticket")
public class TicketRevenue extends GeneralRevenue {

    public static final String TICKET_IDENTIFIER_KEY = "ticket_id";

    public static final String TICKET_REVENUES_KEY = "ticket_revenues";

    public static final String PENDING_TICKET_LABEL_COLOR = "#B5A422";

    public static final String CLOSED_TICKET_LABEL_COLOR = "#12b543";

    public static final String CLOSING_DATE_KEY = "closing_date";

    @Transient
    private static final RevenueLabel PENDING_TICKET_LABEL = new RevenueLabel(
            "Ticket",
            PENDING_TICKET_LABEL_COLOR
    );

    @Transient
    private static final RevenueLabel CLOSED_TICKET_LABEL = new RevenueLabel(
            "Ticket",
            CLOSED_TICKET_LABEL_COLOR
    );

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

    @Column(name = CLOSING_DATE_KEY)
    private long closingDate;

    public TicketRevenue() {
        this(null, null, 0, -1, null, -1, null, null);
    }

    public TicketRevenue(String id, String title, double value, long openingDate, String description) {
        this(id, title, value, openingDate, description, null);
    }

    public TicketRevenue(String id, String title, double value, long openingDate, String description, User owner) {
        this(id, title, value, openingDate, description, -1, null, null);
    }

    public TicketRevenue(String id, String title, double value, long openingDate, String description, long closingDate) {
        this(id, title, value, openingDate, description, closingDate, null, null);
    }

    public TicketRevenue(String id, String title, double value, long openingDate, String description, long closingDate,
                         User owner, ProjectRevenue projectRevenue) {
        super(id, title, value, openingDate, List.of(CLOSED_TICKET_LABEL), description, owner);
        this.closingDate = closingDate;
        this.projectRevenue = null;
    }

    public TicketRevenue(JSONObject jTicketRevenue) {
        super(jTicketRevenue);
        closingDate = hItem.getLong(CLOSING_DATE_KEY, -1);
        projectRevenue = null;
    }

    @JsonIgnore
    public long getDuration() {
        return closingDate - revenueDate;
    }

    @JsonGetter(CLOSING_DATE_KEY)
    public long getClosingTimestamp() {
        return closingDate;
    }

    public void setClosingDate(long closingDate) {
        this.closingDate = closingDate;
    }

    @JsonIgnore
    public String getClosingDate() {
        return timeFormatter.formatAsString(closingDate);
    }

    @JsonIgnore
    public boolean isClosed() {
        return closingDate != -1;
    }

    @Override
    @JsonIgnore
    public List<RevenueLabel> getLabels() {
        return super.getLabels();
    }

    @Override
    public JSONObject toTransferTarget() {
        JSONObject transferTarget = super.toTransferTarget();
        transferTarget.put(CLOSING_DATE_KEY, closingDate);
        transferTarget.remove(REVENUE_LABELS_KEY);
        return transferTarget;
    }

    @Returner
    public static ArrayList<TicketRevenue> returnTickets(JSONArray jTickets) {
        ArrayList<TicketRevenue> tickets = new ArrayList<>();
        if (jTickets == null)
            return tickets;
        for (int j = 0; j < jTickets.length(); j++)
            tickets.add(new TicketRevenue(jTickets.getJSONObject(j)));
        return tickets;
    }

    public RevenueLabel getCurrentLabel() {
        if(isClosed())
            return CLOSED_TICKET_LABEL;
        else
            return PENDING_TICKET_LABEL;
    }

}
