package com.tecknobit.neutron.services.users.repository;

import com.tecknobit.equinoxbackend.environment.services.users.repository.EquinoxUsersRepository;
import com.tecknobit.neutron.services.users.entity.NeutronUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import static com.tecknobit.equinoxbackend.environment.services.builtin.service.EquinoxItemsHelper._WHERE_;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.IDENTIFIER_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
import static com.tecknobit.neutroncore.ContantsKt.CURRENCY_KEY;

/**
 * The {@code NeutronUsersRepository} interface is useful to manage the queries for the users operations
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see JpaRepository
 * @see NeutronUser
 */
@Repository
public interface NeutronUsersRepository extends EquinoxUsersRepository<NeutronUser> {
    
    /**
     * Method to execute the query to change the currency of the {@link NeutronUser}
     *
     * @param newCurrency The new currency of the user
     * @param id The identifier of the user
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + USERS_KEY + " SET " +
                    CURRENCY_KEY + "=:" + CURRENCY_KEY +
                    _WHERE_ + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void changeCurrency(
            @Param(CURRENCY_KEY) String newCurrency,
            @Param(IDENTIFIER_KEY) String id
    );

    /**
     * Method to execute the query to delete the {@link NeutronUser} who requested a transfer from the current server
     *
     * @param id The identifier of the user
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + USERS_KEY + _WHERE_ + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteAfterTransferred(
            @Param(IDENTIFIER_KEY) String id
    );

}
