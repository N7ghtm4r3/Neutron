package com.tecknobit.neutron.helpers.services.repositories;

import com.tecknobit.neutroncore.records.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.*;

@Service
@Repository
public interface UsersRepository extends JpaRepository<User, String> {

    /**
     * Method to execute the query to find a {@link User} by email field
     *
     * @param email: the email to find the user
     * @return the user, if exists, as {@link User}
     */
    @Query(
            value = "SELECT * FROM " + USERS_KEY + " WHERE " + EMAIL_KEY + "=:" + EMAIL_KEY,
            nativeQuery = true
    )
    User findUserByEmail(
            @Param(EMAIL_KEY) String email
    );

    /**
     * Method to execute the query to change the profile pic of the {@link User}
     *
     * @param profilePicUrl: the profile pic formatted as url
     * @param id: the identifier of the user
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + USERS_KEY + " SET " + PROFILE_PIC_KEY + "=:" + PROFILE_PIC_KEY + " WHERE "
                    + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void changeProfilePic(
            @Param(PROFILE_PIC_KEY) String profilePicUrl,
            @Param(IDENTIFIER_KEY) String id
    );

    /**
     * Method to execute the query to change the email of the {@link User}
     *
     * @param newEmail: the new email of the user
     * @param id: the identifier of the user
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + USERS_KEY + " SET " + EMAIL_KEY + "=:" + EMAIL_KEY + " WHERE "
                    + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void changeEmail(
            @Param(EMAIL_KEY) String newEmail,
            @Param(IDENTIFIER_KEY) String id
    );

    /**
     * Method to execute the query to change the password of the {@link User}
     *
     * @param newPassword: the new password of the user
     * @param id: the identifier of the user
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + USERS_KEY + " SET " + PASSWORD_KEY + "=:" + PASSWORD_KEY + " WHERE "
                    + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void changePassword(
            @Param(PASSWORD_KEY) String newPassword,
            @Param(IDENTIFIER_KEY) String id
    );

    /**
     * Method to execute the query to change the language of the {@link User}
     *
     * @param newLanguage: the new language of the user
     * @param id: the identifier of the user
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + USERS_KEY + " SET " + LANGUAGE_KEY + "=:" + LANGUAGE_KEY + " WHERE "
                    + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void changeLanguage(
            @Param(LANGUAGE_KEY) String newLanguage,
            @Param(IDENTIFIER_KEY) String id
    );

    /**
     * Method to execute the query to change the currency of the {@link User}
     *
     * @param newCurrency: the new currency of the user
     * @param id: the identifier of the user
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE " + USERS_KEY + " SET " + CURRENCY_KEY + "=:" + CURRENCY_KEY + " WHERE "
                    + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void changeCurrency(
            @Param(CURRENCY_KEY) String newCurrency,
            @Param(IDENTIFIER_KEY) String id
    );

    /**
     * Method to execute the query to delete the {@link User} who requested a transfer from the current server
     *
     * @param id: the identifier of the user
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "DELETE FROM " + USERS_KEY + " WHERE " + IDENTIFIER_KEY + "=:" + IDENTIFIER_KEY,
            nativeQuery = true
    )
    void deleteAfterTransferred(
            @Param(IDENTIFIER_KEY) String id
    );

}
