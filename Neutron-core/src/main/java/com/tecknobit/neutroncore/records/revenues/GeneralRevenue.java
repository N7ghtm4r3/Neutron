package com.tecknobit.neutroncore.records.revenues;

import com.tecknobit.neutroncore.records.NeutronItem;

import java.util.List;

public class GeneralRevenue extends Revenue {

    private final List<Label> labels;

    private final String description;

    public GeneralRevenue(String id, String title, double value, long revenueDate, List<Label> labels,
                          String description) {
        super(id, title, value, revenueDate);
        this.labels = labels;
        this.description = description;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public String getDescription() {
        return description;
    }

    public static class Label extends NeutronItem {

        private final String text;

        private final String color;

        public Label(String text, String color) {
            this(null, text, color);
        }

        public Label(String id, String text, String color) {
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

}
