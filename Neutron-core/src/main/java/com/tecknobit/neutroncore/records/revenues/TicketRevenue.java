package com.tecknobit.neutroncore.records.revenues;

import java.util.List;

public class TicketRevenue extends GeneralRevenue {

    public static final String PENDING_TICKET_LABEL_COLOR = "#B5A422";

    public static final String CLOSED_TICKET_LABEL_COLOR = "#12b543";

    private static final RevenueLabel PENDING_TICKET_LABEL = new RevenueLabel(
            "pending_ticket_id",
            "Ticket",
            PENDING_TICKET_LABEL_COLOR
    );

    private static final RevenueLabel CLOSED_TICKET_LABEL = new RevenueLabel(
            "closed_ticket_id",
            "Ticket",
            CLOSED_TICKET_LABEL_COLOR
    );

    private final long closingDate;

    private boolean isClosed;

    public TicketRevenue(String id, String title, double value, long openingDate, String description) {
        super(id, title, value, openingDate, List.of(PENDING_TICKET_LABEL), description);
        this.closingDate = -1;
        this.isClosed = false;
    }

    public TicketRevenue(String id, String title, double value, long openingDate, String description, long closingDate) {
        super(id, title, value, openingDate, List.of(CLOSED_TICKET_LABEL), description);
        this.closingDate = closingDate;
        this.isClosed = true;
    }

    public long getDuration() {
        return closingDate - revenueDate;
    }

    public long getClosingTimestamp() {
        return closingDate;
    }

    public String getClosingDate() {
        return timeFormatter.formatAsString(closingDate);
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public boolean isClosed() {
        return isClosed;
    }

}
