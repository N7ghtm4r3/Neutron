package com.tecknobit.neutron.controllers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.neutron.helpers.resources.ResourcesManager;
import com.tecknobit.neutron.helpers.services.TransferHelper;
import com.tecknobit.neutroncore.records.TransferPayload;
import com.tecknobit.neutroncore.records.User;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.GET;
import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.POST;
import static com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.SUCCESSFUL;
import static com.tecknobit.neutroncore.helpers.Endpoints.TRANSFER_IN_ENDPOINT;
import static com.tecknobit.neutroncore.helpers.Endpoints.TRANSFER_OUT_ENDPOINT;
import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.*;

@RestController
public class TransferController extends NeutronController implements ResourcesManager {

    private final UsersController usersController;

    private final RevenuesController revenuesController;

    private final StepModel stepModel;

    private final TransferHelper transferHelper;

    public TransferController(UsersController usersController, RevenuesController revenuesController,
                              TransferHelper transferHelper) {
        this.usersController = usersController;
        this.revenuesController = revenuesController;
        this.transferHelper = transferHelper;
        stepModel = new StepModel();
    }

    @PostMapping(path = TRANSFER_IN_ENDPOINT)
    @RequestPath(path = "/api/v1/transferIn", method = POST)
    public <T> String transferIn(
            @RequestBody Map<String, T> payload
    ) {
        TransferPayload transferPayload = new TransferPayload(payload);
        if(!executeSignUp(transferPayload))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        if (!executeChangeCurrency(transferPayload))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        String userId = stepModel.getUserId();
        transferHelper.storeGeneralRevenues(transferPayload.getGeneralRevenues(), userId);
        transferHelper.storeProjectRevenues(transferPayload.getProjectRevenues(), userId);
        return successResponse(stepModel.toJSON());
    }

    private boolean executeSignUp(TransferPayload transferPayload) {
        String stepResponse = usersController.signUp(transferPayload.getSignUpPayload());
        if(stepFailed(stepResponse))
            return false;
        JsonHelper stepHelper = new JsonHelper(stepResponse);
        User user = transferPayload.getUserDetails();
        stepModel.setUserId(stepHelper.getString(IDENTIFIER_KEY));
        stepModel.setUserToken(stepHelper.getString(TOKEN_KEY));
        return true;
    }

    private boolean executeChangeCurrency(TransferPayload transferPayload) {
        String stepResponse = usersController.changeCurrency(
                stepModel.getUserId(),
                stepModel.getUserToken(),
                transferPayload.getChangeCurrencyPayload()
        );
        return !stepFailed(stepResponse);
    }

    @GetMapping(
            path = TRANSFER_OUT_ENDPOINT + "/{" + IDENTIFIER_KEY + "}",
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/transferOut/{id}", method = GET)
    public <T> T transferOut(
            @PathVariable(IDENTIFIER_KEY) String userId,
            @RequestHeader(TOKEN_KEY) String userToken
    ) {
        if (isMe(userId, userToken)) {
            T stepResponse = revenuesController.list(userId, userToken);
            if(stepFailed(stepResponse.toString()))
                return (T) failedResponse(WRONG_PROCEDURE_MESSAGE);
            transferHelper.deleteAfterTransferred(userId);
            return stepResponse;
        } else
            return (T) failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    private boolean stepFailed(String response) {
        return !response.contains(SUCCESSFUL.name());
    }

    private static final class StepModel {

        private String userId;

        private String userToken;

        private String profilePic;

        public StepModel() {
            profilePic = DEFAULT_PROFILE_PIC;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserToken() {
            return userToken;
        }

        public void setUserToken(String userToken) {
            this.userToken = userToken;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public JSONObject toJSON() {
            JSONObject transferModel = new JSONObject();
            transferModel.put(IDENTIFIER_KEY, userId);
            transferModel.put(TOKEN_KEY, userToken);
            transferModel.put(PROFILE_PIC_KEY, profilePic);
            clear();
            return transferModel;
        }

        private void clear() {
            userId = null;
            userToken = null;
            profilePic = DEFAULT_PROFILE_PIC;
        }

    }

}
