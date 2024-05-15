package com.tecknobit.neutroncore.records.revenues;

import com.tecknobit.neutroncore.records.NeutronItem;

public class RevenueLabel extends NeutronItem {

    private final String text;

    private final String color;

    public RevenueLabel(String text, String color) {
        this(null, text, color);
    }

    public RevenueLabel(String id, String text, String color) {
        super(id);
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public String getColor() {
        return color;
    }

}
