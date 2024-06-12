package com.tecknobit.neutron.helpers.services.repositories;


import com.tecknobit.neutroncore.records.revenues.GeneralRevenue;
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue;
import com.tecknobit.neutroncore.records.revenues.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.OWNER_KEY;
import static com.tecknobit.neutroncore.records.revenues.GeneralRevenue.GENERAL_REVENUES_KEY;
import static com.tecknobit.neutroncore.records.revenues.ProjectRevenue.PROJECT_REVENUES_KEY;
import static com.tecknobit.neutroncore.records.revenues.Revenue.REVENUE_DATE_KEY;

@Service
@Repository
public interface RevenuesRepository extends JpaRepository<Revenue, String> {

    @Query(
            value = "SELECT * FROM " + GENERAL_REVENUES_KEY + " WHERE " + OWNER_KEY + "=:" + IDENTIFIER_KEY
                    + " ORDER BY " + REVENUE_DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<GeneralRevenue> getGeneralRevenues(
            @Param(IDENTIFIER_KEY) String userId
    );

    @Query(
            value = "SELECT * FROM " + PROJECT_REVENUES_KEY + " WHERE " + OWNER_KEY + "=:" + IDENTIFIER_KEY
                    + " ORDER BY " + REVENUE_DATE_KEY + " DESC",
            nativeQuery = true
    )
    List<ProjectRevenue> getProjectRevenues(
            @Param(IDENTIFIER_KEY) String userId
    );

}
