package com.tecknobit.neutron.controllers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.neutroncore.records.TransferPayload;
import com.tecknobit.neutroncore.records.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.POST;
import static com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.SUCCESSFUL;
import static com.tecknobit.neutroncore.helpers.Endpoints.TRANSFER_IN_ENDPOINT;
import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.TOKEN_KEY;

@RestController
public class TransferController extends NeutronController {

    private final UsersController usersController;

    private final RevenuesController revenuesController;

    private JsonHelper stepHelper;

    public TransferController(UsersController usersController, RevenuesController revenuesController) {
        this.usersController = usersController;
        this.revenuesController = revenuesController;
    }

    @PostMapping(path = TRANSFER_IN_ENDPOINT)
    @RequestPath(path = "/api/v1/users/signUp", method = POST)
    public <T> String transferIn(
            @RequestBody Map<String, T> payload
    ) {
        TransferPayload transferPayload = new TransferPayload(payload);
        String signUpResponse = usersController.signUp(transferPayload.getSignUpPayload());
        if (stepSuccessfully(signUpResponse)) {
            stepHelper = new JsonHelper(signUpResponse);
            String userId = stepHelper.getString(IDENTIFIER_KEY);
            String userToken = stepHelper.getString(TOKEN_KEY);
            User user = transferPayload.getUserDetails();
            // TODO: MAKE THE REAL WORKFLOW
            // usersController.changeProfilePic();
        }
        //Map<String, T> userDetails = jsonHelper.getJSONObject()
        return null;
    }

    private boolean stepSuccessfully(String response) {
        return response.contains(SUCCESSFUL.name());
    }

}
