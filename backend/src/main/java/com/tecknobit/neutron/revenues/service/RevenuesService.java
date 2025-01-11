package com.tecknobit.neutron.revenues.service;

import com.tecknobit.apimanager.apis.APIRequest;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.neutron.revenues.entities.*;
import com.tecknobit.neutron.revenues.repositories.RevenueLabelsRepository;
import com.tecknobit.neutron.revenues.repositories.RevenuesRepository;
import com.tecknobit.neutroncore.enums.NeutronCurrency;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController.generateIdentifier;
import static com.tecknobit.neutroncore.enums.NeutronCurrency.DOLLAR;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * The {@code RevenuesService} class is useful to manage all the revenues database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Service
public class RevenuesService {

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
     * {@code labelsRepository} instance for the labels repository
     */
    @Autowired
    private RevenueLabelsRepository labelsRepository;

    /**
     * Method to get the revenues of a user
     *
     * @param userId The user identifier
     * @return the revenues list as {@link List} of {@link Revenue}
     */
    public List<Revenue> getRevenues(String userId) {
        ArrayList<Revenue> revenues = new ArrayList<>(revenuesRepository.getGeneralRevenues(userId));
        revenues.addAll(revenuesRepository.getProjectRevenues(userId));
        revenues.sort((o1, o2) -> Long.compare(o2.getRevenueTimestamp(), o1.getRevenueTimestamp()));
        return revenues;
    }

    /**
     * Method to get whether a revenue exists
     *
     * @param userId The identifier of the user
     * @param revenueTitle The title of the revenue to check
     * @return whether a revenue exists as boolean
     */
    public boolean revenueExists(String userId, String revenueTitle) {
        return revenuesRepository.projectRevenueExists(userId, revenueTitle) != null ||
                revenuesRepository.generalRevenueExists(userId, revenueTitle) != null;
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
                revenueValue,
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
                revenueValue,
                revenueDescription,
                userId
        );
        for (RevenueLabel label : labels) {
            String id = label.getId();
            if(id == null)
                id = generateIdentifier();
            labelsRepository.insertRevenueLabel(
                id,
                label.getColor(),
                label.getText(),
                revenueId
            );
        }
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
     * Method to store a ticket for a project
     *
     * @param ticketId The identifier of the ticket
     * @param ticketRevenue The amount value of the ticket
     * @param ticketTitle The title of the ticket
     * @param ticketDescription The description of the ticket
     * @param openingTime When the ticket has been opened
     * @param closingTime When the ticket has been closed
     * @param projectRevenueId The identifier of the project where attach the ticket
     * @param userId The identifier of the user who requested the ticket creation
     */
    public void addTicketToProjectRevenue(String ticketId, double ticketRevenue, String ticketTitle,
                                          String ticketDescription, long openingTime, long closingTime,
                                          String projectRevenueId, String userId) {
        revenuesRepository.addTicketToProjectRevenue(
                ticketId,
                ticketRevenue,
                ticketTitle,
                ticketDescription,
                openingTime,
                closingTime,
                projectRevenueId,
                userId
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
     */
    public boolean deleteRevenue(String userId, String revenueId) {
        if(getProjectRevenue(userId, revenueId) != null) {
            revenuesRepository.deleteProjectRevenue(revenueId, userId);
            return true;
        } else if(revenuesRepository.generalRevenueExistsById(userId, revenueId) != null) {
            revenuesRepository.deleteGeneralRevenue(revenueId, userId);
            return true;
        }
        return false;
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
        for(Revenue revenue : getRevenues(userId)) {
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
            return revenueValue * taxChange;
        } else {
            taxChange = currencyRates.get(oldCurrency);
            double usdValue = revenueValue / taxChange;
            if(newCurrency == DOLLAR)
                return usdValue;
            else
                return usdValue * currencyRates.get(newCurrency);
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
