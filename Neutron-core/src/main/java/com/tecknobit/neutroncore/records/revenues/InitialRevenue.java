package com.tecknobit.neutroncore.records.revenues;

public class InitialRevenue extends Revenue {

    public static final String INITIAL_REVENUE_KEY = "initial_revenue";

    public InitialRevenue(String id, double value, long revenueDate) {
        super(id, INITIAL_REVENUE_KEY, value, revenueDate);
    }

}
