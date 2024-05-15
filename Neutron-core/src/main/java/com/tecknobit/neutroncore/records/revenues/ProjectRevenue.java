package com.tecknobit.neutroncore.records.revenues;

import java.util.List;

public class ProjectRevenue extends Revenue {

    public static final String PROJECT_LABEL_COLOR = "#a68cef";

    private final InitialRevenue initialRevenue;

    private final List<TicketRevenue> tickets;

    public ProjectRevenue(String id, String title, long revenueDate, InitialRevenue initialRevenue,
                          List<TicketRevenue> tickets) {
        super(id, title, -1, revenueDate);
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

    public InitialRevenue getInitialRevenue() {
        return initialRevenue;
    }

    public List<TicketRevenue> getTickets() {
        return tickets;
    }

}
