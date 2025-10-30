package com.tecknobit.neutron.services.revenues.batch;

import com.tecknobit.equinoxbackend.annotations.BatchQueryImpl;
import com.tecknobit.equinoxbackend.annotations.TableColumns;
import com.tecknobit.equinoxbackend.apis.batch.EquinoxItemsHelper;
import com.tecknobit.equinoxbackend.apis.batch.EquinoxItemsHelper.BatchQuery;
import com.tecknobit.neutron.services.revenues.entities.GeneralRevenue;
import com.tecknobit.neutron.services.revenues.entities.RevenueLabel;
import jakarta.persistence.Query;

import java.util.Collection;
import java.util.List;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.ContantsKt.REVENUE_IDENTIFIER_KEY;

/**
 * Class used to execute a batch insert query with a {@link RevenueLabel} item and the related {@link GeneralRevenue}
 *
 * @see EquinoxItemsHelper
 * @see EquinoxItemsHelper.BatchQuery
 * @see RevenueLabel
 */
@BatchQueryImpl
public class RevenueLabelsBatchQuery implements BatchQuery<RevenueLabel> {

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
    @TableColumns(columns = {REVENUE_IDENTIFIER_KEY, IDENTIFIER_KEY})
    public void prepareQuery(Query query, int index, Collection<RevenueLabel> items) {
        for (RevenueLabel label : labels) {
            query.setParameter(index++, revenueId);
            query.setParameter(index++, label.getId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getColumns() {
        return new String[]{REVENUE_IDENTIFIER_KEY, IDENTIFIER_KEY};
    }

}
