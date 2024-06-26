package com.tecknobit.neutron.controllers;


import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.neutron.helpers.services.RevenuesHelper;
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue;
import com.tecknobit.neutroncore.records.revenues.RevenueLabel;
import com.tecknobit.neutroncore.records.revenues.TicketRevenue;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.SUCCESSFUL;
import static com.tecknobit.equinox.Requester.RESPONSE_MESSAGE_KEY;
import static com.tecknobit.equinox.Requester.RESPONSE_STATUS_KEY;
import static com.tecknobit.neutroncore.helpers.Endpoints.BASE_ENDPOINT;
import static com.tecknobit.neutroncore.helpers.Endpoints.TICKETS_ENDPOINT;
import static com.tecknobit.neutroncore.helpers.InputValidator.*;
import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.*;
import static com.tecknobit.neutroncore.records.revenues.GeneralRevenue.REVENUE_DESCRIPTION_KEY;
import static com.tecknobit.neutroncore.records.revenues.GeneralRevenue.REVENUE_LABELS_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.IS_PROJECT_REVENUE_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECTS_KEY;
import static com.tecknobit.neutroncore.records.revenues.Revenue.*;
import static com.tecknobit.neutroncore.records.revenues.TicketRevenue.CLOSING_DATE_KEY;
import static com.tecknobit.neutroncore.records.revenues.TicketRevenue.TICKET_IDENTIFIER_KEY;

/**
 * The {@code RevenuesController} class is useful to manage all the operations on the user revenues
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see NeutronController
 */
@RestController
@RequestMapping(BASE_ENDPOINT + USERS_KEY + "/{" + IDENTIFIER_KEY + "}/" + REVENUES_KEY)
public class RevenuesController extends NeutronController {

    /**
     * {@code revenuesHelper} helper to manage the revenues database operations
     */
    private final RevenuesHelper revenuesHelper;

    /**
     * Constructor to init the {@link RevenuesController} controller
     *
     * @param revenuesHelper: helper to manage the revenues database operations
     */
    public RevenuesController(RevenuesHelper revenuesHelper) {
        this.revenuesHelper = revenuesHelper;
    }

    /**
     * Method to get the list of the user revenues</b>
     *
     * @param userId: the identifier of the user
     * @param token: the token of the user
     *
     * @return the result of the request as {@link String}
     */
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
        if(isMe(userId, token)) {
            HashMap<String, T> response = new HashMap<>();
            response.put(CURRENCY_KEY, (T) me.getCurrency().name());
            response.put(PROFILE_PIC_KEY, (T) me.getProfilePic());
            response.put(RESPONSE_MESSAGE_KEY, (T) revenuesHelper.getRevenues(userId));
            response.put(RESPONSE_STATUS_KEY, (T) SUCCESSFUL);
            return (T) response;
        } else
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    /**
     * Method to create a new revenue
     *
     * @param userId: the identifier of the user
     * @param token: the token of the user
     * @param payload: payload of the request
     * <pre>
     *      {@code
     *              {
     *                  "is_project_revenue": "whether the revenue is a project", -> [boolean]
     *                  "value": "the amount value of the revenue", -> [double]
     *                  "title": "the title of the revenue", -> [String]
     *                  "revenue_date": "the insertion date of the revenue", -> [long]
     *                  ------- if "is_project_revenue" == false ------
     *                  "description": "the description of the revenue", -> [String]
     *                   "labels": "the labels attached to the revenue" -> [array of RevenueLabel]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
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

    /**
     * Method to get a project revenue
     *
     * @param userId: the identifier of the user
     * @param token: the token of the user
     * @param revenueId: the identifier of the project to get
     *
     * @return the result of the request as {@link String}
     */
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

    /**
     * Method to add a new ticket to a project
     *
     * @param userId: the identifier of the user
     * @param token: the token of the user
     * @param revenueId: the identifier of the project
     * @param payload: payload of the request
     * <pre>
     *      {@code
     *              {
     *                  "value": "the amount value of the ticket", -> [double]
     *                  "title": "the title of the ticket", -> [String]
     *                  "description": "the description of the ticket", -> [String]
     *                  "revenue_date": "the insertion date of the ticket", -> [long]
     *                  "closing_date": "the closing date of the ticket" -> [long, default -1]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
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

    /**
     * Method to close a ticket
     *
     * @param userId: the identifier of the user
     * @param token: the token of the user
     * @param revenueId: the identifier of the project
     * @param ticketId: the identifier of the ticket
     *
     * @return the result of the request as {@link String}
     */
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

    /**
     * Method to delete a ticket
     *
     * @param userId: the identifier of the user
     * @param token: the token of the user
     * @param revenueId: the identifier of the project
     * @param ticketId: the identifier of the ticket
     *
     * @return the result of the request as {@link String}
     */
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

    /**
     * Method to delete a revenue
     *
     * @param userId: the identifier of the user
     * @param token: the token of the user
     * @param revenueId: the identifier of the project
     *
     * @return the result of the request as {@link String}
     */
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
