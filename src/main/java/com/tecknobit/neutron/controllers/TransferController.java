package com.tecknobit.neutron.controllers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.apimanager.formatters.JsonHelper;
import com.tecknobit.neutron.helpers.resources.ResourcesManager;
import com.tecknobit.neutron.helpers.services.TransferHelper;
import com.tecknobit.neutroncore.records.TransferPayload;
import com.tecknobit.neutroncore.records.User;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.POST;
import static com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.SUCCESSFUL;
import static com.tecknobit.neutroncore.helpers.Endpoints.TRANSFER_IN_ENDPOINT;
import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.*;

@RestController
public class TransferController extends NeutronController implements ResourcesManager {

    private final UsersController usersController;

    private final StepModel stepModel;

    private final TransferHelper transferHelper;

    public TransferController(UsersController usersController, TransferHelper transferHelper) {
        this.usersController = usersController;
        this.transferHelper = transferHelper;
        stepModel = new StepModel();
    }

    @PostMapping(path = TRANSFER_IN_ENDPOINT)
    @RequestPath(path = "/api/v1/transferIn", method = POST)
    public <T> String transferIn(
            @RequestBody Map<String, T> payload,
            @RequestParam(value = PROFILE_PIC_KEY, required = false) MultipartFile profilePic
    ) {
        TransferPayload transferPayload = new TransferPayload(payload);
        if(!executeSignUp(transferPayload))
            return failedResponse(WRONG_PROCEDURE_MESSAGE);
        if(!setProfilePic(profilePic))
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

    private boolean setProfilePic(MultipartFile profilePic) {
        if(profilePic != null && !profilePic.isEmpty()) {
            String stepResponse = usersController.changeProfilePic(
                    stepModel.getUserId(),
                    stepModel.getUserToken(),
                    profilePic
            );
            if(stepFailed(stepResponse))
                return false;
            JsonHelper stepHelper = new JsonHelper(stepResponse);
            stepModel.setProfilePic(jsonHelper.getString(PROFILE_PIC_KEY));
        }
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
