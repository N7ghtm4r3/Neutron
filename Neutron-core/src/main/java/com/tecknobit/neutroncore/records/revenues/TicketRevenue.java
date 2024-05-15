package com.tecknobit.neutroncore.records.revenues;

import java.util.List;

public class TicketRevenue extends GeneralRevenue {

    public static final String TICKET_LABEL_COLOR = "#B5A422";

    private static final RevenueLabel label = new RevenueLabel(
            "label_ticket_id",
            "Ticket",
            TICKET_LABEL_COLOR
    );

    private final long closingDate;

    public TicketRevenue(String id, String title, double value, long openingDate, String description, long closingDate) {
        super(id, title, value, openingDate, List.of(label), description);
        this.closingDate = closingDate;
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

}
