package com.tecknobit.neutron.services.users.controller;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.equinoxbackend.environment.services.users.controller.EquinoxUsersController;
import com.tecknobit.neutron.services.users.entity.NeutronUser;
import com.tecknobit.neutron.services.users.repository.NeutronUsersRepository;
import com.tecknobit.neutron.services.users.service.NeutronUsersService;
import com.tecknobit.neutroncore.helpers.NeutronInputsValidator;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.PATCH;
import static com.tecknobit.equinoxbackend.environment.services.builtin.entity.EquinoxItem.IDENTIFIER_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.TOKEN_KEY;
import static com.tecknobit.equinoxcore.helpers.CommonKeysKt.USERS_KEY;
import static com.tecknobit.neutroncore.ContantsKt.CURRENCY_KEY;
import static com.tecknobit.neutroncore.helpers.NeutronEndpoints.CHANGE_CURRENCY_ENDPOINT;
import static com.tecknobit.neutroncore.helpers.NeutronEndpoints.DYNAMIC_ACCOUNT_DATA_ENDPOINT;

/**
 * The {@code NeutronUsersController} class is useful to manage all the user operations
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see com.tecknobit.equinoxbackend.environment.services.builtin.controller.EquinoxController
 * @see EquinoxUsersController
 */
@RestController
public class NeutronUsersController extends EquinoxUsersController<NeutronUser, NeutronUsersRepository, NeutronUsersService> {

    /**
     * {@code WRONG_CURRENCY_MESSAGE} error message used when the currency inserted is not valid
     */
    public static final String WRONG_CURRENCY_MESSAGE = "wrong_currency_key";

    /**
     * {@inheritDoc}
     */
    @Override
    protected JSONObject assembleSignInSuccessResponse(NeutronUser user) {
        JSONObject response = super.assembleSignInSuccessResponse(user);
        response.put(CURRENCY_KEY, user.getCurrency());
        return response;
    }

    @Deprecated(since = "USE THE EQUINOX BUILT-IN")
    @GetMapping(
            path = USERS_KEY + "/{" + IDENTIFIER_KEY + "}" + DYNAMIC_ACCOUNT_DATA_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/dynamicAccountData", method = GET)
    public <T> T getDynamicAccountData(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token
    ) {
        if(!isMe(id, token))
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        return (T) successResponse(usersHelper.getDynamicAccountData(id));
    }

    /**
     * Method to change the currency of the user
     *
     * @param id The identifier of the user
     * @param token The token of the user
     * @param payload: payload of the request
     * <pre>
     *      {@code
     *              {
     *                  "currency": "the new currency of the user" -> [String]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
    @PatchMapping(
            path = USERS_KEY + "/{" + IDENTIFIER_KEY + "}" + CHANGE_CURRENCY_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/changeCurrency", method = PATCH)
    public String changeCurrency(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, String> payload
    ) {
        if(!isMe(id, token))
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
        loadJsonHelper(payload);
        String currency = jsonHelper.getString(CURRENCY_KEY);
        if(!NeutronInputsValidator.INSTANCE.isCurrencyValid(currency))
            return failedResponse(WRONG_CURRENCY_MESSAGE);
        try {
            usersHelper.changeCurrency(currency, me.getCurrency(), id);
            return successResponse();
        } catch (Exception e) {
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        }
    }

}
