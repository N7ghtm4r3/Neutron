package com.tecknobit.neutroncore.l;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue;
import com.tecknobit.neutroncore.records.revenues.RevenueLabel;
import com.tecknobit.neutroncore.records.revenues.TicketRevenue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

@Structure
public abstract class LRevenuesController extends LServerController {

    public LRevenuesController(String userId, String userToken) {
        super(userId, userToken);
    }

    public abstract JSONArray list(String userId, String userToken);

    public abstract void createProjectRevenue(String title, double value, long revenueDate);

    public abstract void createGeneralRevenue(String title, String description, double value, long revenueDate,
                                              List<RevenueLabel> labels);

    public ProjectRevenue getProjectRevenue(ProjectRevenue revenue) {
        return new ProjectRevenue(getProjectRevenue(revenue.getId()));
    }

    public abstract JSONObject getProjectRevenue(String revenueId);

    public void addTicketToProjectRevenue(ProjectRevenue revenue, String ticketTitle, double ticketValue,
                                          String ticketDescription, long openingDate) {
        addTicketToProjectRevenue(revenue.getId(), ticketTitle, ticketValue, ticketDescription, openingDate);
    }
    
    public void addTicketToProjectRevenue(String revenueId, String ticketTitle, double ticketValue,
                                          String ticketDescription, long openingDate) {
        addTicketToProjectRevenue(revenueId, ticketTitle, ticketValue, ticketDescription, openingDate, -1);
    }

    public void addTicketToProjectRevenue(ProjectRevenue revenue, String ticketTitle,
                                          double ticketValue, String ticketDescription, long openingDate,
                                          long closingDate) {
        addTicketToProjectRevenue(revenue.getId(), ticketTitle, ticketValue, ticketDescription, openingDate, closingDate);
    }

    public abstract void addTicketToProjectRevenue(String revenueId, String ticketTitle,
                                                   double ticketValue, String ticketDescription, long openingDate,
                                                   long closingDate);

    public void closeProjectRevenueTicket(ProjectRevenue revenue, TicketRevenue ticket) {
        closeProjectRevenueTicket(revenue.getId(), ticket.getId());
    }

    public abstract void closeProjectRevenueTicket(String revenueId, String ticketId);

    public void deleteProjectRevenueTicket(ProjectRevenue revenue, TicketRevenue ticket) {
        deleteProjectRevenueTicket(revenue.getId(), ticket.getId());
    }

    public abstract void deleteProjectRevenueTicket(String revenueId, String ticketId);

    public void deleteRevenue(ProjectRevenue revenue) {
        deleteRevenue(revenue.getId());
    }

    public abstract void deleteRevenue(String revenueId);
    
}
