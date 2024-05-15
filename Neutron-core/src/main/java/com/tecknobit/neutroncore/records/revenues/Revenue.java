package com.tecknobit.neutroncore.records.revenues;

import com.tecknobit.neutroncore.records.NeutronItem;

public abstract class Revenue extends NeutronItem {

    protected final String title;

    protected final double value;

    protected final long revenueDate;

    public Revenue(String id, String title, double value, long revenueDate) {
        super(id);
        this.title = title;
        this.value = value;
        this.revenueDate = revenueDate;
    }

    public String getTitle() {
        return title;
    }

    public double getValue() {
        return value;
    }

    public long getRevenueTimestamp() {
        return revenueDate;
    }

    public String getRevenueDate() {
        return timeFormatter.formatAsString(revenueDate);
    }

}
