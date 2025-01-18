package com.tecknobit.neutron.services.revenues.controller;


import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinoxbackend.environment.services.DefaultEquinoxController;
import com.tecknobit.neutron.services.DefaultNeutronController;
import com.tecknobit.neutron.services.revenues.entities.ProjectRevenue;
import com.tecknobit.neutron.services.revenues.entities.RevenueLabel;
import com.tecknobit.neutron.services.revenues.entities.TicketRevenue;
import com.tecknobit.neutron.services.revenues.service.RevenuesService;
import com.tecknobit.neutroncore.enums.RevenuePeriod;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*;
import static com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.TOKEN_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.equinoxcore.pagination.PaginatedResponse.*;
import static com.tecknobit.neutron.services.revenues.entities.ProjectRevenue.PROJECTS_KEY;
import static com.tecknobit.neutroncore.ContantsKt.*;
import static com.tecknobit.neutroncore.helpers.NeutronEndpoints.PROJECT_BALANCE_ENDPOINT;
import static com.tecknobit.neutroncore.helpers.NeutronEndpoints.TICKETS_ENDPOINT;
import static com.tecknobit.neutroncore.helpers.NeutronInputsValidator.INSTANCE;
import static com.tecknobit.neutroncore.helpers.NeutronInputsValidator.MAX_REVENUE_LABELS_NUMBER;

/**
 * The {@code RevenuesController} class is useful to manage all the operations on the user revenues
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see DefaultEquinoxController
 * @see DefaultNeutronController
 */
@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + IDENTIFIER_KEY + "}/" + REVENUES_KEY)
public class RevenuesController extends DefaultNeutronController {

    /**
     * {@code revenuesService} helper to manage the revenues database operations
     */
    @Autowired
    private RevenuesService revenuesService;

    /**
     * Method to get the labels of the user
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     *
     * @return the result of the request as {@link String}
     */
    @GetMapping(
            path = {
                    REVENUE_LABELS_KEY
            },
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/revenues/revenue_labels", method = GET)
    public <T> T getRevenuesLabels(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(revenuesService.getRevenueLabels(userId));
    }

    /**
     * Method to get the list of the user revenues
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param period The period to use to select the revenues
     * @param retrieveGeneralRevenues Whether include the {@link com.tecknobit.neutron.services.revenues.entities.GeneralRevenue}
     * @param retrieveProjectRevenues Whether include the {@link com.tecknobit.neutron.services.revenues.entities.ProjectRevenue}
     * @param labels The labels used to filter the data
     *
     * @return the result of the request as {@link String}
     */
    @GetMapping(
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/revenues", method = GET)
    public <T> T getRevenues(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestParam(name = PAGE_KEY, defaultValue = DEFAULT_PAGE_HEADER_VALUE, required = false) int page,
            @RequestParam(name = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE_HEADER_VALUE, required = false) int pageSize,
            @RequestParam(name = REVENUE_PERIOD_KEY, defaultValue = "LAST_MONTH", required = false) RevenuePeriod period,
            @RequestParam(name = GENERAL_REVENUES_KEY, defaultValue = "true", required = false) boolean retrieveGeneralRevenues,
            @RequestParam(name = PROJECT_REVENUES_KEY, defaultValue = "true", required = false) boolean retrieveProjectRevenues,
            @RequestParam(name = REVENUE_LABELS_KEY, required = false) Set<String> labels
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        // TODO: 12/01/2025 REPLACE OR CREATE A DEDICATED REQUEST
        // response.put(CURRENCY_KEY, (T) me.getCurrency().getIsoName());
        // response.put(PROFILE_PIC_KEY, (T) me.getProfilePic());
        return (T) successResponse(revenuesService.getRevenues(userId, page, pageSize, period, retrieveGeneralRevenues,
                retrieveProjectRevenues, labels));
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
        if(invalidRevenuePayload(revenueValue, revenueTitle, insertionDate))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        String identifier = generateIdentifier();
        try {
            if(jsonHelper.getBoolean(IS_PROJECT_REVENUE_KEY))
                revenuesService.createProjectRevenue(identifier, revenueValue, revenueTitle, insertionDate, userId);
            else {
                String revenueDescription = jsonHelper.getString(REVENUE_DESCRIPTION_KEY);
                if(!INSTANCE.isRevenueDescriptionValid(revenueDescription))
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                ArrayList<RevenueLabel> labels = extractRevenueLabels();
                revenuesService.createGeneralRevenue(identifier, revenueValue, revenueTitle, insertionDate,
                        revenueDescription, labels, userId);
            }
        } catch (Exception e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
        return successResponse();
    }

    /**
     * Method to edit an existing revenue
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
    @PatchMapping(
            path = "/{" + REVENUE_IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/revenues/{revenue_id}", method = PATCH)
    public String editRevenue(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @PathVariable(REVENUE_IDENTIFIER_KEY) String revenueId,
            @RequestBody Map<String, Object> payload
    ) {
        if(!isMe(userId, token) || !revenuesService.revenueExistsById(userId, revenueId))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        double revenueValue = jsonHelper.getDouble(REVENUE_VALUE_KEY, 0);
        String revenueTitle = jsonHelper.getString(REVENUE_TITLE_KEY);
        long insertionDate = jsonHelper.getLong(REVENUE_DATE_KEY, 0);
        if(invalidRevenuePayload(revenueValue, revenueTitle, insertionDate))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        try {
            if(jsonHelper.getBoolean(IS_PROJECT_REVENUE_KEY)) {
                revenuesService.editProjectRevenue(revenueId, revenueValue, revenueTitle, insertionDate, userId);
            } else {
                String revenueDescription = jsonHelper.getString(REVENUE_DESCRIPTION_KEY);
                if(!INSTANCE.isRevenueDescriptionValid(revenueDescription))
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                ArrayList<RevenueLabel> labels = extractRevenueLabels();
                revenuesService.editGeneralRevenue(revenueId, revenueValue, revenueTitle, insertionDate,
                        revenueDescription, labels, userId);
            }
        } catch (Exception e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
        return successResponse();
    }

    /**
     * Method to check whether the payload of the revenue is valid
     *
     * @param revenueValue The value of the revenue
     * @param revenueTitle The title of the revenue
     * @param insertionDate The date when the revenue has been created
     * @return whether the payload is valid or not as {@code boolean}
     */
    private boolean invalidRevenuePayload(double revenueValue, String revenueTitle, long insertionDate) {
        return !INSTANCE.isRevenueValueValid(revenueValue) || !INSTANCE.isRevenueTitleValid(revenueTitle);
    }

    /**
     * Method to extract the labels from the payload
     *
     * @return the labels as {@link ArrayList} of {@link RevenueLabel}
     */
    private ArrayList<RevenueLabel> extractRevenueLabels() {
        ArrayList<RevenueLabel> labels = new ArrayList<>();
        JSONArray jLabels = jsonHelper.getJSONArray(REVENUE_LABELS_KEY, new JSONArray());
        for(int j = 0; j < jLabels.length() && j < MAX_REVENUE_LABELS_NUMBER; j++) {
            JSONObject jLabel = jLabels.getJSONObject(j);
            if(!jLabel.has(IDENTIFIER_KEY))
                jLabel.put(IDENTIFIER_KEY, generateIdentifier());
            labels.add(new RevenueLabel(jLabel));
        }
        return labels;
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
     * Method to get the balance of the project, this count just the closed ticket
     *
     * @param userId The identifier of the user
     * @param revenueId The project identifier
     * @param token The token of the user
     * @param period The period to use to select the tickets
     * @param retrieveClosedTickets Whether include the closed tickets
     *
     * @return the result of the request as {@link String}
     */
    @GetMapping(
            path = PROJECTS_KEY + "{" + REVENUE_IDENTIFIER_KEY + "}" + PROJECT_BALANCE_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}/balance", method = GET)
    public <T> T getProjectBalance(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @PathVariable(REVENUE_IDENTIFIER_KEY) String revenueId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestParam(name = REVENUE_PERIOD_KEY, defaultValue = "LAST_MONTH", required = false) RevenuePeriod period,
            @RequestParam(name = CLOSED_TICKETS_KEY, defaultValue = "true", required = false) boolean retrieveClosedTickets
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        ProjectRevenue project = revenuesService.getProjectRevenue(userId, revenueId);
        if(project == null)
            return (T) failedResponse(WRONG_PROCEDURE_MESSAGE);
        return (T) successResponse(revenuesService.getProjectBalance(project, period, retrieveClosedTickets));
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
    public String addTicketToProjectRevenue(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @PathVariable(REVENUE_IDENTIFIER_KEY) String revenueId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, Object> payload
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
        if (!INSTANCE.isRevenueValueValid(ticketRevenue) || !INSTANCE.isRevenueTitleValid(ticketTitle)
                || !INSTANCE.isRevenueDescriptionValid(ticketDescription) || projectRevenue.hasTicket(ticketTitle)) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
        revenuesService.addTicketToProjectRevenue(generateIdentifier(), ticketRevenue, ticketTitle, ticketDescription,
                openingTime, revenueId, userId);
        return successResponse();
    }

    /**
     * Method to get the tickets attached to the project
     *
     * @param userId The identifier of the user
     * @param revenueId The project identifier
     * @param token The token of the user
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param period The period to use to select the tickets
     * @param retrievePendingTickets Whether include the pending tickets
     * @param retrieveClosedTickets Whether include the closed tickets
     *
     * @return the result of the request as {@link String}
     */
    @GetMapping(
            path = PROJECTS_KEY + "{" + REVENUE_IDENTIFIER_KEY + "}" + TICKETS_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}/tickets", method = POST)
    public <T> T getTickets(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @PathVariable(REVENUE_IDENTIFIER_KEY) String revenueId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestParam(name = PAGE_KEY, defaultValue = DEFAULT_PAGE_HEADER_VALUE, required = false) int page,
            @RequestParam(name = PAGE_SIZE_KEY, defaultValue = DEFAULT_PAGE_SIZE_HEADER_VALUE, required = false) int pageSize,
            @RequestParam(name = REVENUE_PERIOD_KEY, defaultValue = "LAST_MONTH", required = false) RevenuePeriod period,
            @RequestParam(name = PENDING_TICKETS_KEY, defaultValue = "true", required = false) boolean retrievePendingTickets,
            @RequestParam(name = CLOSED_TICKETS_KEY, defaultValue = "true", required = false) boolean retrieveClosedTickets
    ) {
        if(!isMe(userId, token) || revenuesService.getProjectRevenue(userId, revenueId) == null)
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) revenuesService.getTickets(revenueId, page, pageSize, period, retrievePendingTickets,
                retrieveClosedTickets);
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
