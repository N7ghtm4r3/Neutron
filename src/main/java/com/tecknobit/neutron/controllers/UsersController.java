package com.tecknobit.neutron.controllers;

import com.tecknobit.apimanager.annotations.RequestPath;
import com.tecknobit.neutron.helpers.services.UsersHelper;
import com.tecknobit.neutroncore.records.User;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.PATCH;
import static com.tecknobit.apimanager.apis.APIRequest.RequestMethod.POST;
import static com.tecknobit.apimanager.apis.ServerProtector.SERVER_SECRET_KEY;
import static com.tecknobit.neutron.Launcher.serverProtector;
import static com.tecknobit.neutroncore.helpers.Endpoints.*;
import static com.tecknobit.neutroncore.helpers.InputValidator.*;
import static com.tecknobit.neutroncore.records.NeutronItem.IDENTIFIER_KEY;
import static com.tecknobit.neutroncore.records.User.*;

@RestController
public class UsersController extends NeutronController {

    private final UsersHelper usersHelper;

    public UsersController(UsersHelper usersHelper) {
        this.usersHelper = usersHelper;
    }

    /**
     * Method to sign up in the <b>Nova's system</b>
     *
     * @param payload: payload of the request
     * <pre>
     *      {@code
     *              {
     *                  "server_secret" : "the secret of the server" -> [String],
     *                  "name" : "the name of the user" -> [String],
     *                  "surname": "the surname of the user" -> [String],
     *                  "email": "the email of the user" -> [String],
     *                  "password": "the password of the user" -> [String]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
    @PostMapping(path = SIGN_UP_ENDPOINT)
    @RequestPath(path = "/api/v1/users/signUp", method = POST)
    public String signUp(@RequestBody Map<String, String> payload) {
        loadJsonHelper(payload);
        if(serverProtector.serverSecretMatches(jsonHelper.getString(SERVER_SECRET_KEY))) {
            String name = jsonHelper.getString(NAME_KEY);
            String surname = jsonHelper.getString(SURNAME_KEY);
            if(isNameValid(name)) {
                if(isSurnameValid(surname))
                    return executeAuth(payload, name, surname);
                else
                    return failedResponse(WRONG_SURNAME_MESSAGE);
            } else
                return failedResponse(WRONG_NAME_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    /**
     * Method to sign in the <b>Nova's system</b>
     *
     * @param payload: payload of the request
     * <pre>
     *      {@code
     *              {
     *                  "email": "the email of the user", -> [String]
     *                  "password": "the password of the user" -> [String]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
    @PostMapping(path = SIGN_IN_ENDPOINT)
    @RequestPath(path = "/api/v1/users/signIn", method = POST)
    public String signIn(@RequestBody Map<String, String> payload) {
        return executeAuth(payload);
    }

    /**
     * Method to execute the auth operations
     *
     * @param payload: the payload received with the auth request
     * @param personalData: the personal data of the user like name and surname
     * @return the result of the auth operation as {@link String}
     */
    private String executeAuth(Map<String, String> payload, String ... personalData) {
        loadJsonHelper(payload);
        String email = jsonHelper.getString(EMAIL_KEY);
        String password = jsonHelper.getString(PASSWORD_KEY);
        String language = jsonHelper.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE);
        if(isEmailValid(email)) {
            if(isPasswordValid(password)) {
                if(isLanguageValid(language)) {
                    String id;
                    String token;
                    String profilePicUrl;
                    JSONObject response = new JSONObject();
                    if(personalData.length == 2) {
                        id = generateIdentifier();
                        token = generateIdentifier();
                        profilePicUrl = DEFAULT_PROFILE_PIC;
                        try {
                            usersHelper.signUpUser(
                                    id,
                                    token,
                                    personalData[0],
                                    personalData[1],
                                    email.toLowerCase(),
                                    password,
                                    language
                            );
                        } catch (Exception e) {
                            e.printStackTrace();
                            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
                        }
                    } else {
                        try {
                            User user = usersHelper.signInUser(email.toLowerCase(), password);
                            if(user != null) {
                                id = user.getId();
                                token = user.getToken();
                                profilePicUrl = user.getProfilePic();
                                response.put(NAME_KEY, user.getName());
                                response.put(SURNAME_KEY, user.getSurname());
                                response.put(LANGUAGE_KEY, user.getLanguage());
                            } else
                                return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
                        } catch (Exception e) {
                            return failedResponse(WRONG_PROCEDURE_MESSAGE);
                        }
                    }
                    return successResponse(response
                            .put(IDENTIFIER_KEY, id)
                            .put(TOKEN_KEY, token)
                            .put(PROFILE_PIC_KEY, profilePicUrl)
                    );
                } else
                    return failedResponse(WRONG_LANGUAGE_MESSAGE);
            } else
                return failedResponse(WRONG_PASSWORD_MESSAGE);
        } else
            return failedResponse(WRONG_EMAIL_MESSAGE);
    }

    /**
     * Method to change the profile pic of the user
     *
     * @param id: the identifier of the user
     * @param token: the token of the user
     * @param profilePic: the profile pic chosen by the user to set as the new profile pic
     *
     * @return the result of the request as {@link String}
     */
    @PostMapping(
            path = USERS_KEY + "/{" + IDENTIFIER_KEY + "}" + CHANGE_PROFILE_PIC_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/changeProfilePic", method = POST)
    public String changeProfilePic(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestParam(PROFILE_PIC_KEY) MultipartFile profilePic
    ) {
        if(isMe(id, token)) {
            if(!profilePic.isEmpty()) {
                JSONObject response = new JSONObject();
                try {
                    String profilePicUrl = usersHelper.changeProfilePic(profilePic, id);
                    response.put(PROFILE_PIC_KEY, profilePicUrl);
                } catch (Exception e) {
                    response.put(PROFILE_PIC_KEY, DEFAULT_PROFILE_PIC);
                }
                return successResponse(response);
            } else
                return failedResponse(WRONG_PROCEDURE_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    /**
     * Method to change the email of the user
     *
     * @param id: the identifier of the user
     * @param token: the token of the user
     * @param payload: payload of the request
     * <pre>
     *      {@code
     *              {
     *                  "email": "the new email of the user" -> [String]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
    @PatchMapping(
            path = USERS_KEY + "/{" + IDENTIFIER_KEY + "}" + CHANGE_EMAIL_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/changeEmail", method = PATCH)
    public String changeEmail(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, String> payload
    ) {
        if(isMe(id, token)) {
            loadJsonHelper(payload);
            String email = jsonHelper.getString(EMAIL_KEY);
            if(isEmailValid(email)) {
                try {
                    usersHelper.changeEmail(email, id);
                    return successResponse();
                } catch (Exception e) {
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                }
            } else
                return failedResponse(WRONG_EMAIL_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    /**
     * Method to change the password of the user
     *
     * @param id: the identifier of the user
     * @param token: the token of the user
     * @param payload: payload of the request
     * <pre>
     *      {@code
     *              {
     *                  "password": "the new password of the user" -> [String]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
    @PatchMapping(
            path = USERS_KEY + "/{" + IDENTIFIER_KEY + "}" + CHANGE_PASSWORD_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/changePassword", method = PATCH)
    public String changePassword(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, String> payload
    ) {
        if(isMe(id, token)) {
            loadJsonHelper(payload);
            String password = jsonHelper.getString(PASSWORD_KEY);
            if(isPasswordValid(password)) {
                try {
                    usersHelper.changePassword(password, id);
                    return successResponse();
                } catch (Exception e) {
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                }
            } else
                return failedResponse(WRONG_PASSWORD_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

    /**
     * Method to change the language of the user
     *
     * @param id: the identifier of the user
     * @param token: the token of the user
     * @param payload: payload of the request
     * <pre>
     *      {@code
     *              {
     *                  "language": "the new language of the user" -> [String]
     *              }
     *      }
     * </pre>
     *
     * @return the result of the request as {@link String}
     */
    @PatchMapping(
            path = USERS_KEY + "/{" + IDENTIFIER_KEY + "}" + CHANGE_LANGUAGE_ENDPOINT,
            headers = {
                    TOKEN_KEY
            }
    )
    @RequestPath(path = "/api/v1/users/{id}/changeLanguage", method = PATCH)
    public String changeLanguage(
            @PathVariable(IDENTIFIER_KEY) String id,
            @RequestHeader(TOKEN_KEY) String token,
            @RequestBody Map<String, String> payload
    ) {
        if(isMe(id, token)) {
            loadJsonHelper(payload);
            String language = jsonHelper.getString(LANGUAGE_KEY);
            if(isLanguageValid(language)) {
                try {
                    usersHelper.changeLanguage(language, id);
                    return successResponse();
                } catch (Exception e) {
                    return failedResponse(WRONG_PROCEDURE_MESSAGE);
                }
            } else
                return failedResponse(WRONG_LANGUAGE_MESSAGE);
        } else
            return failedResponse(NOT_AUTHORIZED_OR_WRONG_DETAILS_MESSAGE);
    }

}
