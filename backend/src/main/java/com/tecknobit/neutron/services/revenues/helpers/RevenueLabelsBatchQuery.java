package com.tecknobit.neutron.services.revenues.helpers;

import com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper;
import com.tecknobit.neutron.services.revenues.entities.GeneralRevenue;
import com.tecknobit.neutron.services.revenues.entities.RevenueLabel;
import jakarta.persistence.Query;

import java.util.List;

/**
 * Class used to execute a batch insert query with a {@link RevenueLabel} item and the related {@link GeneralRevenue}
 *
 * @see EquinoxItemsHelper
 * @see com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper.BatchQuery
 * @see RevenueLabel
 */
public class RevenueLabelsBatchQuery implements EquinoxItemsHelper.BatchQuery<RevenueLabel> {

    /**
     * {@code revenueId} the identifier of the revenue where attach the labels
     */
    private final String revenueId;

    /**
     * {@code labels} the labels to attach to the revenue
     */
    private final List<RevenueLabel> labels;

    /**
     * Constructor to init the {@link LabelsBatchQuery}
     *
     * @param revenueId The identifier of the revenue where attach the labels
     * @param labels The labels to attach to the revenue
     */
    public RevenueLabelsBatchQuery(String revenueId, List<RevenueLabel> labels) {
        this.revenueId = revenueId;
        this.labels = labels;
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
            query.setParameter(index++, revenueId);
            query.setParameter(index++, label.getId());
        }
    }

}
