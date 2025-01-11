package com.tecknobit.neutron.users.entity;

import com.tecknobit.equinoxbackend.environment.services.users.entity.EquinoxUser;
import com.tecknobit.neutroncore.enums.NeutronCurrency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
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
     * {@code CURRENCY_KEY} the key for the <b>"currency"</b> field
     */
    public static final String CURRENCY_KEY = "currency";

    /**
     * {@code currency} the currency of the user
     */
    @Enumerated(value = STRING)
    @Column(name = CURRENCY_KEY)
    private final NeutronCurrency currency;

    public NeutronUser() {
        this(null, null, null, null, null, null, null, null, null);
    }

    public NeutronUser(String id, String token, String name, String surname, String email, String password, String profilePic,
                       String language, NeutronCurrency currency) {
        super(id, token, name, surname, email, password, profilePic, language);
        this.currency = currency;
    }

    /**
     * Method to get {@link #currency} instance <br>
     * No-any params required
     *
     * @return {@link #currency} instance as {@link NeutronCurrency}
     */
    public NeutronCurrency getCurrency() {
        return currency;
    }

}
