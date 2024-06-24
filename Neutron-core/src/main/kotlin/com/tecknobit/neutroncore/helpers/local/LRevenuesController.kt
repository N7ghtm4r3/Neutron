package com.tecknobit.neutroncore.l;

import com.tecknobit.apimanager.annotations.Structure;
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue;
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue;
import com.tecknobit.neutroncore.records.revenues.RevenueLabel;
import com.tecknobit.neutroncore.records.revenues.TicketRevenue;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.*;
import static com.tecknobit.neutroncore.records.revenues.GeneralRevenue.*;
import static com.tecknobit.neutroncore.records.revenues.InitialRevenue.INITIAL_REVENUES_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECT_REVENUES_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECT_REVENUE_KEY;
import static com.tecknobit.neutroncore.records.revenues.RevenueLabel.REVENUE_LABELS_KEY;
import static com.tecknobit.neutroncore.records.revenues.RevenueLabel.*;
import static com.tecknobit.neutroncore.records.revenues.TicketRevenue.CLOSING_DATE_KEY;
import static com.tecknobit.neutroncore.records.revenues.TicketRevenue.TICKET_REVENUES_KEY;
import static java.lang.String.format;

@Structure
public abstract class LRevenuesController extends LServerController {

    protected static final String CREATE_PROJECT_REVENUES_TABLE = "CREATE TABLE IF NOT EXISTS " + PROJECT_REVENUES_KEY +
            " (\n" +
                IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                REVENUE_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                REVENUE_TITLE_KEY + " VARCHAR(30) NOT NULL" + ",\n" +
                OWNER_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                " FOREIGN KEY (" + OWNER_KEY + ") REFERENCES " + USERS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            + ");";

    protected static final String CREATE_INITIAL_REVENUES_TABLE = "CREATE TABLE IF NOT EXISTS " + INITIAL_REVENUES_KEY +
            " (\n" +
                IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                REVENUE_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                REVENUE_TITLE_KEY + " VARCHAR(30) NOT NULL" + ",\n" +
                REVENUE_VALUE_KEY + " REAL NOT NULL" + ",\n" +
                OWNER_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                " FOREIGN KEY (" + OWNER_KEY + ") REFERENCES " + USERS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE" +
                PROJECT_REVENUE_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                " FOREIGN KEY (" + PROJECT_REVENUE_KEY + ") REFERENCES " + PROJECT_REVENUES_KEY + "(" + IDENTIFIER_KEY + ") " +
                "ON DELETE CASCADE"
            + ");";

    protected static final String CREATE_TICKET_REVENUES_TABLE = "CREATE TABLE IF NOT EXISTS " + TICKET_REVENUES_KEY +
            " (\n" +
                IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                REVENUE_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                REVENUE_TITLE_KEY + " VARCHAR(30) NOT NULL" + ",\n" +
                REVENUE_VALUE_KEY + " REAL NOT NULL" + ",\n" +
                REVENUE_DESCRIPTION_KEY + " VARCHAR(255) NOT NULL" + ",\n" +
                CLOSING_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                OWNER_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                " FOREIGN KEY (" + OWNER_KEY + ") REFERENCES " + USERS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE" +
                PROJECT_REVENUE_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                " FOREIGN KEY (" + PROJECT_REVENUE_KEY + ") REFERENCES " + PROJECT_REVENUES_KEY + "(" + IDENTIFIER_KEY + ") " +
                "ON DELETE CASCADE"
            + ");";

    protected static final String CREATE_GENERAL_REVENUES_TABLE = "CREATE TABLE IF NOT EXISTS " + GENERAL_REVENUES_KEY +
            " (\n" +
                IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                REVENUE_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                REVENUE_TITLE_KEY + " VARCHAR(30) NOT NULL" + ",\n" +
                REVENUE_VALUE_KEY + " REAL NOT NULL" + ",\n" +
                REVENUE_DESCRIPTION_KEY + " VARCHAR(255) NOT NULL" + ",\n" +
                OWNER_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                " FOREIGN KEY (" + OWNER_KEY + ") REFERENCES " + USERS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
            + ");";

    protected static final String CREATE_REVENUE_LABELS_TABLE = "CREATE TABLE IF NOT EXISTS " + REVENUE_LABELS_KEY +
            " (\n" +
                IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                REVENUE_LABEL_COLOR_KEY + " VARCHAR(7) NOT NULL" + ",\n" +
                REVENUE_LABEL_TEXT_KEY + " TEXT NOT NULL" + ",\n" +
                REVENUE_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                " FOREIGN KEY (" + REVENUE_KEY + ") REFERENCES " + GENERAL_REVENUES_KEY + "(" + IDENTIFIER_KEY + ") " +
                "ON DELETE CASCADE"
            + ");";

    private static final String LIST_REVENUES_QUERY = "SELECT * FROM %s" + " WHERE " + OWNER_KEY + "='?'";

    protected static final String LIST_PROJECT_REVENUES_QUERY = format(LIST_REVENUES_QUERY, PROJECT_REVENUES_KEY);

    protected static final String LIST_GENERAL_REVENUES_QUERY = format(LIST_REVENUES_QUERY, GENERAL_REVENUES_KEY);

    protected static final String GET_REVENUE_LABELS_QUERY = "SELECT * FROM " + REVENUE_LABELS_KEY +
            " WHERE " + REVENUE_KEY + "='?'";

    protected static final String CREATE_PROJECT_REVENUE_QUERY = "INSERT INTO " + PROJECT_REVENUES_KEY +
            " (" +
                IDENTIFIER_KEY + "," +
                REVENUE_DATE_KEY + "," +
                REVENUE_TITLE_KEY + "," +
                OWNER_KEY +
            " ) VALUES (" +
                "?" + "," +
                "?" + "," +
                "?" + "," +
                "?"
            + ")";

    protected static final String ATTACH_INITIAL_REVENUE_QUERY = "INSERT INTO " + INITIAL_REVENUES_KEY +
            " (" +
                IDENTIFIER_KEY + "," +
                REVENUE_DATE_KEY + "," +
                REVENUE_VALUE_KEY + "," +
                OWNER_KEY + "," +
                PROJECT_REVENUE_KEY +
            " ) VALUES (" +
                "?" + "," +
                "?" + "," +
                "?" + "," +
                "?" + "," +
                "?"
            + ")";

    protected static final String CREATE_GENERAL_REVENUE_QUERY = "INSERT INTO " + GENERAL_REVENUES_KEY +
            " (" +
                IDENTIFIER_KEY + "," +
                REVENUE_DATE_KEY + "," +
                REVENUE_TITLE_KEY + "," +
                REVENUE_VALUE_KEY + "," +
                REVENUE_DESCRIPTION_KEY + "," +
                OWNER_KEY + "," +
            " ) VALUES (" +
                "?" + "," +
                "?" + "," +
                "?" + "," +
                "?" + "," +
                "?" + "," +
                "?"
            + ")";

    protected static final String ATTACH_LABEL_TO_REVENUE_QUERY = "INSERT INTO " + REVENUE_LABELS_KEY +
            " (" +
                IDENTIFIER_KEY + "," +
                REVENUE_LABEL_COLOR_KEY + "," +
                REVENUE_LABEL_TEXT_KEY + "," +
                REVENUE_KEY +
            " ) VALUES (" +
                "?" + "," +
                "?" + "," +
                "?" + "," +
                "?"
            + ")";

    protected static final String GET_PROJECT_REVENUE_QUERY = "SELECT * FROM " + PROJECT_REVENUES_KEY + " WHERE "
            + IDENTIFIER_KEY + "='?'" + " AND " + OWNER_KEY + "='?'";

    private static final String GET_PROJECT_REVENUE_EXTRA_INFO_QUERY = "SELECT * FROM %s" + " WHERE "
            + PROJECT_REVENUE_KEY + "='?'";

    protected static final String GET_INITIAL_REVENUE_QUERY = format(GET_PROJECT_REVENUE_EXTRA_INFO_QUERY, INITIAL_REVENUES_KEY);

    protected static final String GET_TICKETS_QUERY = format(GET_PROJECT_REVENUE_EXTRA_INFO_QUERY, TICKET_REVENUES_KEY);

    protected static final String ATTACH_TICKET_TO_PROJECT_QUERY = "INSERT INTO " + TICKET_REVENUES_KEY +
            " (" +
                IDENTIFIER_KEY + "," +
                REVENUE_DATE_KEY + "," +
                REVENUE_TITLE_KEY + "," +
                REVENUE_VALUE_KEY + "," +
                REVENUE_DESCRIPTION_KEY + "," +
                CLOSING_DATE_KEY + "," +
                OWNER_KEY + "," +
                PROJECT_REVENUE_KEY +
            " ) VALUES (" +
                "?" + "," +
                "?" + "," +
                "?" + "," +
                "?" + "," +
                "?" + "," +
                "?" + "," +
                "?" + "," +
                "?"
            + ")";

    protected static final String DELETE_REVENUE_QUERY = "DELETE FROM %s" + " WHERE " + IDENTIFIER_KEY + "='?'";

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

    public void addTicketToProjectRevenue(ProjectRevenue revenue, String ticketTitle, double ticketValue,
                                          String ticketDescription, long openingDate, long closingDate) {
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

    public void deleteProjectRevenue(ProjectRevenue revenue) {
        deleteProjectRevenue(revenue.getId());
    }

    public void deleteProjectRevenue(String revenueId) {
        deleteRevenue(PROJECT_REVENUES_KEY, revenueId);
    }

    public void deleteGeneralRevenue(GeneralRevenue revenue) {
        deleteGeneralRevenue(revenue.getId());
    }

    public void deleteGeneralRevenue(String revenueId) {
        deleteRevenue(GENERAL_REVENUES_KEY, revenueId);
    }

    public void deleteTicket(TicketRevenue ticket) {
        deleteTicket(ticket.getId());
    }

    public void deleteTicket(String revenueId) {
        deleteRevenue(TICKET_REVENUES_KEY, revenueId);
    }

    //String.format("Ciao %s a", "c")
    protected abstract void deleteRevenue(String table, String revenueId);
    
}
