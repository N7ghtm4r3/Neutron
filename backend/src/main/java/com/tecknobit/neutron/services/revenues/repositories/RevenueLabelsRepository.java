package com.tecknobit.neutron.services.revenues.repositories;

import com.tecknobit.neutron.services.revenues.entities.RevenueLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

import static com.tecknobit.equinoxbackend.apis.database.SQLConstants._WHERE_;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.IDENTIFIER_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.OWNER_KEY;
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
            value = "SELECT " + LABELS_KEY + ".* FROM " + LABELS_KEY + " AS " + LABELS_KEY +
                    " INNER JOIN " + GENERAL_REVENUES_KEY + " AS " + GENERAL_REVENUES_KEY +
                    " ON " + GENERAL_REVENUES_KEY + "." + OWNER_KEY + "=:" + IDENTIFIER_KEY +
                    " INNER JOIN " + REVENUE_LABELS_KEY + " AS rv ON rv." + REVENUE_IDENTIFIER_KEY + "=" +
                    GENERAL_REVENUES_KEY + "." + IDENTIFIER_KEY +
                    _WHERE_ + LABELS_KEY + "." + IDENTIFIER_KEY + "=rv." + IDENTIFIER_KEY,
            nativeQuery = true
    )
    Set<RevenueLabel> getRevenueLabels(
            @Param(IDENTIFIER_KEY) String userId
    );

    /**
     * Method to count the current relationship of a shared label
     *
     * @param labelId The identifier of the label to check
     *
     * @return the count the current relationship of a shared label as long
     */
    @Query(
            value = "SELECT COUNT(*) FROM " + REVENUE_LABELS_KEY +
                    _WHERE_ + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    long countSharedLabels(
            @Param(IDENTIFIER_KEY) String labelId
    );

}
