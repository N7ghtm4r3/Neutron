package com.tecknobit.neutron.services.revenues.repository;

import com.tecknobit.neutron.services.revenues.entities.RevenueLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

import static com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.ContantsKt.*;

/**
 * The {@code RevenueLabelsRepository} interface is useful to manage the queries for the revenue labels operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see RevenueLabel
 */
@Repository
public interface RevenueLabelsRepository extends JpaRepository<RevenueLabel, String> {

    /**
     * Method to retrieve all the labels created by the user
     *
     * @param userId The user identifier
     *
     * @return the labels of the user as {@link Set} of {@link RevenueLabel}
     */
    @Query(
            value = "SELECT DISTINCT " + REVENUE_LABELS_KEY + ".* FROM " +
                    REVENUE_LABELS_KEY + " AS " + REVENUE_LABELS_KEY +
                    " INNER JOIN " + GENERAL_REVENUES_KEY + " AS " + GENERAL_REVENUES_KEY + " ON " +
                    REVENUE_LABELS_KEY + "." + REVENUE_KEY + "=" + GENERAL_REVENUES_KEY + "." + IDENTIFIER_KEY +
                    " WHERE " + GENERAL_REVENUES_KEY + "." + OWNER_KEY + "=:" + IDENTIFIER_KEY +
                    " GROUP BY " + REVENUE_LABELS_KEY + "." + REVENUE_LABEL_TEXT_KEY,
            nativeQuery = true
    )
    Set<RevenueLabel> getRevenueLabels(
            @Param(IDENTIFIER_KEY) String userId
    );

}
