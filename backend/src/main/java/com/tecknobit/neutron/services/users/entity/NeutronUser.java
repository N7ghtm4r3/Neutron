package com.tecknobit.neutron.services.users.entity;

import com.tecknobit.equinoxbackend.environment.services.users.entity.EquinoxUser;
import com.tecknobit.neutroncore.enums.NeutronCurrency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
import static com.tecknobit.neutroncore.ContantsKt.CURRENCY_KEY;
import static jakarta.persistence.EnumType.STRING;

/**
 * The {@code NeutronUser} class is useful to represent a Neutron's user
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem
 * @see EquinoxUser
 */
@Entity
@Table(name = USERS_KEY)
public class NeutronUser extends EquinoxUser {

    /**
     * {@code currency} the currency of the user
     */
    @Enumerated(value = STRING)
    @Column(
            name = CURRENCY_KEY,
            columnDefinition = "TEXT DEFAULT 'DOLLAR'",
            insertable = false
    )
    private final NeutronCurrency currency;

    /**
     * Constructor to init the {@link EquinoxUser} class <br>
     *
     * @apiNote empty constructor required
     */
    public NeutronUser() {
        this(null, null, null, null, null, null, null, null, null);
    }

    /**
     * Constructor to init the {@link EquinoxUser} class
     *
     * @param id Identifier of the user
     * @param token The token which the user is allowed to operate on server
     * @param name The password of the user
     * @param surname The surname of the user
     * @param email The password of the user
     * @param password The password of the user
     * @param profilePic The profile pic of the user
     * @param language The password of the user
     * @param currency The currency of the user
     */
    public NeutronUser(String id, String token, String name, String surname, String email, String password, String profilePic,
                       String language, NeutronCurrency currency) {
        super(id, token, name, surname, email, password, profilePic, language);
        this.currency = currency;
    }

    /**
     * Method to get {@link #currency} instance 
     *
     * @return {@link #currency} instance as {@link NeutronCurrency}
     */
    public NeutronCurrency getCurrency() {
        return currency;
    }

}
