package com.tecknobit.neutron.services.revenues.helpers;

import com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper;
import com.tecknobit.neutron.services.revenues.entities.RevenueLabel;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to execute a batch insert query with a {@link RevenueLabel} item
 *
 * @see EquinoxItemsHelper
 * @see com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper.BatchQuery
 * @see RevenueLabel
 */
public class LabelsBatchQuery implements EquinoxItemsHelper.BatchQuery<RevenueLabel> {

    /**
     * {@code labels} the labels attached to the revenue
     */
    private final ArrayList<RevenueLabel> labels;

    /**
     * Constructor to init the {@link LabelsBatchQuery}
     *
     * @param labels The labels attached to the revenue
     */
    public LabelsBatchQuery(List<RevenueLabel> labels) {
        this.labels = new ArrayList<>(labels);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RevenueLabel> getData() {
        return labels;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareQuery(Query query, int index, List<RevenueLabel> labels) {
        for (RevenueLabel label : labels) {
            query.setParameter(index++, label.getId());
            query.setParameter(index++, label.getColor());
            query.setParameter(index++, label.getText());
        }
    }

}
