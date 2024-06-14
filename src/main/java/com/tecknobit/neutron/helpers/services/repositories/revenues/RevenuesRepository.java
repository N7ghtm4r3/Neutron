package com.tecknobit.neutron.helpers.services.repositories.revenues;


import com.tecknobit.neutroncore.records.revenues.GeneralRevenue;
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue;
import com.tecknobit.neutroncore.records.revenues.Revenue;
import com.tecknobit.neutroncore.records.revenues.TicketRevenue;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.OWNER_KEY;
import static com.tecknobit.neutroncore.records.revenues.GeneralRevenue.GENERAL_REVENUES_KEY;
import static com.tecknobit.neutroncore.records.revenues.GeneralRevenue.REVENUE_DESCRIPTION_KEY;
import static com.tecknobit.neutroncore.records.revenues.InitialRevenue.INITIAL_REVENUES_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECT_REVENUES_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECT_REVENUE_KEY;
import static com.tecknobit.neutroncore.records.revenues.Revenue.*;
import static com.tecknobit.neutroncore.records.revenues.TicketRevenue.CLOSING_DATE_KEY;

@Service
@Repository
public interface RevenuesRepository extends JpaRepository<Revenue, String> {

    @Query(
            value = "SELECT * FROM " + GENERAL_REVENUES_KEY + " WHERE " + OWNER_KEY + "=:" + IDENTIFIER_KEY
                    + " AND dtype='general'"
                    + " ORDER BY " + REVENUE_DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<GeneralRevenue> getGeneralRevenues(
            @Param(IDENTIFIER_KEY) String userId
    );

    @Query(
            value = "SELECT * FROM " + GENERAL_REVENUES_KEY + " WHERE "
                    + OWNER_KEY + "=:" + OWNER_KEY + " AND "
                    + REVENUE_TITLE_KEY + "=:" + REVENUE_TITLE_KEY,
            nativeQuery = true
    )
    GeneralRevenue generalRevenueExists(
            @Param(OWNER_KEY) String userId,
            @Param(REVENUE_TITLE_KEY) String revenueTitle
    );

    @Query(
            value = "SELECT * FROM " + GENERAL_REVENUES_KEY + " WHERE "
                    + OWNER_KEY + "=:" + OWNER_KEY + " AND "
                    + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    GeneralRevenue generalRevenueExistsById(
            @Param(OWNER_KEY) String userId,
            @Param(IDENTIFIER_KEY) String revenueId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + GENERAL_REVENUES_KEY + " WHERE "
                    + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY + " AND "
                    + OWNER_KEY + "=:" + OWNER_KEY,
            nativeQuery = true
    )
    void deleteGeneralRevenue(
            @Param(IDENTIFIER_KEY) String revenueId,
            @Param(OWNER_KEY) String ownerId
    );

    @Query(
            value = "SELECT * FROM " + PROJECT_REVENUES_KEY + " WHERE " + OWNER_KEY + "=:" + IDENTIFIER_KEY
                    + " ORDER BY " + REVENUE_DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<ProjectRevenue> getProjectRevenues(
            @Param(IDENTIFIER_KEY) String userId
    );

    @Query(
            value = "SELECT * FROM " + PROJECT_REVENUES_KEY + " WHERE "
                    + OWNER_KEY + "=:" + IDENTIFIER_KEY + " AND "
                    + REVENUE_TITLE_KEY + "=:" + REVENUE_TITLE_KEY,
            nativeQuery = true
    )
    ProjectRevenue projectRevenueExists(
            @Param(IDENTIFIER_KEY) String userId,
            @Param(REVENUE_TITLE_KEY) String revenueTitle
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + PROJECT_REVENUES_KEY + " (" +
                        IDENTIFIER_KEY + "," +
                        REVENUE_TITLE_KEY + "," +
                        REVENUE_DATE_KEY + "," +
                        OWNER_KEY +
                    ") " +
                    "VALUES (" +
                        ":" + IDENTIFIER_KEY + "," +
                        ":" + REVENUE_TITLE_KEY + "," +
                        ":" + REVENUE_DATE_KEY + "," +
                        ":" + OWNER_KEY +
                    ");",
            nativeQuery = true
    )
    void insertProjectRevenue(
            @Param(IDENTIFIER_KEY) String revenueId,
            @Param(REVENUE_TITLE_KEY) String revenueTitle,
            @Param(REVENUE_DATE_KEY) long insertionDate,
            @Param(OWNER_KEY) String ownerId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + INITIAL_REVENUES_KEY + " (" +
                        IDENTIFIER_KEY + "," +
                        REVENUE_DATE_KEY + "," +
                        REVENUE_TITLE_KEY + "," +
                        REVENUE_VALUE_KEY + "," +
                        OWNER_KEY + "," +
                        PROJECT_REVENUE_KEY +
                    ") " +
                    "VALUES (" +
                        ":" + IDENTIFIER_KEY + "," +
                        ":" + REVENUE_DATE_KEY + "," +
                        ":" + REVENUE_TITLE_KEY + "," +
                        ":" + REVENUE_VALUE_KEY + "," +
                        ":" + OWNER_KEY + "," +
                        ":" + PROJECT_REVENUE_KEY
                    + ");",
            nativeQuery = true
    )
    void insertInitialRevenue(
            @Param(IDENTIFIER_KEY) String revenueId,
            @Param(REVENUE_DATE_KEY) long insertionDate,
            @Param(REVENUE_TITLE_KEY) String revenueTitle,
            @Param(REVENUE_VALUE_KEY) double value,
            @Param(OWNER_KEY) String ownerId,
            @Param(PROJECT_REVENUE_KEY) String projectRevenue
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + GENERAL_REVENUES_KEY + " (" +
                        "dType" + "," +
                        IDENTIFIER_KEY + "," +
                    REVENUE_TITLE_KEY + "," +
                        REVENUE_DATE_KEY + "," +
                        REVENUE_VALUE_KEY + "," +
                        REVENUE_DESCRIPTION_KEY + "," +
                        OWNER_KEY +
                    ") " +
                    "VALUES (" +
                        "'general'" + "," +
                        ":" + IDENTIFIER_KEY + "," +
                    ":" + REVENUE_TITLE_KEY + "," +
                        ":" + REVENUE_DATE_KEY + "," +
                        ":" + REVENUE_VALUE_KEY + "," +
                        ":" + REVENUE_DESCRIPTION_KEY + "," +
                        ":" + OWNER_KEY +
                    ");",
            nativeQuery = true
    )
    void insertGeneralRevenue(
            @Param(IDENTIFIER_KEY) String revenueId,
            @Param(REVENUE_TITLE_KEY) String revenueTitle,
            @Param(REVENUE_DATE_KEY) long insertionDate,
            @Param(REVENUE_VALUE_KEY) double value,
            @Param(REVENUE_DESCRIPTION_KEY) String revenueDescription,
            @Param(OWNER_KEY) String ownerId
    );

    @Query(
            value = "SELECT * FROM " + PROJECT_REVENUES_KEY + " WHERE "
                    + OWNER_KEY + "=:" + OWNER_KEY + " AND "
                    + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    ProjectRevenue projectRevenueExistsById(
            @Param(OWNER_KEY) String userId,
            @Param(IDENTIFIER_KEY) String revenueId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "INSERT INTO " + GENERAL_REVENUES_KEY + " (" +
                    "dType" + "," +
                    IDENTIFIER_KEY + "," +
                    REVENUE_VALUE_KEY + "," +
                    REVENUE_TITLE_KEY + "," +
                    REVENUE_DESCRIPTION_KEY + "," +
                    REVENUE_DATE_KEY + "," +
                    CLOSING_DATE_KEY + "," +
                    PROJECT_REVENUE_KEY + "," +
                    OWNER_KEY +
                    ") " +
                    "VALUES (" +
                    "'ticket'" + "," +
                    ":" + IDENTIFIER_KEY + "," +
                    ":" + REVENUE_VALUE_KEY + "," +
                    ":" + REVENUE_TITLE_KEY + "," +
                    ":" + REVENUE_DESCRIPTION_KEY + "," +
                    ":" + REVENUE_DATE_KEY + "," +
                    ":" + CLOSING_DATE_KEY + "," +
                    ":" + PROJECT_REVENUE_KEY + "," +
                    ":" + OWNER_KEY +
                    ");",
            nativeQuery = true
    )
    void addTicketToProjectRevenue(
            @Param(IDENTIFIER_KEY) String ticketId,
            @Param(REVENUE_VALUE_KEY) double ticketRevenue,
            @Param(REVENUE_TITLE_KEY) String ticketTitle,
            @Param(REVENUE_DESCRIPTION_KEY) String ticketDescription,
            @Param(REVENUE_DATE_KEY) long openingTime,
            @Param(CLOSING_DATE_KEY) long closingTime,
            @Param(PROJECT_REVENUE_KEY) String projectRevenueId,
            @Param(OWNER_KEY) String ownerId
    );

    @Query(
            value = "SELECT * FROM " + GENERAL_REVENUES_KEY + " WHERE "
                    + "dtype='ticket' AND "
                    + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY + " AND "
                    + OWNER_KEY + "=:" + OWNER_KEY + " AND "
                    + PROJECT_REVENUE_KEY + "=:" + PROJECT_REVENUE_KEY,
            nativeQuery = true
    )
    TicketRevenue getTicketRevenue(
            @Param(IDENTIFIER_KEY) String ticketId,
            @Param(OWNER_KEY) String ownerId,
            @Param(PROJECT_REVENUE_KEY) String revenueId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + GENERAL_REVENUES_KEY
                    + " SET " + CLOSING_DATE_KEY + "=:" + CLOSING_DATE_KEY
                    + " WHERE " + "dtype='ticket' AND "
                    + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY + " AND "
                    + OWNER_KEY + "=:" + OWNER_KEY + " AND "
                    + PROJECT_REVENUE_KEY + "=:" + PROJECT_REVENUE_KEY,
            nativeQuery = true
    )
    void closeTicketRevenue(
            @Param(IDENTIFIER_KEY) String ticketId,
            @Param(OWNER_KEY) String ownerId,
            @Param(PROJECT_REVENUE_KEY) String revenueId,
            @Param(CLOSING_DATE_KEY) long closingDate
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + GENERAL_REVENUES_KEY + " WHERE " + "dtype='ticket' AND "
                    + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteTicketRevenue(
            @Param(IDENTIFIER_KEY) String ticketId
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + PROJECT_REVENUES_KEY + " WHERE "
                    + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY + " AND "
                    + OWNER_KEY + "=:" + OWNER_KEY,
            nativeQuery = true
    )
    void deleteProjectRevenue(
            @Param(IDENTIFIER_KEY) String revenueId,
            @Param(OWNER_KEY) String ownerId
    );

}
