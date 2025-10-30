package com.tecknobit.neutron.services.revenues.batch;

import com.tecknobit.equinoxbackend.annotations.BatchQueryImpl;
import com.tecknobit.equinoxbackend.annotations.TableColumns;
import com.tecknobit.equinoxbackend.apis.batch.EquinoxItemsHelper;
import com.tecknobit.neutron.services.revenues.entities.RevenueLabel;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.ContantsKt.REVENUE_LABEL_COLOR_KEY;
import static com.tecknobit.neutroncore.ContantsKt.REVENUE_LABEL_TEXT_KEY;

/**
 * Class used to execute a batch insert query with a {@link RevenueLabel} item
 *
 * @see EquinoxItemsHelper
 * @see com.tecknobit.equinoxbackend.apis.batch.EquinoxItemsHelper.BatchQuery
 * @see RevenueLabel
 */
@BatchQueryImpl
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
    @TableColumns(columns = {IDENTIFIER_KEY, REVENUE_LABEL_COLOR_KEY, REVENUE_LABEL_TEXT_KEY})
    public void prepareQuery(Query query, int index, Collection<RevenueLabel> items) {
        for (RevenueLabel label : labels) {
            query.setParameter(index++, label.getId());
            query.setParameter(index++, label.getColor());
            query.setParameter(index++, label.getText());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getColumns() {
        return new String[]{IDENTIFIER_KEY, REVENUE_LABEL_COLOR_KEY, REVENUE_LABEL_TEXT_KEY};
    }

}
