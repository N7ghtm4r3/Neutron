package com.tecknobit.neutron.services.users.service;

import com.tecknobit.equinoxbackend.environment.services.users.entity.EquinoxUser;
import com.tecknobit.equinoxbackend.environment.services.users.service.EquinoxUsersService;
import com.tecknobit.neutron.services.revenues.service.RevenuesService;
import com.tecknobit.neutron.services.users.entity.NeutronUser;
import com.tecknobit.neutron.services.users.repository.NeutronUsersRepository;
import com.tecknobit.neutroncore.enums.NeutronCurrency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.tecknobit.neutroncore.ContantsKt.CURRENCY_KEY;

/**
 * The {@code NeutronUsersService} class is useful to manage all the user database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Service
public class NeutronUsersService extends EquinoxUsersService<NeutronUser, NeutronUsersRepository> {

    /**
     * {@code revenuesService} instance for the revenues repository
     */
    @Autowired
    private RevenuesService revenuesService;

    /**
     * Method used to get the list of keys to use in the {@link #getDynamicAccountData(String)} method
     *
     * @return a list of keys as {@link List} of {@link String}
     * @apiNote This method allows a customizable retrieving of custom parameters added in a customization of the {@link EquinoxUser}
     */
    @Override
    protected List<String> getDynamicAccountDataKeys() {
        List<String> keys = new ArrayList<>(super.getDynamicAccountDataKeys());
        keys.add(CURRENCY_KEY);
        return keys;
    }

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
