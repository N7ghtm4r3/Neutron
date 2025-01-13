package com.tecknobit.neutron.services.users.service;

import com.tecknobit.equinoxbackend.environment.services.users.service.EquinoxUsersHelper;
import com.tecknobit.neutron.services.revenues.service.RevenuesService;
import com.tecknobit.neutron.services.users.entity.NeutronUser;
import com.tecknobit.neutron.services.users.repository.NeutronUsersRepository;
import com.tecknobit.neutroncore.enums.NeutronCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The {@code NeutronUsersService} class is useful to manage all the user database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Service
public class NeutronUsersService extends EquinoxUsersHelper<NeutronUser, NeutronUsersRepository> {

    /**
     * {@code revenuesService} instance for the revenues repository
     */
    @Autowired
    private RevenuesService revenuesService;

    /**
     * Method to change the currency of the {@link NeutronUser}
     *
     * @param newCurrency The new currency of the user
     * @param oldCurrency The current currency of the user
     * @param userId The identifier of the user
     */
    public void changeCurrency(String newCurrency, NeutronCurrency oldCurrency, String userId) {
        NeutronCurrency newCurrencyValue = NeutronCurrency.valueOf(newCurrency);
        if(newCurrencyValue != oldCurrency) {
            usersRepository.changeCurrency(newCurrency, userId);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> revenuesService.convertRevenues(userId, oldCurrency, newCurrencyValue));
        }
    }

}
