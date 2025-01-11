package com.tecknobit.neutron.revenues.repositories;

import com.tecknobit.neutron.revenues.entities.RevenueLabel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.neutron.revenues.entities.GeneralRevenue.REVENUE_LABELS_KEY;
import static com.tecknobit.neutron.revenues.entities.Revenue.REVENUE_KEY;
import static com.tecknobit.neutron.revenues.entities.RevenueLabel.REVENUE_LABEL_COLOR_KEY;
import static com.tecknobit.neutron.revenues.entities.RevenueLabel.REVENUE_LABEL_TEXT_KEY;

/**
 * The {@code RevenueLabelsRepository} interface is useful to manage the queries for the labels of a revenue operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see com.tecknobit.neutron.revenues.entities.RevenueLabel
 */
@Service
@Repository
public interface RevenueLabelsRepository extends JpaRepository<RevenueLabel, String> {

    /**
     * Method to store a general revenue label
     *
     * @param labelId The identifier of the label
     * @param labelColor The color of the label
     * @param labelText The text of the label
     * @param revenueId The identifier of the revenue where the label is attached
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + REVENUE_LABELS_KEY + " (" +
                        IDENTIFIER_KEY + "," +
                        REVENUE_LABEL_COLOR_KEY + "," +
                        REVENUE_LABEL_TEXT_KEY + "," +
                        REVENUE_KEY +
                    ") " +
                    "VALUES (" +
                        ":" + IDENTIFIER_KEY + "," +
                        ":" + REVENUE_LABEL_COLOR_KEY + "," +
                        ":" + REVENUE_LABEL_TEXT_KEY + "," +
                        ":" + REVENUE_KEY +
                    ");",
            nativeQuery = true
    )
    void insertRevenueLabel(
            @Param(IDENTIFIER_KEY) String labelId,
            @Param(REVENUE_LABEL_COLOR_KEY) String labelColor,
            @Param(REVENUE_LABEL_TEXT_KEY) String labelText,
            @Param(REVENUE_KEY) String revenueId
    );

}
