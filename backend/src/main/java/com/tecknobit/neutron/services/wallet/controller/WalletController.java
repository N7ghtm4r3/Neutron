package com.tecknobit.neutron.services.wallet.controller;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinoxbackend.environment.services.DefaultEquinoxController;
import com.tecknobit.neutron.services.DefaultNeutronController;
import com.tecknobit.neutron.services.wallet.service.WalletService;
import com.tecknobit.neutroncore.enums.RevenuePeriod;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.TOKEN_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.neutroncore.ContantsKt.*;
import static com.tecknobit.neutroncore.helpers.NeutronEndpoints.WALLET_ENDPOINT;

/**
 * The {@code WalletController} class is useful to manage all the operations on the user wallet
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see DefaultEquinoxController
 * @see DefaultNeutronController
 */
@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + IDENTIFIER_KEY + "}" + WALLET_ENDPOINT)
public class WalletController extends DefaultNeutronController {

    /**
     * {@code walletService} helper to manage the wallet database operations
     */
    @Autowired
    private WalletService walletService;

    /**
     * Method to get the wallet status of the user
     *
     * @param userId The identifier of the user
     * @param token The token of the user
     * @param period The period to use to select the revenues
     * @param retrieveGeneralRevenues Whether include the {@link com.tecknobit.neutron.services.revenues.entities.GeneralRevenue}
     * @param retrieveProjectRevenues Whether include the {@link com.tecknobit.neutron.services.revenues.entities.ProjectRevenue}
     * @param labels The labels used to filter the data
     *
     * @return the result of the request as {@link String}
     */
    @GetMapping(
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/wallet", method = GET)
    public <T> T getWalletStatus(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestParam(name = REVENUE_PERIOD_KEY, defaultValue = "LAST_MONTH", required = false) String period,
            @RequestParam(name = GENERAL_REVENUES_KEY, defaultValue = "true", required = false) boolean retrieveGeneralRevenues,
            @RequestParam(name = PROJECT_REVENUES_KEY, defaultValue = "true", required = false) boolean retrieveProjectRevenues,
            @RequestParam(name = LABELS_KEY, required = false) JSONArray labels
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        RevenuePeriod revenuePeriod = RevenuePeriod.Companion.toRevenuePeriod(period);
        return (T) successResponse(walletService.getWalletStatus(userId, revenuePeriod, retrieveGeneralRevenues,
                retrieveProjectRevenues, labels));
    }

}
