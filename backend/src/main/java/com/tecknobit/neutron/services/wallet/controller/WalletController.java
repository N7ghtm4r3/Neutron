package com.tecknobit.neutron.services.wallet.controller;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.neutron.services.DefaultNeutronController;
import com.tecknobit.neutron.services.wallet.service.WalletService;
import com.tecknobit.neutroncore.enums.RevenuePeriod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.TOKEN_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
import static com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet.BASE_EQUINOX_ENDPOINT;
import static com.tecknobit.neutroncore.ContantsKt.REVENUE_LABELS_KEY;
import static com.tecknobit.neutroncore.ContantsKt.REVENUE_PERIOD_KEY;
import static com.tecknobit.neutroncore.helpers.NeutronEndpoints.WALLET_ENDPOINT;

@RestController
@RequestMapping(BASE_EQUINOX_ENDPOINT + USERS_KEY + "/{" + IDENTIFIER_KEY + "}" + WALLET_ENDPOINT)
public class WalletController extends DefaultNeutronController {

    @Autowired
    private WalletService walletService;

    @GetMapping(
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/wallet", method = GET)
    public <T> T getWalletStatus(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestParam(name = REVENUE_PERIOD_KEY, defaultValue = "LAST_MONTH", required = false) RevenuePeriod period,
            @RequestParam(name = REVENUE_LABELS_KEY, required = false) HashSet<String> labels
    ) {
        if(!isMe(userId, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(walletService.getWalletStatus(userId, period, labels));
    }

}
