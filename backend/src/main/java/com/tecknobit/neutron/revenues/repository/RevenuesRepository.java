package com.tecknobit.neutron.revenues.repository;


import com.tecknobit.neutron.revenues.entities.GeneralRevenue;
import com.tecknobit.neutron.revenues.entities.ProjectRevenue;
import com.tecknobit.neutron.revenues.entities.Revenue;
import com.tecknobit.neutron.revenues.entities.TicketRevenue;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.neutron.revenues.entities.GeneralRevenue.GENERAL_REVENUES_KEY;
import static com.tecknobit.neutron.revenues.entities.GeneralRevenue.REVENUE_DESCRIPTION_KEY;
import static com.tecknobit.neutron.revenues.entities.InitialRevenue.INITIAL_REVENUES_KEY;
import static com.tecknobit.neutron.revenues.entities.ProjectRevenue.PROJECT_REVENUES_KEY;
import static com.tecknobit.neutron.revenues.entities.ProjectRevenue.PROJECT_REVENUE_KEY;
import static com.tecknobit.neutron.revenues.entities.Revenue.*;
import static com.tecknobit.neutron.revenues.entities.TicketRevenue.CLOSING_DATE_KEY;

/**
 * The {@code RevenuesRepository} interface is useful to manage the queries for the revenues operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see Revenue
 */
@Service
@Repository
public interface RevenuesRepository extends JpaRepository<Revenue, String> {


    /**
     * Method to count the general revenues of a user
     *
     * @param userId The user identifier
     *
     * @return the count of the revenues long
     */
    @Query(
            value = "SELECT COUNT(*) FROM " + GENERAL_REVENUES_KEY + " WHERE " + OWNER_KEY + "=:" + IDENTIFIER_KEY
                    + " AND dtype='general'",
            nativeQuery = true
    )
    long countGeneralRevenues(
            @Param(IDENTIFIER_KEY) String userId
    );
    
    /**
     * Method to get the general revenues of a user
     *
     * @param userId The user identifier
     * @param pageable  The parameters to paginate the query
     *
     * @return the revenues as {@link List} of {@link GeneralRevenue}
     */
    @Query(
            value = "SELECT * FROM " + GENERAL_REVENUES_KEY + " WHERE " + OWNER_KEY + "=:" + IDENTIFIER_KEY
                    + " AND dtype='general'"
                    + " ORDER BY " + REVENUE_DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<GeneralRevenue> getGeneralRevenues(
            @Param(IDENTIFIER_KEY) String userId,
            Pageable pageable
    );

    /**
     * Method to get a general revenue if exists
     *
     * @param userId The identifier of the user
     * @param revenueTitle The title of the revenue to get
     * @return a general revenue if exists as {@link GeneralRevenue}
     */
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

    /**
     * Method to get a general revenue if exists
     *
     * @param userId The identifier of the user
     * @param revenueId The identifier of the revenue to get
     * @return a general revenue if exists as {@link GeneralRevenue}
     */
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

    /**
     * Method to store a new general revenue
     *
     * @param revenueId The identifier of the new revenue
     * @param revenueTitle The title of the revenue
     * @param insertionDate The date when the revenue has been created/inserted
     * @param value The value of the revenue
     * @param revenueDescription The description of the revenue
     * @param ownerId The identifier of the owner of the revenue created
     */
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

    /**
     * Method to delete a general revenue
     *
     * @param ownerId The identifier of the owner
     * @param revenueId The identifier of the revenue to delete
     */
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

    /**
     * Method to convert a general revenue value
     *
     * @param revenueId The identifier of the revenue to convert
     * @param value The value converted to set
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + GENERAL_REVENUES_KEY
                    + " SET " + REVENUE_VALUE_KEY + "=:" + REVENUE_VALUE_KEY
                    + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void convertGeneralRevenue(
            @Param(IDENTIFIER_KEY) String revenueId,
            @Param(REVENUE_VALUE_KEY) double value
    );

    /**
     * Method to count the projects of a user
     *
     * @param userId The user identifier
     *
     * @return the count the projects of a user as long
     */
    @Query(
            value = "SELECT COUNT(*) FROM " + PROJECT_REVENUES_KEY + " WHERE " + OWNER_KEY + "=:" + IDENTIFIER_KEY
                    + " ORDER BY " + REVENUE_DATE_KEY + " DESC",
            nativeQuery = true
    )
    long countProjectRevenues(
            @Param(IDENTIFIER_KEY) String userId
    );

    /**
     * Method to get the projects of a user
     *
     * @param userId The user identifier
     * @param pageable  The parameters to paginate the query
     * @return the revenues as {@link List} of {@link ProjectRevenue}
     */
    @Query(
            value = "SELECT * FROM " + PROJECT_REVENUES_KEY + " WHERE " + OWNER_KEY + "=:" + IDENTIFIER_KEY
                    + " ORDER BY " + REVENUE_DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<ProjectRevenue> getProjectRevenues(
            @Param(IDENTIFIER_KEY) String userId,
            Pageable pageable
    );

    /**
     * Method to get a project if exists
     *
     * @param userId The identifier of the user
     * @param revenueTitle The title of the project to get
     * @return a project as {@link ProjectRevenue}
     */
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

    /**
     * Method to get a project if exists
     *
     * @param userId The identifier of the user
     * @param revenueId The identifier of the project to get
     * @return a project as {@link ProjectRevenue}
     */
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

    /**
     * Method to store a new project revenue
     *
     * @param revenueId The identifier of the new project
     * @param revenueTitle The title of the project
     * @param insertionDate The date when the project has been created/inserted
     * @param ownerId The identifier of the owner of the project created
     */
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

    /**
     * Method to store a new initial revenue
     *
     * @param revenueId The identifier of the new project
     * @param insertionDate The date when the project has been created/inserted
     * @param revenueTitle The title of the project
     * @param value The initial amount value
     * @param ownerId The identifier of the owner of the project created
     * @param projectRevenue The identifier of the project where the initial revenue is attached
     */
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

    /**
     * Method to store a new ticket revenue
     *
     * @param ticketId The identifier of the new ticket
     * @param ticketRevenue The amount value of the ticket
     * @param ticketTitle The title of the ticket
     * @param ticketDescription The description of the ticket
     * @param openingTime The date when the ticket has been opened/inserted
     * @param closingTime The date when the ticket has been closed
     * @param projectRevenueId The identifier of the project where the ticket revenue is attached
     * @param ownerId The identifier of the owner of the project
     */
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

    /**
     * Method to get a ticket revenue
     *
     * @param ticketId The identifier of the ticket
     * @param revenueId The identifier of the project where the ticket revenue is attached
     * @param ownerId The identifier of the owner of the project
     * @return the ticket as {@link TicketRevenue}
     */
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

    /**
     * Method to close a ticket revenue
     *
     * @param ticketId The identifier of the ticket
     * @param ownerId The identifier of the owner of the project
     * @param revenueId The identifier of the project where the ticket revenue is attached
     * @param closingTime The date when the ticket has been closed
     */
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
            @Param(CLOSING_DATE_KEY) long closingTime
    );

    /**
     * Method to delete a ticket revenue
     *
     * @param ticketId The identifier of the ticket
     */
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

    /**
     * Method to delete a revenue
     *
     * @param revenueId The identifier of the revenue
     * @param ownerId The identifier of the owner of the revenue
     */
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

    /**
     * Method to convert an initial revenue value
     *
     * @param revenueId The identifier of the revenue to convert
     * @param value The value converted to set
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + INITIAL_REVENUES_KEY
                    + " SET " + REVENUE_VALUE_KEY + "=:" + REVENUE_VALUE_KEY
                    + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void convertInitialRevenue(
            @Param(IDENTIFIER_KEY) String revenueId,
            @Param(REVENUE_VALUE_KEY) double value
    );

}
