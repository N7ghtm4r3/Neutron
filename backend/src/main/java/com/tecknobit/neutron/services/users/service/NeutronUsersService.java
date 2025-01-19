package com.tecknobit.neutron.services.users.service;

import com.tecknobit.equinoxbackend.environment.services.users.service.EquinoxUsersHelper;
import com.tecknobit.neutron.services.revenues.service.RevenuesService;
import com.tecknobit.neutron.services.users.entity.NeutronUser;
import com.tecknobit.neutron.services.users.repository.NeutronUsersRepository;
import com.tecknobit.neutroncore.enums.NeutronCurrency;
import jakarta.persistence.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.*;
import static com.tecknobit.neutroncore.ContantsKt.CURRENCY_KEY;
import static com.tecknobit.neutroncore.ContantsKt.IDENTIFIER_KEY;

/**
 * The {@code NeutronUsersService} class is useful to manage all the user database operations
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Service
public class NeutronUsersService extends EquinoxUsersHelper<NeutronUser, NeutronUsersRepository> {

    @Deprecated(since = "REMOVE WHEN EQUINOX INTEGRATED")
    protected static final String SELECT_ = "SELECT ";

    @Deprecated(since = "REMOVE WHEN EQUINOX INTEGRATED")
    protected static final String _FROM_ = " FROM ";

    @Deprecated(since = "REMOVE WHEN EQUINOX INTEGRATED")
    protected static final List<String> DEFAULT_DYNAMIC_ACCOUNT_DATA_KEYS = List.of(EMAIL_KEY, PROFILE_PIC_KEY);

    /**
     * {@code revenuesService} instance for the revenues repository
     */
    @Autowired
    private RevenuesService revenuesService;

    @Deprecated(since = "REMOVE WHEN EQUINOX INTEGRATED")
    public JSONObject getDynamicAccountData(String userId) {
        StringBuilder queryBuilder = new StringBuilder(SELECT_);
        List<String> dynamicAccountDataKeys = getDynamicAccountDataKeys();
        arrangeQuery(queryBuilder, dynamicAccountDataKeys, false);
        queryBuilder.append(_FROM_ + USERS_KEY);
        queryBuilder.append(_WHERE_);
        queryBuilder.append(IDENTIFIER_KEY + "=").append(SINGLE_QUOTE).append(userId).append(SINGLE_QUOTE);
        Query query = entityManager.createNativeQuery(queryBuilder.toString());
        JSONArray result = new JSONArray(query.getResultList().get(0));
        JSONObject dynamicData = new JSONObject();
        for (int j = 0; j < result.length(); j++)
            dynamicData.put(dynamicAccountDataKeys.get(j), result.get(j));
        return dynamicData;
    }

    @Deprecated(since = "USE THE EQUINOX BUILT-IN")
    protected List<String> getDynamicAccountDataKeys() {
        ArrayList<String> keys = new ArrayList<>(DEFAULT_DYNAMIC_ACCOUNT_DATA_KEYS);
        keys.add(CURRENCY_KEY);
        return keys;
    }

    @Deprecated(since = "REMOVE WHEN EQUINOX INTEGRATED")
    private <E> void arrangeQuery(StringBuilder queryBuilder, List<E> list, boolean escape) {
        int listSize = list.size();
        int lastIndex = listSize - 1;
        for (int j = 0; j < listSize; j++) {
            if (escape)
                queryBuilder.append(SINGLE_QUOTE);
            queryBuilder.append(list.get(j));
            if (escape)
                queryBuilder.append(SINGLE_QUOTE);
            if (j < lastIndex)
                queryBuilder.append(COMMA);
        }
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
