package com.tecknobit.neutron.services.wallet.service;

import com.tecknobit.apimanager.trading.TradingTools;
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

@Service
public class WalletService {

    private static final int PREVIOUS_PERIOD_GAP = 2;

    @Autowired
    private RevenuesService revenuesService;

    public WalletStatus getWalletStatus(String userId, RevenuePeriod period, Set<String> labels) {
        List<Revenue> revenues = revenuesService.getRevenues(userId, DEFAULT_PAGE, MAX_VALUE, period, labels).getData();
        List<Revenue> lastMonthRevenues = revenuesService.getRevenues(userId, DEFAULT_PAGE, MAX_VALUE, period,
                PREVIOUS_PERIOD_GAP, labels).getData();
        lastMonthRevenues.removeAll(revenues);
        double totalEarnings = calculateEarnings(revenues);
        double lastMonthEarnings = calculateEarnings(lastMonthRevenues);
        double trend = 100.0;
        if(lastMonthEarnings != 0)
            trend = TradingTools.computeAssetPercent(lastMonthEarnings, totalEarnings);
        return new WalletStatus(
                totalEarnings,
                trend
        );
    }

    private double calculateEarnings(List<Revenue> revenues) {
        double earnings = 0;
        for (Revenue revenue : revenues)
            earnings += revenue.getValue();
        return earnings;
    }

}
