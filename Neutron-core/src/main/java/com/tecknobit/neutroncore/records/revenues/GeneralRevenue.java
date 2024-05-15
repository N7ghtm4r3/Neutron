package com.tecknobit.neutroncore.records.revenues;

import java.util.List;

public class GeneralRevenue extends Revenue {

    protected final List<RevenueLabel> labels;

    protected final String description;

    public GeneralRevenue(String id, String title, double value, long revenueDate, List<RevenueLabel> labels,
                          String description) {
        super(id, title, value, revenueDate);
        this.labels = labels;
        this.description = description;
    }

    public List<RevenueLabel> getLabels() {
        return labels;
    }

    public String getDescription() {
        return description;
    }

}
