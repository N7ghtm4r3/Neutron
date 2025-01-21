package com.tecknobit.neutron.services.revenues.service;

import com.tecknobit.apimanager.apis.APIRequest;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper;
import com.tecknobit.equinoxcore.annotations.Wrapper;
import com.tecknobit.equinoxcore.pagination.PaginatedResponse;
import com.tecknobit.neutron.services.revenues.entities.*;
import com.tecknobit.neutron.services.revenues.helpers.LabelsBatchQuery;
import com.tecknobit.neutron.services.revenues.helpers.RevenueLabelsBatchQuery;
import com.tecknobit.neutron.services.revenues.repositories.RevenueLabelsRepository;
import com.tecknobit.neutron.services.revenues.repositories.RevenuesRepository;
import com.tecknobit.neutroncore.enums.NeutronCurrency;
import com.tecknobit.neutroncore.enums.RevenuePeriod;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.apimanager.trading.TradingTools.roundValue;
import static com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController.generateIdentifier;
import static com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper.InsertCommand.INSERT_IGNORE_INTO;
import static com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper.InsertCommand.INSERT_INTO;
import static com.tecknobit.equinoxcore.pagination.PaginatedResponse.DEFAULT_PAGE;
import static com.tecknobit.neutroncore.ContantsKt.*;
import static com.tecknobit.neutroncore.enums.NeutronCurrency.DOLLAR;
import static com.tecknobit.neutroncore.enums.RevenuePeriod.ALL;
import static java.lang.Integer.MAX_VALUE;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * The {@code RevenuesService} class is useful to manage all the revenues database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see EquinoxItemsHelper
 */
@Service
public class RevenuesService extends EquinoxItemsHelper {

    /**
     * {@code EXCHANGE_RATES_ENDPOINT} the endpoint to get the new fiat tax change
     */
    private static final String EXCHANGE_RATES_ENDPOINT = "https://open.er-api.com/v6/latest/USD";

    /**
     * {@code currencyRates} all the fiat tax change
     */
    private static final HashMap<NeutronCurrency, Double> currencyRates = new HashMap<>();

    /**
     * {@code previousRefreshTimestamp} the previous time when the {@link #currencyRates} have been refreshed
     */
    private static long previousRefreshTimestamp = 0L;

    /**
     * {@code revenuesRepository} instance for the revenues repository
     */
    @Autowired
    private RevenuesRepository revenuesRepository;

    /**
     * {@code labelsRepository} instance for the revenue labels repository
     */
    @Autowired
    private RevenueLabelsRepository labelsRepository;

    /**
     * Method to retrieve all the labels created by the user
     *
     * @param userId The user identifier
     *
     * @return the labels of the user as {@link Set} of {@link RevenueLabel}
     */
    public Set<RevenueLabel> getRevenueLabels(String userId) {
        return labelsRepository.getRevenueLabels(userId);
    }

    /**
     * Method to get the revenues of a user
     *
     * @param userId The user identifier
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param period The period to use to select the revenues
     *
     * @return the revenues getRevenues as {@link PaginatedResponse} of {@link Revenue}
     */
    @Wrapper
    public PaginatedResponse<Revenue> getRevenues(String userId, int page, int pageSize, RevenuePeriod period) {
        return getRevenues(userId, page, pageSize, period, 1, true, true, new JSONArray());
    }

    /**
     * Method to get the revenues of a user
     *
     * @param userId The user identifier
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param period The period to use to select the revenues
     * @param retrieveGeneralRevenues Whether include the {@link com.tecknobit.neutron.services.revenues.entities.GeneralRevenue}
     * @param retrieveProjectRevenues Whether include the {@link com.tecknobit.neutron.services.revenues.entities.ProjectRevenue}
     * @param labels The labels used to filter the data
     *
     * @return the revenues getRevenues as {@link PaginatedResponse} of {@link Revenue}
     */
    @Wrapper
    public PaginatedResponse<Revenue> getRevenues(String userId, int page, int pageSize, RevenuePeriod period,
                                                  boolean retrieveGeneralRevenues, boolean retrieveProjectRevenues,
                                                  JSONArray labels) {
        return getRevenues(userId, page, pageSize, period, 1, retrieveGeneralRevenues, retrieveProjectRevenues, labels);
    }

    /**
     * Method to get the revenues of a user
     *
     * @param userId The user identifier
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param period The period to use to select the revenues
     * @param offset The offset to apply to the period, for example to obtain the previous month you need to fetch the
     *              previous revenues of that month
     * @param labels The labels used to filter the data
     * @param retrieveGeneralRevenues Whether include the {@link com.tecknobit.neutron.services.revenues.entities.GeneralRevenue}
     * @param retrieveProjectRevenues Whether include the {@link com.tecknobit.neutron.services.revenues.entities.ProjectRevenue}
     *
     * @return the revenues getRevenues as {@link PaginatedResponse} of {@link Revenue}
     */
    public PaginatedResponse<Revenue> getRevenues(String userId, int page, int pageSize, RevenuePeriod period,
                                                  int offset, boolean retrieveGeneralRevenues,
                                                  boolean retrieveProjectRevenues, JSONArray labels) {
        List<Revenue> revenues = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, pageSize);
        long fromDate = period.calculateFromDate(period, offset);
        long revenuesCount = 0;
        long projectsCount = 0;
        List<String> labelsFilter;
        if(labels == null)
            labelsFilter = Collections.EMPTY_LIST;
        else
            labelsFilter = new JsonHelper(labels).toList();
        if(retrieveGeneralRevenues) {
            revenues.addAll(revenuesRepository.getGeneralRevenues(userId, fromDate, labelsFilter, pageable));
            revenuesCount = revenuesRepository.countGeneralRevenues(userId, fromDate, labelsFilter);
        }
        if(retrieveProjectRevenues) {
            revenues.addAll(revenuesRepository.getProjectRevenues(userId, fromDate, pageable));
            projectsCount = revenuesRepository.countProjectRevenues(userId, fromDate);
        }
        revenues.sort((o1, o2) -> Long.compare(o2.getRevenueTimestamp(), o1.getRevenueTimestamp()));
        return new PaginatedResponse<>(
                revenues,
                page,
                pageSize,
                revenuesCount + projectsCount
        );
    }

    /**
     * Method to get whether a revenue exists
     *
     * @param userId The identifier of the user
     * @param revenueId The identifier of the revenue to check
     * @return whether a revenue exists as boolean
     */
    public boolean revenueExistsById(String userId, String revenueId) {
        return revenuesRepository.projectRevenueExistsById(userId, revenueId) != null ||
                revenuesRepository.generalRevenueExistsById(userId, revenueId) != null;
    }

    /**
     * Method to store a new project revenue
     *
     * @param projectRevenueId The identifier of the new project revenue
     * @param revenueValue The initial value of the project ({@link InitialRevenue})
     * @param revenueTitle The title of the project
     * @param insertionDate The date when the project has been created/inserted
     * @param userId The identifier of the user who created the project
     */
    public void createProjectRevenue(String projectRevenueId, double revenueValue, String revenueTitle, long insertionDate,
                                     String userId) {
        revenuesRepository.insertProjectRevenue(
                projectRevenueId,
                revenueTitle,
                insertionDate,
                userId
        );
        revenuesRepository.insertInitialRevenue(
                generateIdentifier(),
                insertionDate,
                revenueTitle,
                roundValue(revenueValue, 2),
                userId,
                projectRevenueId
        );
    }

    /**
     * Method to store a new general revenue
     *
     * @param revenueId The identifier of the new revenue
     * @param revenueValue The value of the revenue
     * @param revenueTitle The title of the revenue
     * @param insertionDate The date when the revenue has been created/inserted
     * @param revenueDescription The description of the revenue
     * @param labels The labels attached to the revenue
     * @param userId The identifier of the user who created the revenue
     */
    public void createGeneralRevenue(String revenueId, double revenueValue, String revenueTitle, long insertionDate,
                                     String revenueDescription, List<RevenueLabel> labels, String userId) {
        revenuesRepository.insertGeneralRevenue(
                revenueId,
                revenueTitle,
                insertionDate,
                roundValue(revenueValue, 2),
                revenueDescription,
                userId
        );
        batchInsert(INSERT_IGNORE_INTO, LABELS_KEY, new LabelsBatchQuery(labels), IDENTIFIER_KEY,
                REVENUE_LABEL_COLOR_KEY, REVENUE_LABEL_TEXT_KEY);
        batchInsert(INSERT_INTO, REVENUE_LABELS_KEY, new RevenueLabelsBatchQuery(revenueId, labels), REVENUE_IDENTIFIER_KEY,
                IDENTIFIER_KEY);
    }

    /**
     * Method to store a new general revenue
     *
     * @param revenueId The identifier of the new revenue
     * @param revenueValue The value of the revenue
     * @param revenueTitle The title of the revenue
     * @param insertionDate The date when the revenue has been created/inserted
     * @param revenueDescription The description of the revenue
     * @param labels The labels attached to the revenue
     * @param userId The identifier of the user who created the revenue
     */
    public void editGeneralRevenue(String revenueId, double revenueValue, String revenueTitle, long insertionDate,
                                   String revenueDescription, List<RevenueLabel> labels, String userId) {
        revenuesRepository.editGeneralRevenue(
                revenueId,
                revenueTitle,
                insertionDate,
                roundValue(revenueValue, 2),
                revenueDescription
        );
        batchInsert(INSERT_IGNORE_INTO, LABELS_KEY, new LabelsBatchQuery(labels),
                IDENTIFIER_KEY, REVENUE_LABEL_COLOR_KEY, REVENUE_LABEL_TEXT_KEY);
        batchInsert(INSERT_IGNORE_INTO, REVENUE_LABELS_KEY, new RevenueLabelsBatchQuery(revenueId, labels),
                REVENUE_IDENTIFIER_KEY, IDENTIFIER_KEY);
        GeneralRevenue generalRevenue = revenuesRepository.generalRevenueExistsById(userId, revenueId);
        HashSet<String> currentLabels = new HashSet<>(generalRevenue.getLabels()
                .stream()
                .map(RevenueLabel::compareOn)
                .toList());
        for (RevenueLabel label : labels)
            currentLabels.remove(label.compareOn());
        batchDelete(REVENUE_LABELS_KEY, List.of(List.of(revenueId), currentLabels.stream().toList()), REVENUE_IDENTIFIER_KEY,
                IDENTIFIER_KEY);
    }

    /**
     * Method to get a revenue
     *
     * @param userId The identifier of the user who requested the revenue
     * @param revenueId The identifier of the project to get
     * @return the revenue as {@link Revenue}, null if it not exists
     */
    public GeneralRevenue getGeneralRevenue(String userId, String revenueId) {
        return revenuesRepository.generalRevenueExistsById(userId, revenueId);
    }

    /**
     * Method to edit an existing project revenue
     *
     * @param projectRevenueId The identifier of the project revenue
     * @param revenueValue The initial value of the project ({@link InitialRevenue})
     * @param revenueTitle The title of the project
     * @param insertionDate The date when the project has been created/inserted
     * @param userId The identifier of the user who created the project
     */
    public void editProjectRevenue(String projectRevenueId, double revenueValue, String revenueTitle, long insertionDate,
                                   String userId) {
        revenuesRepository.editProjectRevenue(
                projectRevenueId,
                revenueTitle,
                insertionDate
        );
        ProjectRevenue projectRevenue = getProjectRevenue(userId, projectRevenueId);
        revenuesRepository.editInitialRevenue(
                projectRevenue.getInitialRevenue().getId(),
                insertionDate,
                revenueTitle,
                roundValue(revenueValue, 2)
        );
    }

    /**
     * Method to get a project
     *
     * @param userId The identifier of the user who requested the project
     * @param revenueId The identifier of the project to get
     * @return the project revenue as {@link ProjectRevenue}, null if it not exists
     */
    public ProjectRevenue getProjectRevenue(String userId, String revenueId) {
        return revenuesRepository.projectRevenueExistsById(userId, revenueId);
    }

    /**
     * Method to get the balance of the project, this count just the closed ticket
     *
     * @param project The project to calculate its balance
     * @param period The period to use to select the tickets
     * @param retrieveClosedTickets Whether include the closed tickets
     *
     * @return the balance of the project as {@code double}
     */
    public double getProjectBalance(ProjectRevenue project, RevenuePeriod period, boolean retrieveClosedTickets) {
        double balance = project.getInitialRevenue().getValue();
        List<TicketRevenue> tickets = getTickets(project.getId(), DEFAULT_PAGE, MAX_VALUE, period, false,
                retrieveClosedTickets).getData();
        for (TicketRevenue ticket : tickets)
                balance += ticket.getValue();
        return roundValue(balance, 2);
    }

    /**
     * Method to get the tickets attached to a project
     *
     * @param projectId The project identifier
     * @param page      The page requested
     * @param pageSize  The size of the items to insert in the page
     * @param period The period to use to select the tickets
     * @param retrievePendingTickets Whether include the pending tickets
     * @param retrieveClosedTickets Whether include the closed tickets
     *
     * @return the tickets attached to a project as {@link PaginatedResponse} of {@link TicketRevenue}
     */
    public PaginatedResponse<TicketRevenue> getTickets(String projectId, int page, int pageSize, RevenuePeriod period,
                                                       boolean retrievePendingTickets, boolean retrieveClosedTickets) {
        Pageable pageable = PageRequest.of(page, pageSize);
        long fromDate = period.calculateFromDate(period, 1);
        List<TicketRevenue> tickets = new ArrayList<>();
        long totalTickets = 0;
        if(retrievePendingTickets && retrieveClosedTickets) {
            tickets = revenuesRepository.getAllTickets(projectId, fromDate, pageable);
            totalTickets = revenuesRepository.countAllTickets(projectId, fromDate);
        } else if(retrievePendingTickets) {
            tickets = revenuesRepository.getPendingTickets(projectId, fromDate, pageable);
            totalTickets = revenuesRepository.countPendingTickets(projectId, fromDate);
        } else if(retrieveClosedTickets){
            tickets = revenuesRepository.getClosedTickets(projectId, fromDate, pageable);
            totalTickets = revenuesRepository.countClosedTickets(projectId, fromDate);
        }
        return new PaginatedResponse<>(tickets, page, pageSize, totalTickets);
    }

    /**
     * Method to store a ticket for a project
     *
     * @param ticketId The identifier of the ticket
     * @param ticketRevenue The amount value of the ticket
     * @param ticketTitle The title of the ticket
     * @param ticketDescription The description of the ticket
     * @param openingTime When the ticket has been opened
     * @param projectRevenueId The identifier of the project where attach the ticket
     * @param userId The identifier of the user who requested the ticket creation
     */
    public void addTicket(String ticketId, double ticketRevenue, String ticketTitle,
                          String ticketDescription, long openingTime, String projectRevenueId,
                          String userId) {
        revenuesRepository.addTicket(
                ticketId,
                roundValue(ticketRevenue, 2),
                ticketTitle,
                ticketDescription,
                openingTime,
                projectRevenueId,
                userId
        );
    }

    /**
     * Method to update an existing ticket of a project
     *
     * @param ticketId The identifier of the ticket
     * @param ticketRevenue The amount value of the ticket
     * @param ticketTitle The title of the ticket
     * @param ticketDescription The description of the ticket
     * @param openingTime When the ticket has been opened
     */
    public void editTicket(String ticketId, double ticketRevenue, String ticketTitle, String ticketDescription,
                           long openingTime) {
        revenuesRepository.editTicket(
                ticketId,
                roundValue(ticketRevenue, 2),
                ticketTitle,
                ticketDescription,
                openingTime
        );
    }

    /**
     * Method to get a ticket
     *
     * @param ticketId The identifier of the ticket to get
     * @param userId The identifier of the user who requested the ticket
     * @param projectRevenueId The identifier of the project
     * @return the ticket revenue as {@link TicketRevenue}, null if it not exists
     */
    public TicketRevenue getTicketRevenue(String ticketId, String userId, String projectRevenueId) {
        return revenuesRepository.getTicketRevenue(ticketId, userId, projectRevenueId);
    }

    /**
     * Method to close a ticket
     *
     * @param ticketId The identifier of the ticket to close
     * @param userId The identifier of the user who requested the closing of the ticket
     * @param projectRevenueId The identifier of the project
     */
    public void closeTicketRevenue(String ticketId, String userId, String projectRevenueId) {
        revenuesRepository.closeTicketRevenue(
                ticketId,
                userId,
                projectRevenueId,
                System.currentTimeMillis()
        );
    }

    /**
     * Method to delete a ticket
     *
     * @param ticketId The identifier of the ticket to delete
     */
    public void deleteTicketRevenue(String ticketId) {
        revenuesRepository.deleteTicketRevenue(
                ticketId
        );
    }

    /**
     * Method to delete a revenue
     *
     * @param userId The identifier of the user who requested the deletion of the revenue
     * @param revenueId The identifier of the revenue to delete
     *
     * @return whether the revenue has been deleted as boolean
     */
    public boolean deleteRevenue(String userId, String revenueId) {
        if(getProjectRevenue(userId, revenueId) != null) {
            revenuesRepository.deleteProjectRevenue(revenueId, userId);
            return true;
        } else {
            GeneralRevenue revenue = revenuesRepository.generalRevenueExistsById(userId, revenueId);
            if (revenue != null) {
                deleteAndDeleteUnrelatedLabels(revenue);
                revenuesRepository.deleteGeneralRevenue(revenueId, userId);
                return true;

            } else
                return false;
        }
    }

    /**
     * Method to detach the labels from the deleting revenue and, if needed, delete the labels which no have relationship
     * with other revenues
     *
     * @param revenue The deleting revenue
     */
    private void deleteAndDeleteUnrelatedLabels(GeneralRevenue revenue) {
        List<RevenueLabel> labels = revenue.getLabels();
        revenuesRepository.detachLabelsFromGeneralRevenue(revenue.getId());
        for (RevenueLabel label : labels) {
            String labelId = label.getId();
            long labelRelationships = labelsRepository.countSharedLabels(labelId);
            if(labelRelationships == 0)
                labelsRepository.deleteById(labelId);
        }
    }

    /**
     * Method to convert all the revenues values in a new fiat currency
     *
     * @param userId The identifier of the user who requested the newCurrency change
     * @param oldCurrency The current user's currency
     * @param newCurrency The new currency of the user
     */
    public void convertRevenues(String userId, NeutronCurrency oldCurrency, NeutronCurrency newCurrency) {
        refreshCurrencyRates();
        double taxChange = currencyRates.get(newCurrency);
        List<Revenue> userRevenues = getRevenues(userId, 0, MAX_VALUE, ALL).getData();
        for(Revenue revenue : userRevenues) {
            if(revenue instanceof ProjectRevenue projectRevenue) {
                InitialRevenue initialRevenue = projectRevenue.getInitialRevenue();
                revenuesRepository.convertInitialRevenue(
                        initialRevenue.getId(),
                        getValueConverted(initialRevenue, oldCurrency, newCurrency)
                );
                for (TicketRevenue ticket : projectRevenue.getTickets()) {
                    revenuesRepository.convertGeneralRevenue(
                            ticket.getId(),
                            getValueConverted(ticket, oldCurrency, newCurrency)
                    );
                }
            } else {
                revenuesRepository.convertGeneralRevenue(
                        revenue.getId(),
                        getValueConverted(revenue, oldCurrency, newCurrency)
                );
            }
        }
    }

    /**
     * Method to get the value of a revenue converted in the new fiat currency
     *
     * @param revenue The revenue from convert its value
     * @param oldCurrency The current user's currency
     * @param newCurrency The new currency of the user
     *
     * @return the value converted as double
     */
    private double getValueConverted(Revenue revenue, NeutronCurrency oldCurrency, NeutronCurrency newCurrency) {
        double revenueValue = revenue.getValue();
        double taxChange;
        if(oldCurrency == DOLLAR) {
            taxChange = currencyRates.get(newCurrency);
            return roundValue(revenueValue * taxChange, 2);
        } else {
            taxChange = currencyRates.get(oldCurrency);
            double usdValue = revenueValue / taxChange;
            if(newCurrency == DOLLAR)
                return roundValue(usdValue, 2);
            else
                return roundValue(usdValue * currencyRates.get(newCurrency), 2);
        }
    }

    /**
     * Method to refresh the {@link #currencyRates} values 
     *
     * @apiNote this is an api request made to the <a href="https://www.exchangerate-api.com">Rates By Exchange Rate API service</a>
     */
    public static void refreshCurrencyRates() {
        if((System.currentTimeMillis() - previousRefreshTimestamp) >= MILLISECONDS.convert(1, DAYS)) {
            APIRequest apiRequest = new APIRequest();
            try {
                apiRequest.sendAPIRequest(EXCHANGE_RATES_ENDPOINT, GET);
                JsonHelper helper = new JsonHelper(apiRequest.getJSONResponse().toString());
                JSONObject rates = helper.getJSONObject("rates");
                if(rates != null) {
                    for (NeutronCurrency currency : NeutronCurrency.getEntries())
                        currencyRates.put(currency, rates.getDouble(currency.getIsoCode()));
                    previousRefreshTimestamp = System.currentTimeMillis();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
