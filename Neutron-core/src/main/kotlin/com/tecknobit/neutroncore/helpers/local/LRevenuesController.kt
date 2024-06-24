package com.tecknobit.neutroncore.helpers.local

import com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY
import com.tecknobit.neutroncore.records.User.OWNER_KEY
import com.tecknobit.neutroncore.records.User.USERS_KEY
import com.tecknobit.neutroncore.records.revenues.*
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECT_REVENUES_KEY
import com.tecknobit.neutroncore.records.revenues.RevenueLabel.REVENUE_LABEL_COLOR_KEY
import com.tecknobit.neutroncore.records.revenues.RevenueLabel.REVENUE_LABEL_TEXT_KEY
import com.tecknobit.neutroncore.records.revenues.TicketRevenue.*
import org.json.JSONArray
import org.json.JSONObject

interface LRevenuesController : LNeutronController {
    
    companion object {

        protected const val CREATE_PROJECT_REVENUES_TABLE: String = (
                "CREATE TABLE IF NOT EXISTS " + PROJECT_REVENUES_KEY +
                    " (\n" +
                        IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                        REVENUE_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                        REVENUE_TITLE_KEY + " VARCHAR(30) NOT NULL" + ",\n" +
                        OWNER_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        " FOREIGN KEY (" + OWNER_KEY + ") REFERENCES " + USERS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
                    + ");"
                )

        protected const val CREATE_INITIAL_REVENUES_TABLE: String = (
                "CREATE TABLE IF NOT EXISTS " + InitialRevenue.INITIAL_REVENUES_KEY +
                    " (\n" +
                        IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                        REVENUE_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                        REVENUE_TITLE_KEY + " VARCHAR(30) NOT NULL" + ",\n" +
                        REVENUE_VALUE_KEY + " REAL NOT NULL" + ",\n" +
                        OWNER_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        " FOREIGN KEY (" + OWNER_KEY + ") REFERENCES " + USERS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE" +
                        ProjectRevenue.PROJECT_REVENUE_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        " FOREIGN KEY (" + ProjectRevenue.PROJECT_REVENUE_KEY + ") REFERENCES " + PROJECT_REVENUES_KEY + "(" + IDENTIFIER_KEY + ") " +
                        "ON DELETE CASCADE"
                    + ");"
                )

        protected const val CREATE_TICKET_REVENUES_TABLE: String = (
                "CREATE TABLE IF NOT EXISTS " + TICKET_REVENUES_KEY +
                    " (\n" +
                        IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                        REVENUE_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                        REVENUE_TITLE_KEY + " VARCHAR(30) NOT NULL" + ",\n" +
                        REVENUE_VALUE_KEY + " REAL NOT NULL" + ",\n" +
                        REVENUE_DESCRIPTION_KEY + " VARCHAR(255) NOT NULL" + ",\n" +
                        CLOSING_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                        OWNER_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        " FOREIGN KEY (" + OWNER_KEY + ") REFERENCES " + USERS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE" +
                        ProjectRevenue.PROJECT_REVENUE_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        " FOREIGN KEY (" + ProjectRevenue.PROJECT_REVENUE_KEY + ") REFERENCES " + PROJECT_REVENUES_KEY + "(" + IDENTIFIER_KEY + ") " +
                        "ON DELETE CASCADE"
                    + ");"
                )

        protected const val CREATE_GENERAL_REVENUES_TABLE: String = (
                "CREATE TABLE IF NOT EXISTS " + GENERAL_REVENUES_KEY +
                    " (\n" +
                        IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                        REVENUE_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                        REVENUE_TITLE_KEY + " VARCHAR(30) NOT NULL" + ",\n" +
                        REVENUE_VALUE_KEY + " REAL NOT NULL" + ",\n" +
                        REVENUE_DESCRIPTION_KEY + " VARCHAR(255) NOT NULL" + ",\n" +
                        OWNER_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        " FOREIGN KEY (" + OWNER_KEY + ") REFERENCES " + USERS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
                    + ");"
                )

        protected const val CREATE_REVENUE_LABELS_TABLE: String = ("CREATE TABLE IF NOT EXISTS " + RevenueLabel.REVENUE_LABELS_KEY +
                    " (\n" +
                        IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                        REVENUE_LABEL_COLOR_KEY + " VARCHAR(7) NOT NULL" + ",\n" +
                        REVENUE_LABEL_TEXT_KEY + " TEXT NOT NULL" + ",\n" +
                        REVENUE_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        " FOREIGN KEY (" + REVENUE_KEY + ") REFERENCES " + GENERAL_REVENUES_KEY + "(" + IDENTIFIER_KEY + ") " +
                        "ON DELETE CASCADE"
                    + ");"
                )

        private const val LIST_REVENUES_QUERY = "SELECT * FROM %s WHERE $OWNER_KEY='?'"

        protected val LIST_PROJECT_REVENUES_QUERY: String = String.format(LIST_REVENUES_QUERY, PROJECT_REVENUES_KEY)

        protected val LIST_GENERAL_REVENUES_QUERY: String = String.format(LIST_REVENUES_QUERY, GENERAL_REVENUES_KEY)

        protected const val GET_REVENUE_LABELS_QUERY: String = "SELECT * FROM " + RevenueLabel.REVENUE_LABELS_KEY +
                " WHERE " + REVENUE_KEY + "='?'"

        protected const val CREATE_PROJECT_REVENUE_QUERY: String = ("INSERT INTO " + PROJECT_REVENUES_KEY +
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
                    + ")"
                )

        protected const val ATTACH_INITIAL_REVENUE_QUERY: String = (
                "INSERT INTO " + InitialRevenue.INITIAL_REVENUES_KEY +
                    " (" +
                        IDENTIFIER_KEY + "," +
                        REVENUE_DATE_KEY + "," +
                        REVENUE_VALUE_KEY + "," +
                        OWNER_KEY + "," +
                    ProjectRevenue.PROJECT_REVENUE_KEY +
                    " ) VALUES (" +
                        "?" + "," +
                        "?" + "," +
                        "?" + "," +
                        "?" + "," +
                        "?"
                    + ")"
                )

        protected const val CREATE_GENERAL_REVENUE_QUERY: String = (
                "INSERT INTO " + GENERAL_REVENUES_KEY +
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
                    + ")"
                )

        protected const val ATTACH_LABEL_TO_REVENUE_QUERY: String = (
                "INSERT INTO " + RevenueLabel.REVENUE_LABELS_KEY +
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
                    + ")"
                )

        protected const val GET_PROJECT_REVENUE_QUERY: String =
            ("SELECT * FROM " + PROJECT_REVENUES_KEY + " WHERE "
                    + IDENTIFIER_KEY + "='?'" + " AND " + OWNER_KEY + "='?'")

        private const val GET_PROJECT_REVENUE_EXTRA_INFO_QUERY = ("SELECT * FROM %s" + " WHERE " + ProjectRevenue.PROJECT_REVENUE_KEY + "='?'")

        protected val GET_INITIAL_REVENUE_QUERY: String = String.format(GET_PROJECT_REVENUE_EXTRA_INFO_QUERY, InitialRevenue.INITIAL_REVENUES_KEY)

        protected val GET_TICKETS_QUERY: String = String.format(GET_PROJECT_REVENUE_EXTRA_INFO_QUERY, TICKET_REVENUES_KEY)

        protected const val ATTACH_TICKET_TO_PROJECT_QUERY: String = (
                "INSERT INTO " + TICKET_REVENUES_KEY +
                        " (" +
                            IDENTIFIER_KEY + "," +
                            REVENUE_DATE_KEY + "," +
                            REVENUE_TITLE_KEY + "," +
                            REVENUE_VALUE_KEY + "," +
                            REVENUE_DESCRIPTION_KEY + "," +
                            CLOSING_DATE_KEY + "," +
                            OWNER_KEY + "," +
                        ProjectRevenue.PROJECT_REVENUE_KEY +
                            " ) VALUES (" +
                            "?" + "," +
                            "?" + "," +
                            "?" + "," +
                            "?" + "," +
                            "?" + "," +
                            "?" + "," +
                            "?" + "," +
                            "?"
                        + ")"
                )

        protected const val DELETE_REVENUE_QUERY: String = "DELETE FROM %s WHERE $IDENTIFIER_KEY='?'"
        
    }
    
    fun list(
        userId: String,
        userToken: String,
    ): JSONArray?

    fun createProjectRevenue(
        title: String,
        value: Double,
        revenueDate: Long,
    )

    fun createGeneralRevenue(
        title: String,
        description: String,
        value: Double,
        revenueDate: Long,
        labels: List<RevenueLabel?>,
    )

    fun getProjectRevenue(
        revenue: ProjectRevenue
    ): ProjectRevenue {
        return ProjectRevenue(getProjectRevenue(revenue.id))
    }

    fun getProjectRevenue(
        revenueId: String
    ): JSONObject

    fun addTicketToProjectRevenue(
        revenue: ProjectRevenue,
        ticketTitle: String,
        ticketValue: Double,
        ticketDescription: String,
        openingDate: Long
    ) {
        addTicketToProjectRevenue(
            revenueId = revenue.id,
            ticketTitle = ticketTitle,
            ticketValue = ticketValue,
            ticketDescription = ticketDescription,
            openingDate = openingDate
        )
    }

    fun addTicketToProjectRevenue(
        revenueId: String,
        ticketTitle: String,
        ticketValue: Double,
        ticketDescription: String,
        openingDate: Long
    ) {
        addTicketToProjectRevenue(
            revenueId = revenueId,
            ticketTitle = ticketTitle,
            ticketValue = ticketValue,
            ticketDescription = ticketDescription,
            openingDate = openingDate,
            closingDate = -1
        )
    }

    fun addTicketToProjectRevenue(
        revenue: ProjectRevenue,
        ticketTitle: String,
        ticketValue: Double,
        ticketDescription: String,
        openingDate: Long,
        closingDate: Long
    ) {
        addTicketToProjectRevenue(
            revenueId = revenue.id,
            ticketTitle = ticketTitle,
            ticketValue = ticketValue,
            ticketDescription = ticketDescription,
            openingDate = openingDate,
            closingDate = closingDate
        )
    }

    fun addTicketToProjectRevenue(
        revenueId: String,
        ticketTitle: String,
        ticketValue: Double,
        ticketDescription: String,
        openingDate: Long,
        closingDate: Long
    )

    fun closeProjectRevenueTicket(
        revenue: ProjectRevenue,
        ticket: TicketRevenue
    ) {
        closeProjectRevenueTicket(
            revenueId = revenue.id,
            ticketId = ticket.id
        )
    }

    fun closeProjectRevenueTicket(
        revenueId: String,
        ticketId: String
    )

    fun deleteProjectRevenueTicket(
        revenue: ProjectRevenue,
        ticket: TicketRevenue
    ) {
        deleteProjectRevenueTicket(
            revenueId = revenue.id,
            ticketId = ticket.id
        )
    }

    fun deleteProjectRevenueTicket(
        revenueId: String,
        ticketId: String
    )

    fun deleteProjectRevenue(
        revenue: ProjectRevenue
    ) {
        deleteProjectRevenue(
            revenueId = revenue.id
        )
    }

    fun deleteProjectRevenue(
        revenueId: String
    ) {
        deleteRevenue(
            table = PROJECT_REVENUES_KEY,
            revenueId = revenueId
        )
    }

    fun deleteGeneralRevenue(
        revenue: GeneralRevenue
    ) {
        deleteGeneralRevenue(
            revenueId = revenue.id
        )
    }

    fun deleteGeneralRevenue(
        revenueId: String
    ) {
        deleteRevenue(
            table = GENERAL_REVENUES_KEY,
            revenueId = revenueId
        )
    }

    fun deleteTicket(
        ticket: TicketRevenue
    ) {
        deleteTicket(
            revenueId = ticket.id
        )
    }

    fun deleteTicket(
        revenueId: String
    ) {
        deleteRevenue(
            table = TICKET_REVENUES_KEY,
            revenueId = revenueId
        )
    }

    //String.format("Ciao %s a", "c")
    fun deleteRevenue(
        table: String,
        revenueId: String
    )
    
}
