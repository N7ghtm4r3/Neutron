package com.tecknobit.neutron.controllers;


import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.neutron.helpers.services.RevenuesHelper;
import org.springframework.web.bind.annotation.*;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.neutroncore.helpers.Endpoints.BASE_ENDPOINT;
import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.TOKEN_KEY;
import static com.tecknobit.neutroncore.records.User.USERS_KEY;
import static com.tecknobit.neutroncore.records.revenues.Revenue.REVENUES_KEY;

@RestController
@RequestMapping(BASE_ENDPOINT + USERS_KEY + "/{" + IDENTIFIER_KEY + "}/" + REVENUES_KEY)
public class RevenuesController extends NeutronController {

    private final RevenuesHelper revenuesHelper;

    public RevenuesController(RevenuesHelper revenuesHelper) {
        this.revenuesHelper = revenuesHelper;
    }

    @GetMapping(
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/revenues", method = GET)
    public <T> T list(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(isMe(userId, token))
            return (T) successResponse(revenuesHelper.getRevenues(userId));
        else
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }


}
