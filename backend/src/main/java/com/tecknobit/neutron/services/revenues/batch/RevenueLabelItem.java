package com.tecknobit.neutron.services.revenues.batch;

import com.tecknobit.equinoxbackend.annotations.BatchQueryItem;
import com.tecknobit.equinoxbackend.annotations.TableColumns;
import com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper.ComplexBatchItem;
import com.tecknobit.neutron.services.revenues.entities.GeneralRevenue;
import com.tecknobit.neutron.services.revenues.entities.RevenueLabel;
import com.tecknobit.neutron.services.revenues.service.RevenuesService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.ContantsKt.REVENUE_IDENTIFIER_KEY;

/**
 * The {@code RevenueLabelItem} is useful to handle the labels to delete when the user request to
 * {@link RevenuesService#editGeneralRevenue(String, double, String, long, String, List, String)} and the {@link #label}
 * is not more attached to the owner {@link #revenue}
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see ComplexBatchItem
 *
 * @since 1.0.2
 */
@BatchQueryItem
public class RevenueLabelItem implements ComplexBatchItem {

    /**
     * {@code revenue} the owner revenue of the label
     */
    private final GeneralRevenue revenue;

    /**
     * {@code label} the label to delete due the user removed from the labels attached to the revenue
     */
    private final RevenueLabel label;

    /**
     * Constructor to create the item
     * @param revenue the owner revenue of the label
     * @param label the label to delete due the user removed from the labels attached to the revenue
     */
    public RevenueLabelItem(GeneralRevenue revenue, RevenueLabel label) {
        this.revenue = revenue;
        this.label = label;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TableColumns(columns = {REVENUE_IDENTIFIER_KEY, IDENTIFIER_KEY})
    public @NotNull List<?> mappedValues() {
        return List.of(revenue.getId(), label.getId());
    }

}
