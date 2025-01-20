package com.tecknobit.neutron.services.wallet.service;

import com.tecknobit.apimanager.trading.TradingTools;
import com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper;
import com.tecknobit.neutron.services.revenues.entities.Revenue;
import com.tecknobit.neutron.services.revenues.service.RevenuesService;
import com.tecknobit.neutroncore.dtos.WalletStatus;
import com.tecknobit.neutroncore.enums.RevenuePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.tecknobit.equinoxcore.pagination.PaginatedResponse.DEFAULT_PAGE;
import static java.lang.Integer.MAX_VALUE;

/**
 * The {@code WalletService} class is useful to manage all the wallet database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see EquinoxItemsHelper
 */
@Service
public class WalletService {

    /**
     * {@code PREVIOUS_PERIOD_GAP} constant value used to indicate the previous period from the chosen one, for example
     * the past month, past week, etc...
     */
    private static final int PREVIOUS_PERIOD_GAP = 2;

    /**
     * {@code revenuesService} helper to manage the revenues database operations
     */
    @Autowired
    private RevenuesService revenuesService;

    /**
     * Method to get the wallet status of the user
     *
     * @param userId The identifier of the user
     * @param period The period to use to select the revenues
     * @param retrieveGeneralRevenues Whether include the {@link com.tecknobit.neutron.services.revenues.entities.GeneralRevenue}
     * @param retrieveProjectRevenues Whether include the {@link com.tecknobit.neutron.services.revenues.entities.ProjectRevenue}
     * @param labels The labels used to filter the data
     *
     * @return the result of the request as {@link WalletStatus}
     */
    public WalletStatus getWalletStatus(String userId, RevenuePeriod period, boolean retrieveGeneralRevenues,
                                        boolean retrieveProjectRevenues, Set<String> labels) {
        List<Revenue> revenues = revenuesService.getRevenues(userId, DEFAULT_PAGE, MAX_VALUE, period,
                retrieveGeneralRevenues, retrieveProjectRevenues, labels).getData();
        List<Revenue> lastMonthRevenues = revenuesService.getRevenues(userId, DEFAULT_PAGE, MAX_VALUE, period,
                PREVIOUS_PERIOD_GAP, retrieveGeneralRevenues, retrieveProjectRevenues, labels).getData();
        lastMonthRevenues.removeAll(revenues);
        double totalEarnings = calculateEarnings(revenues);
        double lastMonthEarnings = calculateEarnings(lastMonthRevenues);
        double trend = 0.0;
        if(lastMonthEarnings != 0)
            trend = TradingTools.computeAssetPercent(lastMonthEarnings, totalEarnings, 2);
        return new WalletStatus(
                TradingTools.roundValue(totalEarnings, 2),
                trend
        );
    }

    /**
     * Method to calculate the earnings based on the revenues
     *
     * @param revenues The list of the revenues to use to calculate the total earnings
     * @return the total earnings as {@code double}
     */
    private double calculateEarnings(List<Revenue> revenues) {
        double earnings = 0;
        for (Revenue revenue : revenues)
            earnings += revenue.getValue();
        return earnings;
    }

}
