package com.tecknobit.neutroncore.helpers.local

import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY
import com.tecknobit.neutroncore.records.User.OWNER_KEY
import com.tecknobit.neutroncore.records.User.USERS_KEY
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue
import com.tecknobit.neutroncore.records.revenues.InitialRevenue.INITIAL_REVENUES_KEY
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECT_REVENUES_KEY
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECT_REVENUE_KEY
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import com.tecknobit.neutroncore.records.revenues.RevenueLabel.REVENUE_LABEL_COLOR_KEY
import com.tecknobit.neutroncore.records.revenues.RevenueLabel.REVENUE_LABEL_TEXT_KEY
import com.tecknobit.neutroncore.records.revenues.TicketRevenue
import com.tecknobit.neutroncore.records.revenues.TicketRevenue.*
import org.json.JSONObject

interface LRevenuesController : LNeutronController {
    
    companion object {

        const val CREATE_PROJECT_REVENUES_TABLE: String = (
                "CREATE TABLE IF NOT EXISTS " + PROJECT_REVENUES_KEY +
                    " (\n" +
                        IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                        REVENUE_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                        REVENUE_TITLE_KEY + " VARCHAR(30) NOT NULL" + ",\n" +
                        OWNER_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        " FOREIGN KEY (" + OWNER_KEY + ") REFERENCES " + USERS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE"
                    + ");"
                )

        const val CREATE_INITIAL_REVENUES_TABLE: String = (
                "CREATE TABLE IF NOT EXISTS " + INITIAL_REVENUES_KEY +
                    " (\n" +
                        IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                        REVENUE_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                        REVENUE_TITLE_KEY + " VARCHAR(30) NOT NULL" + ",\n" +
                        REVENUE_VALUE_KEY + " REAL NOT NULL" + ",\n" +
                        OWNER_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        PROJECT_REVENUE_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        " FOREIGN KEY (" + OWNER_KEY + ") REFERENCES " + USERS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE " +
                        " FOREIGN KEY (" + PROJECT_REVENUE_KEY + ") REFERENCES " + PROJECT_REVENUES_KEY + "(" + IDENTIFIER_KEY + ") " +
                        "ON DELETE CASCADE"
                    + ");"
                )

        const val CREATE_TICKET_REVENUES_TABLE: String = (
                "CREATE TABLE IF NOT EXISTS " + TICKET_REVENUES_KEY +
                    " (\n" +
                        IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                        REVENUE_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                        REVENUE_TITLE_KEY + " VARCHAR(30) NOT NULL" + ",\n" +
                        REVENUE_VALUE_KEY + " REAL NOT NULL" + ",\n" +
                        REVENUE_DESCRIPTION_KEY + " VARCHAR(255) NOT NULL" + ",\n" +
                        CLOSING_DATE_KEY + " BIGINT NOT NULL" + ",\n" +
                        OWNER_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        PROJECT_REVENUE_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        " FOREIGN KEY (" + OWNER_KEY + ") REFERENCES " + USERS_KEY + "(" + IDENTIFIER_KEY + ") ON DELETE CASCADE " +
                        " FOREIGN KEY (" + PROJECT_REVENUE_KEY + ") REFERENCES " + PROJECT_REVENUES_KEY + "(" + IDENTIFIER_KEY + ") " +
                        "ON DELETE CASCADE"
                    + ");"
                )

        const val CREATE_GENERAL_REVENUES_TABLE: String = (
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

        const val CREATE_REVENUE_LABELS_TABLE: String = (
                "CREATE TABLE IF NOT EXISTS " + RevenueLabel.REVENUE_LABELS_KEY +
                    " (\n" +
                        IDENTIFIER_KEY + " VARCHAR(32) PRIMARY KEY" + ",\n" +
                        REVENUE_LABEL_COLOR_KEY + " VARCHAR(7) NOT NULL" + ",\n" +
                        REVENUE_LABEL_TEXT_KEY + " TEXT NOT NULL" + ",\n" +
                        REVENUE_KEY + " VARCHAR(32) NOT NULL" + ",\n" +
                        " FOREIGN KEY (" + REVENUE_KEY + ") REFERENCES " + GENERAL_REVENUES_KEY + "(" + IDENTIFIER_KEY + ") " +
                        "ON DELETE CASCADE"
                    + ");"
                )

        private const val LIST_REVENUES_QUERY = ""

        const val LIST_PROJECT_REVENUES_QUERY: String = "SELECT * FROM $PROJECT_REVENUES_KEY " +
                "INNER JOIN $INITIAL_REVENUES_KEY ON $PROJECT_REVENUES_KEY.$IDENTIFIER_KEY = " +
                "$INITIAL_REVENUES_KEY.$PROJECT_REVENUE_KEY " +
                "INNER JOIN $TICKET_REVENUES_KEY ON $PROJECT_REVENUES_KEY.$IDENTIFIER_KEY = " +
                "$TICKET_REVENUES_KEY.$PROJECT_REVENUE_KEY " +
                " WHERE $PROJECT_REVENUES_KEY.$OWNER_KEY=? "

        val LIST_GENERAL_REVENUES_QUERY: String = String.format(LIST_REVENUES_QUERY, GENERAL_REVENUES_KEY)

        const val GET_REVENUE_LABELS_QUERY: String = "SELECT * FROM " + RevenueLabel.REVENUE_LABELS_KEY +
                " WHERE " + REVENUE_KEY + "=?"

        const val CREATE_PROJECT_REVENUE_QUERY: String = (
                "INSERT INTO " + PROJECT_REVENUES_KEY +
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

        const val ATTACH_INITIAL_REVENUE_QUERY: String = (
                "INSERT INTO " + INITIAL_REVENUES_KEY +
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
                    + ")"
                )

        const val CREATE_GENERAL_REVENUE_QUERY: String = (
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

        const val ATTACH_LABEL_TO_REVENUE_QUERY: String = (
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

        const val GET_PROJECT_REVENUE_QUERY: String = ("SELECT * FROM $PROJECT_REVENUES_KEY WHERE $IDENTIFIER_KEY=? AND $OWNER_KEY=?")

        private const val GET_PROJECT_REVENUE_EXTRA_INFO_QUERY = ("SELECT * FROM %s WHERE $PROJECT_REVENUE_KEY=?")

        val GET_INITIAL_REVENUE_QUERY: String = String.format(GET_PROJECT_REVENUE_EXTRA_INFO_QUERY, INITIAL_REVENUES_KEY)

        val GET_TICKETS_QUERY: String = String.format(GET_PROJECT_REVENUE_EXTRA_INFO_QUERY, TICKET_REVENUES_KEY)

        const val ATTACH_TICKET_TO_PROJECT_QUERY: String = (
                "INSERT INTO " + TICKET_REVENUES_KEY +
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
                        + ")"
                )

        const val DELETE_REVENUE_QUERY: String = "DELETE FROM %s WHERE $IDENTIFIER_KEY=?"
        
    }
    
    fun listRevenues(
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    )

    fun createProjectRevenue(
        title: String,
        value: Double,
        revenueDate: Long,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    )

    fun createGeneralRevenue(
        title: String,
        description: String,
        value: Double,
        revenueDate: Long,
        labels: List<RevenueLabel>,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    )

    fun getProjectRevenue(
        revenue: ProjectRevenue,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ): ProjectRevenue {
        return ProjectRevenue(
            getProjectRevenue(
                revenueId = revenue.id,
                onSuccess = onSuccess,
                onFailure = onFailure
            )
        )
    }

    fun getProjectRevenue(
        revenueId: String,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ): JSONObject

    fun addTicketToProjectRevenue(
        revenue: ProjectRevenue,
        ticketTitle: String,
        ticketValue: Double,
        ticketDescription: String,
        openingDate: Long,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        addTicketToProjectRevenue(
            revenueId = revenue.id,
            ticketTitle = ticketTitle,
            ticketValue = ticketValue,
            ticketDescription = ticketDescription,
            openingDate = openingDate,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun addTicketToProjectRevenue(
        revenueId: String,
        ticketTitle: String,
        ticketValue: Double,
        ticketDescription: String,
        openingDate: Long,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        addTicketToProjectRevenue(
            revenueId = revenueId,
            ticketTitle = ticketTitle,
            ticketValue = ticketValue,
            ticketDescription = ticketDescription,
            openingDate = openingDate,
            closingDate = -1,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun addTicketToProjectRevenue(
        revenue: ProjectRevenue,
        ticketTitle: String,
        ticketValue: Double,
        ticketDescription: String,
        openingDate: Long,
        closingDate: Long,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        addTicketToProjectRevenue(
            revenueId = revenue.id,
            ticketTitle = ticketTitle,
            ticketValue = ticketValue,
            ticketDescription = ticketDescription,
            openingDate = openingDate,
            closingDate = closingDate,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun addTicketToProjectRevenue(
        revenueId: String,
        ticketTitle: String,
        ticketValue: Double,
        ticketDescription: String,
        openingDate: Long,
        closingDate: Long,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    )

    fun closeProjectRevenueTicket(
        revenue: ProjectRevenue,
        ticket: TicketRevenue,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        closeProjectRevenueTicket(
            revenueId = revenue.id,
            ticketId = ticket.id,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun closeProjectRevenueTicket(
        revenueId: String,
        ticketId: String,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    )

    fun deleteProjectRevenueTicket(
        revenue: ProjectRevenue,
        ticket: TicketRevenue,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        deleteProjectRevenueTicket(
            revenueId = revenue.id,
            ticketId = ticket.id,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun deleteProjectRevenueTicket(
        revenueId: String,
        ticketId: String,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    )

    fun deleteProjectRevenue(
        revenue: ProjectRevenue,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        deleteProjectRevenue(
            revenueId = revenue.id,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun deleteProjectRevenue(
        revenueId: String,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        deleteRevenue(
            table = PROJECT_REVENUES_KEY,
            revenueId = revenueId,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun deleteGeneralRevenue(
        revenue: GeneralRevenue,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        deleteGeneralRevenue(
            revenueId = revenue.id,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun deleteGeneralRevenue(
        revenueId: String,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        deleteRevenue(
            table = GENERAL_REVENUES_KEY,
            revenueId = revenueId,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun deleteTicket(
        ticket: TicketRevenue,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        deleteTicket(
            revenueId = ticket.id,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    fun deleteTicket(
        revenueId: String,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    ) {
        deleteRevenue(
            table = TICKET_REVENUES_KEY,
            revenueId = revenueId,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    //String.format("Ciao %s a", "c")
    fun deleteRevenue(
        table: String,
        revenueId: String,
        onSuccess: (JsonHelper) -> Unit,
        onFailure: (JsonHelper) -> Unit
    )

}
