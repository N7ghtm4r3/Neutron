package com.tecknobit.neutroncore.records.revenues;

import com.tecknobit.neutroncore.records.NeutronItem;

import java.util.List;

public class ProjectRevenue extends Revenue {

    public static final String PROJECT_LABEL_COLOR = "#a68cef";

    private final GeneralRevenue entryRevenue;

    private final List<Ticket> tickets;

    public ProjectRevenue(String id, String title, long revenueDate, GeneralRevenue entryRevenue,
                          List<Ticket> tickets) {
        super(id, title, -1, revenueDate);
        this.entryRevenue = entryRevenue;
        this.tickets = tickets;
    }

    @Override
    public double getValue() {
        double totalValue = entryRevenue.getValue();
        for (Ticket ticket : tickets)
            totalValue += ticket.getRevenue();
        return totalValue;
    }

    public Revenue getEntryRevenue() {
        return entryRevenue;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public static class Ticket extends NeutronItem {

        private final double revenue;

        private final long duration;

        private final long closingDate;

        public Ticket(String id, double revenue, long duration, long closingDate) {
            super(id);
            this.revenue = revenue;
            this.duration = duration;
            this.closingDate = closingDate;
        }

        public double getRevenue() {
            return revenue;
        }

        public long getDuration() {
            return duration;
        }

        public long getClosingTimestamp() {
            return closingDate;
        }

        public String getClosingDate() {
            return timeFormatter.formatAsString(closingDate);
        }

    }

}
