package com.tecknobit.neutron.helpers.services;

import com.tecknobit.apimanager.apis.APIRequest;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.neutron.helpers.services.repositories.revenues.RevenueLabelsRepository;
import com.tecknobit.neutron.helpers.services.repositories.revenues.RevenuesRepository;
import com.tecknobit.neutroncore.records.User.NeutronCurrency;
import com.tecknobit.neutroncore.records.revenues.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.neutron.controllers.NeutronController.generateIdentifier;
import static com.tecknobit.neutroncore.records.User.NeutronCurrency.DOLLAR;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Service
public class RevenuesHelper {

    private static final String EXCHANGE_RATES_ENDPOINT = "https://open.er-api.com/v6/latest/USD";

    private static final HashMap<NeutronCurrency, Double> currencyRates = new HashMap<>();

    private static long previousRefreshTimestamp = 0L;

    @Autowired
    private final RevenuesRepository revenuesRepository;

    @Autowired
    private final RevenueLabelsRepository labelsRepository;

    public RevenuesHelper(RevenuesRepository revenuesRepository, RevenueLabelsRepository labelsRepository) {
        this.revenuesRepository = revenuesRepository;
        this.labelsRepository = labelsRepository;
    }

    public List<Revenue> getRevenues(String userId) {
        ArrayList<Revenue> revenues = new ArrayList<>(revenuesRepository.getGeneralRevenues(userId));
        revenues.addAll(revenuesRepository.getProjectRevenues(userId));
        revenues.sort((o1, o2) -> Long.compare(o2.getRevenueTimestamp(), o1.getRevenueTimestamp()));
        return revenues;
    }

    public boolean revenueExists(String userId, String revenueTitle) {
        return revenuesRepository.projectRevenueExists(userId, revenueTitle) != null ||
                revenuesRepository.generalRevenueExists(userId, revenueTitle) != null;
    }

    public boolean revenueExistsById(String userId, String revenueId) {
        return revenuesRepository.projectRevenueExistsById(userId, revenueId) != null ||
                revenuesRepository.generalRevenueExistsById(userId, revenueId) != null;
    }

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

    public ProjectRevenue getProjectRevenue(String userId, String revenueId) {
        return revenuesRepository.projectRevenueExistsById(userId, revenueId);
    }

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

    public TicketRevenue getTicketRevenue(String ticketId, String userId, String projectRevenueId) {
        return revenuesRepository.getTicketRevenue(ticketId, userId, projectRevenueId);
    }

    public void closeTicketRevenue(String ticketId, String userId, String projectRevenueId) {
        revenuesRepository.closeTicketRevenue(
                ticketId,
                userId,
                projectRevenueId,
                System.currentTimeMillis()
        );
    }

    public void deleteTicketRevenue(String ticketId) {
        revenuesRepository.deleteTicketRevenue(
                ticketId
        );
    }

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

    public void convertRevenues(String userId, NeutronCurrency oldCurrency, NeutronCurrency currency) {
        refreshCurrencyRates();
        double taxChange = currencyRates.get(currency);
        for(Revenue revenue : getRevenues(userId)) {
            if(revenue instanceof ProjectRevenue projectRevenue) {
                InitialRevenue initialRevenue = projectRevenue.getInitialRevenue();
                revenuesRepository.convertInitialRevenue(
                        initialRevenue.getId(),
                        getValueConverted(initialRevenue, oldCurrency, currency)
                );
                for (TicketRevenue ticket : projectRevenue.getTickets()) {
                    revenuesRepository.convertGeneralRevenue(
                            ticket.getId(),
                            getValueConverted(ticket, oldCurrency, currency)
                    );
                }
            } else {
                revenuesRepository.convertGeneralRevenue(
                        revenue.getId(),
                        getValueConverted(revenue, oldCurrency, currency)
                );
            }
        }
    }

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

    public static void refreshCurrencyRates() {
        if((System.currentTimeMillis() - previousRefreshTimestamp) >= MILLISECONDS.convert(1, DAYS)) {
            APIRequest apiRequest = new APIRequest();
            try {
                apiRequest.sendAPIRequest(EXCHANGE_RATES_ENDPOINT, GET);
                JsonHelper helper = new JsonHelper(apiRequest.getJSONResponse().toString());
                JSONObject rates = helper.getJSONObject("rates");
                if(rates != null) {
                    for (NeutronCurrency currency : NeutronCurrency.values())
                        currencyRates.put(currency, rates.getDouble(currency.getIsoCode()));
                    previousRefreshTimestamp = System.currentTimeMillis();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
