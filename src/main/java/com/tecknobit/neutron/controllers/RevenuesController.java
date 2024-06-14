package com.tecknobit.neutron.controllers;


import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.neutron.helpers.services.RevenuesHelper;
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue;
import com.tecknobit.neutroncore.records.revenues.RevenueLabel;
import com.tecknobit.neutroncore.records.revenues.TicketRevenue;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.neutroncore.helpers.Endpoints.*;
import static com.tecknobit.neutroncore.helpers.InputValidator.*;
import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.TOKEN_KEY;
import static com.tecknobit.neutroncore.records.User.USERS_KEY;
import static com.tecknobit.neutroncore.records.revenues.GeneralRevenue.REVENUE_DESCRIPTION_KEY;
import static com.tecknobit.neutroncore.records.revenues.GeneralRevenue.REVENUE_LABELS_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.IS_PROJECT_REVENUE_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECTS_KEY;
import static com.tecknobit.neutroncore.records.revenues.Revenue.*;
import static com.tecknobit.neutroncore.records.revenues.TicketRevenue.CLOSING_DATE_KEY;
import static com.tecknobit.neutroncore.records.revenues.TicketRevenue.TICKET_IDENTIFIER_KEY;

@RestController
@RequestMapping(BASE_ENDPOINT + USERS_KEY + "/{" + IDENTIFIER_KEY + "}/" + REVENUES_KEY)
public class RevenuesController extends NeutronController {

    private final RevenuesHelper revenuesHelper;

    public RevenuesController(RevenuesHelper revenuesHelper) {
        this.revenuesHelper = revenuesHelper;
    }

    @GetMapping(
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/revenues", method = GET)
    public <T> T list(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(isMe(userId, token))
            return (T) successResponse(revenuesHelper.getRevenues(userId));
        else
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @PostMapping(
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/revenues", method = POST)
    public <T> String createRevenue(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, T> payload
    ) {
        if(isMe(userId, token)) {
            loadJsonHelper(payload);
            double revenueValue = jsonHelper.getDouble(REVENUE_VALUE_KEY, 0);
            String revenueTitle = jsonHelper.getString(REVENUE_TITLE_KEY);
            long insertionDate = jsonHelper.getLong(REVENUE_DATE_KEY, 0);
            if(!isRevenueValueValid(revenueValue) || !isRevenueTitleValid(revenueTitle)
                    || revenuesHelper.revenueExists(userId, revenueTitle))
                return failedResponse(WRONG_PROCEDURE_MESSAGE);
            String identifier = generateIdentifier();
            if(jsonHelper.getBoolean(IS_PROJECT_REVENUE_KEY)) {
                revenuesHelper.createProjectRevenue(identifier, revenueValue, revenueTitle, insertionDate, userId);
                return successResponse();
            } else {
                String revenueDescription = jsonHelper.getString(REVENUE_DESCRIPTION_KEY);
                if(!isRevenueDescriptionValid(revenueDescription))
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                ArrayList<RevenueLabel> labels = new ArrayList<>();
                try {
                    JSONArray jLabels = jsonHelper.getJSONArray(REVENUE_LABELS_KEY, new JSONArray());
                    for(int j = 0; j < jLabels.length() && j < MAX_REVENUE_LABELS_NUMBER; j++)
                        labels.add(new RevenueLabel(jLabels.getJSONObject(j)));
                    revenuesHelper.createGeneralRevenue(identifier, revenueValue, revenueTitle, insertionDate,
                            revenueDescription, labels, userId);
                    return successResponse();
                } catch (IllegalArgumentException e) {
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                }
            }
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @GetMapping(
            path = PROJECTS_KEY + "{" + REVENUE_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}", method = GET)
    public <T> T getProjectRevenue(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @PathVariable(REVENUE_IDENTIFIER_KEY) String revenueId,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(isMe(userId, token)) {
            ProjectRevenue projectRevenue = revenuesHelper.getProjectRevenue(userId, revenueId);
            if(projectRevenue != null)
                return (T) successResponse(projectRevenue);
            else
                return (T) failedResponse(WRONG_PROCEDURE_MESSAGE);
        } else
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @PostMapping(
            path = PROJECTS_KEY + "{" + REVENUE_IDENTIFIER_KEY + "}" + TICKETS_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}/tickets", method = POST)
    public <T> String addTicketToProjectRevenue(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @PathVariable(REVENUE_IDENTIFIER_KEY) String revenueId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, T> payload
    ) {
        if (isMe(userId, token)) {
            ProjectRevenue projectRevenue = revenuesHelper.getProjectRevenue(userId, revenueId);
            if (projectRevenue != null) {
                loadJsonHelper(payload);
                double ticketRevenue = jsonHelper.getDouble(REVENUE_VALUE_KEY);
                String ticketTitle = jsonHelper.getString(REVENUE_TITLE_KEY);
                String ticketDescription = jsonHelper.getString(REVENUE_DESCRIPTION_KEY);
                long openingTime = jsonHelper.getLong(REVENUE_DATE_KEY);
                long closingTime = jsonHelper.getLong(CLOSING_DATE_KEY, -1);
                if (!isRevenueValueValid(ticketRevenue) || !isRevenueTitleValid(ticketTitle)
                        || !isRevenueDescriptionValid(ticketDescription) || projectRevenue.hasTicket(ticketTitle)) {
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                }
                revenuesHelper.addTicketToProjectRevenue(generateIdentifier(), ticketRevenue, ticketTitle,
                        ticketDescription, openingTime, closingTime, revenueId, userId);
                return successResponse();
            } else
                return failedResponse(WRONG_PROCEDURE_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @PatchMapping(
            path = PROJECTS_KEY + "{" + REVENUE_IDENTIFIER_KEY + "}" + TICKETS_ENDPOINT
                    + "/{" + TICKET_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}/tickets/{ticket_id}", method = PATCH)
    public String closeProjectRevenueTicket(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @PathVariable(REVENUE_IDENTIFIER_KEY) String revenueId,
            @PathVariable(TICKET_IDENTIFIER_KEY) String ticketId,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if (isMe(userId, token)) {
            TicketRevenue ticketRevenue = revenuesHelper.getTicketRevenue(ticketId, userId, revenueId);
            if(ticketRevenue != null && !ticketRevenue.isClosed()) {
                revenuesHelper.closeTicketRevenue(ticketId, userId, revenueId);
                return successResponse();
            } else
                return failedResponse(WRONG_PROCEDURE_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @DeleteMapping(
            path = PROJECTS_KEY + "{" + REVENUE_IDENTIFIER_KEY + "}" + TICKETS_ENDPOINT
                    + "/{" + TICKET_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}/tickets/{ticket_id}", method = DELETE)
    public String deleteProjectRevenueTicket(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @PathVariable(REVENUE_IDENTIFIER_KEY) String revenueId,
            @PathVariable(TICKET_IDENTIFIER_KEY) String ticketId,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if (isMe(userId, token)) {
            TicketRevenue ticketRevenue = revenuesHelper.getTicketRevenue(ticketId, userId, revenueId);
            if(ticketRevenue != null) {
                revenuesHelper.deleteTicketRevenue(ticketId);
                return successResponse();
            } else
                return failedResponse(WRONG_PROCEDURE_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    @DeleteMapping(
            path = "/{" + REVENUE_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/revenues/{revenue_id}", method = DELETE)
    public String deleteRevenue(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @PathVariable(REVENUE_IDENTIFIER_KEY) String revenueId,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(isMe(userId, token)) {
            if(revenuesHelper.deleteRevenue(userId, revenueId))
                return successResponse();
            else
                return failedResponse(WRONG_PROCEDURE_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

}
