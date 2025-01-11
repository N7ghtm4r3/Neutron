package com.tecknobit.neutron.revenues.controller;


import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinoxbackend.environment.services.DefaultEquinoxController;
import com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController;
import com.tecknobit.neutron.revenues.entities.ProjectRevenue;
import com.tecknobit.neutron.revenues.entities.RevenueLabel;
import com.tecknobit.neutron.revenues.entities.TicketRevenue;
import com.tecknobit.neutron.revenues.service.RevenuesService;
import com.tecknobit.neutron.users.entity.NeutronUser;
import com.tecknobit.neutron.users.repository.NeutronUsersRepository;
import com.tecknobit.neutron.users.service.NeutronUsersService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.SUCCESSFUL;
import static com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinoxcore.network.Requester.RESPONSE_DATA_KEY;
import static com.tecknobit.equinoxcore.network.Requester.RESPONSE_STATUS_KEY;
import static com.tecknobit.neutron.revenues.entities.GeneralRevenue.REVENUE_DESCRIPTION_KEY;
import static com.tecknobit.neutron.revenues.entities.GeneralRevenue.REVENUE_LABELS_KEY;
import static com.tecknobit.neutron.revenues.entities.ProjectRevenue.IS_PROJECT_REVENUE_KEY;
import static com.tecknobit.neutron.revenues.entities.ProjectRevenue.PROJECTS_KEY;
import static com.tecknobit.neutron.revenues.entities.Revenue.*;
import static com.tecknobit.neutron.revenues.entities.TicketRevenue.CLOSING_DATE_KEY;
import static com.tecknobit.neutron.revenues.entities.TicketRevenue.TICKET_IDENTIFIER_KEY;
import static com.tecknobit.neutron.users.entity.NeutronUser.CURRENCY_KEY;
import static com.tecknobit.neutroncore.helpers.NeutronEndpoints.TICKETS_ENDPOINT;
import static com.tecknobit.neutroncore.helpers.NeutronInputsValidator.INSTANCE;
import static com.tecknobit.neutroncore.helpers.NeutronInputsValidator.MAX_REVENUE_LABELS_NUMBER;

/**
 * The {@code RevenuesController} class is useful to manage all the operations on the user revenues
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see DefaultEquinoxController
 */
@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + IDENTIFIER_KEY + "}/" + REVENUES_KEY)
public class RevenuesController extends EquinoxController<NeutronUser, NeutronUsersRepository, NeutronUsersService> {

    /**
     * {@code revenuesService} helper to manage the revenues database operations
     */
    @Autowired
    private RevenuesService revenuesService;

    /**
     * Method to get the list of the user revenues</b>
     *
     * @param userId The identifier of the user
     * @param token The token of the user
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
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        HashMap<String, T> response = new HashMap<>();
        response.put(CURRENCY_KEY, (T) me.getCurrency().getIsoName());
        response.put(PROFILE_PIC_KEY, (T) me.getProfilePic());
        response.put(RESPONSE_DATA_KEY, (T) revenuesService.getRevenues(userId));
        response.put(RESPONSE_STATUS_KEY, (T) SUCCESSFUL);
        return (T) response;
    }

    /**
     * Method to create a new revenue
     *
     * @param userId The identifier of the user
     * @param token The token of the user
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
    public String createRevenue(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, Object> payload
    ) {
        if(!isMe(userId, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        double revenueValue = jsonHelper.getDouble(REVENUE_VALUE_KEY, 0);
        String revenueTitle = jsonHelper.getString(REVENUE_TITLE_KEY);
        long insertionDate = jsonHelper.getLong(REVENUE_DATE_KEY, 0);
        if(!INSTANCE.isRevenueValueValid(revenueValue) || !INSTANCE.isRevenueTitleValid(revenueTitle)
                || revenuesService.revenueExists(userId, revenueTitle))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        String identifier = generateIdentifier();
        if(jsonHelper.getBoolean(IS_PROJECT_REVENUE_KEY)) {
            revenuesService.createProjectRevenue(identifier, revenueValue, revenueTitle, insertionDate, userId);
            return successResponse();
        } else {
            String revenueDescription = jsonHelper.getString(REVENUE_DESCRIPTION_KEY);
            if(!INSTANCE.isRevenueDescriptionValid(revenueDescription))
                return failedResponse(WRONG_PROCEDURE_MESSAGE);
            ArrayList<RevenueLabel> labels = new ArrayList<>();
            try {
                JSONArray jLabels = jsonHelper.getJSONArray(REVENUE_LABELS_KEY, new JSONArray());
                for(int j = 0; j < jLabels.length() && j < MAX_REVENUE_LABELS_NUMBER; j++)
                    labels.add(new RevenueLabel(jLabels.getJSONObject(j)));
                revenuesService.createGeneralRevenue(identifier, revenueValue, revenueTitle, insertionDate,
                        revenueDescription, labels, userId);
                return successResponse();
            } catch (IllegalArgumentException e) {
                return failedResponse(WRONG_PROCEDURE_MESSAGE);
            }
        }
    }

    /**
     * Method to get a project revenue
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param revenueId The identifier of the project to get
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
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        ProjectRevenue projectRevenue = revenuesService.getProjectRevenue(userId, revenueId);
        if(projectRevenue == null)
            return (T) failedResponse(WRONG_PROCEDURE_MESSAGE);
        return (T) successResponse(projectRevenue);
    }

    /**
     * Method to add a new ticket to a project
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param revenueId The identifier of the project
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
        if(!isMe(userId, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        ProjectRevenue projectRevenue = revenuesService.getProjectRevenue(userId, revenueId);
        if (projectRevenue == null)
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        loadJsonHelper(payload);
        double ticketRevenue = jsonHelper.getDouble(REVENUE_VALUE_KEY);
        String ticketTitle = jsonHelper.getString(REVENUE_TITLE_KEY);
        String ticketDescription = jsonHelper.getString(REVENUE_DESCRIPTION_KEY);
        long openingTime = jsonHelper.getLong(REVENUE_DATE_KEY);
        long closingTime = jsonHelper.getLong(CLOSING_DATE_KEY, -1);
        if (!INSTANCE.isRevenueValueValid(ticketRevenue) || !INSTANCE.isRevenueTitleValid(ticketTitle)
                || !INSTANCE.isRevenueDescriptionValid(ticketDescription) || projectRevenue.hasTicket(ticketTitle)) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
        revenuesService.addTicketToProjectRevenue(generateIdentifier(), ticketRevenue, ticketTitle,
                ticketDescription, openingTime, closingTime, revenueId, userId);
        return successResponse();
    }

    /**
     * Method to close a ticket
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param revenueId The identifier of the project
     * @param ticketId The identifier of the ticket
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
        if(!isMe(userId, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        TicketRevenue ticketRevenue = revenuesService.getTicketRevenue(ticketId, userId, revenueId);
        if(ticketRevenue == null || ticketRevenue.isClosed())
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        revenuesService.closeTicketRevenue(ticketId, userId, revenueId);
        return successResponse();
    }

    /**
     * Method to delete a ticket
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param revenueId The identifier of the project
     * @param ticketId The identifier of the ticket
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
        if(!isMe(userId, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        TicketRevenue ticketRevenue = revenuesService.getTicketRevenue(ticketId, userId, revenueId);
        if(ticketRevenue == null)
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        revenuesService.deleteTicketRevenue(ticketId);
        return successResponse();
    }

    /**
     * Method to delete a revenue
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param revenueId The identifier of the project
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
        if(!isMe(userId, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        if(!revenuesService.deleteRevenue(userId, revenueId))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        return successResponse();
    }

}
