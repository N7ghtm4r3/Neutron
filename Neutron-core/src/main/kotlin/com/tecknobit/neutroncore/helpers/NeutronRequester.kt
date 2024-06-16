package com.tecknobit.neutroncore.helpers

import com.tecknobit.apimanager.annotations.RequestPath
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.apimanager.apis.APIRequest.Params
import com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*
import com.tecknobit.apimanager.apis.ServerProtector.SERVER_SECRET_KEY
import com.tecknobit.equinox.Requester
import com.tecknobit.neutroncore.helpers.Endpoints.*
import com.tecknobit.neutroncore.helpers.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.neutroncore.helpers.InputValidator.isLanguageValid
import com.tecknobit.neutroncore.records.User.*
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue.REVENUE_DESCRIPTION_KEY
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue.REVENUE_LABELS_KEY
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue.*
import com.tecknobit.neutroncore.records.revenues.Revenue.REVENUES_KEY
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import org.json.JSONObject
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.io.File

class NeutronRequester(
    host: String,
    userId: String? = null,
    userToken: String? = null,
) : Requester(
    host = host,
    userId = userId,
    userToken = userToken,
    connectionErrorMessage = "Server is temporarily unavailable",
    enableCertificatesValidation = true
) {

    /**
     * Function to execute the request to sign up in the Neutron's system
     *
     * @param serverSecret: the secret of the personal Neutron's backend
     * @param name: the name of the user
     * @param surname: the surname of the user
     * @param email: the email of the user
     * @param password: the password of the user
     * @param language: the language of the user
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/users/signUp", method = POST)
    fun signUp(
        serverSecret: String,
        name: String,
        surname: String,
        email: String,
        password: String,
        language: String
    ) : JSONObject {
        val payload = Params()
        payload.addParam(SERVER_SECRET_KEY, serverSecret)
        payload.addParam(NAME_KEY, name)
        payload.addParam(SURNAME_KEY, surname)
        payload.addParam(EMAIL_KEY, email)
        payload.addParam(PASSWORD_KEY, password)
        payload.addParam(LANGUAGE_KEY,
            if(!isLanguageValid(language))
                DEFAULT_LANGUAGE
            else
                language
        )
        return execPost(
            endpoint = SIGN_UP_ENDPOINT,
            payload = payload
        )
    }

    /**
     * Function to execute the request to sign in the Neutron's system
     *
     * @param email: the email of the user
     * @param password: the password of the user
     *
     * @return the result of the request as [JSONObject]
     *
     */
    @RequestPath(path = "/api/v1/users/signIn", method = POST)
    fun signIn(
        email: String,
        password: String
    ) : JSONObject {
        val payload = Params()
        payload.addParam(EMAIL_KEY, email)
        payload.addParam(PASSWORD_KEY, password)
        return execPost(
            endpoint = SIGN_IN_ENDPOINT,
            payload = payload
        )
    }

    /**
     * Function to execute the request to change the profile pic of the user
     *
     * @param profilePic: the profile pic chosen by the user to set as the new profile pic
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/changeProfilePic", method = POST)
    open fun changeProfilePic(
        profilePic: File
    ) : JSONObject {
        val body: MultiValueMap<String, Any> = LinkedMultiValueMap()
        body.add(PROFILE_PIC_KEY, FileSystemResource(profilePic))
        return execMultipartRequest(
            body = body,
            endpoint = assembleUsersEndpointPath(CHANGE_PROFILE_PIC_ENDPOINT)
        )
    }

    /**
     * Function to exec a multipart body  request
     *
     * @param body: the body payload of the request
     * @param endpoint: the endpoint path of the url
     *
     * @return the result of the request as [JSONObject]
     */
    private fun execMultipartRequest(
        body: MultiValueMap<String, Any>,
        endpoint: String
    ) : JSONObject {
        val headers = org.springframework.http.HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        headers.add(TOKEN_KEY, userToken)
        val requestEntity: HttpEntity<Any?> = HttpEntity<Any?>(body, headers)
        val restTemplate = RestTemplate()
        val response = restTemplate.postForEntity(
            host + endpoint,
            requestEntity,
            String::class.java
        ).body
        return JSONObject(response)
    }

    /**
     * Function to execute the request to change the email of the user
     *
     * @param newEmail: the new email of the user
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/changeEmail", method = PATCH)
    fun changeEmail(
        newEmail: String
    ): JSONObject {
        val payload = Params()
        payload.addParam(EMAIL_KEY, newEmail)
        return execPatch(
            endpoint = assembleUsersEndpointPath(CHANGE_EMAIL_ENDPOINT),
            payload = payload
        )
    }

    /**
     * Function to execute the request to change the password of the user
     *
     * @param newPassword: the new password of the user
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/changePassword", method = PATCH)
    fun changePassword(
        newPassword: String
    ): JSONObject {
        val payload = Params()
        payload.addParam(PASSWORD_KEY, newPassword)
        return execPatch(
            endpoint = assembleUsersEndpointPath(CHANGE_PASSWORD_ENDPOINT),
            payload = payload
        )
    }

    /**
     * Function to execute the request to change the language of the user
     *
     * @param newLanguage: the new language of the user
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/changeLanguage", method = PATCH)
    fun changeLanguage(
        newLanguage: String
    ): JSONObject {
        val payload = Params()
        payload.addParam(LANGUAGE_KEY, newLanguage)
        return execPatch(
            endpoint = assembleUsersEndpointPath(CHANGE_LANGUAGE_ENDPOINT),
            payload = payload
        )
    }

    /**
     * Function to execute the request to change the currency of the user
     *
     * @param newCurrency: the new currency of the user
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/changeCurrency", method = PATCH)
    fun changeCurrency(
        newCurrency: NeutronCurrency
    ): JSONObject {
        val payload = Params()
        payload.addParam(CURRENCY_KEY, newCurrency.name)
        return execPatch(
            endpoint = assembleUsersEndpointPath(CHANGE_CURRENCY_ENDPOINT),
            payload = payload
        )
    }

    @RequestPath(path = "/api/v1/users/{id}/revenues", method = GET)
    fun listRevenues(): JSONObject {
        return execGet(
            endpoint = assembleRevenuesEndpointPath()
        )
    }

    @Wrapper
    @RequestPath(path = "/api/v1/users/{id}/revenues", method = POST)
    fun createProjectRevenue(
        title: String,
        value: Double,
        revenueDate: Long
    ): JSONObject {
        return createRevenue(
            title = title,
            value = value,
            revenueDate = revenueDate
        )
    }

    @Wrapper
    @RequestPath(path = "/api/v1/users/{id}/revenues", method = POST)
    fun createGeneralRevenue(
        title: String,
        description: String,
        value: Double,
        revenueDate: Long,
        labels: List<RevenueLabel> = emptyList()
    ): JSONObject {
        val payload = Params()
        payload.addParam(REVENUE_DESCRIPTION_KEY, description)
        payload.addParam(REVENUE_LABELS_KEY, labels)
        return createRevenue(
            title = title,
            value = value,
            revenueDate = revenueDate
        )
    }

    @RequestPath(path = "/api/v1/users/{id}/revenues", method = POST)
    private fun createRevenue(
        payload: Params? = null,
        title: String,
        value: Double,
        revenueDate: Long
    ): JSONObject {
        val isProjectRevenue = payload == null
        val rPayload = if(isProjectRevenue)
            Params()
        else
            payload!!
        rPayload.addParam(IS_PROJECT_REVENUE_KEY, isProjectRevenue)
        rPayload.addParam(REVENUE_TITLE_KEY, title)
        rPayload.addParam(REVENUE_VALUE_KEY, value)
        rPayload.addParam(REVENUE_DATE_KEY, revenueDate)
        println(rPayload)
        return execPost(
            endpoint = assembleRevenuesEndpointPath(),
            payload = rPayload
        )
    }

    /**
     * Function to assemble the endpoint to make the request to the users controller
     *
     * @param endpoint: the endpoint path of the url
     *
     * @return an endpoint to make the request as [String]
     */
    protected fun assembleRevenuesEndpointPath(
        endpoint: String = ""
    ): String {
        return "${assembleUsersEndpointPath(endpoint)}/$REVENUES_KEY"
    }

    /**
     * Function to assemble the endpoint to make the request to the users controller
     *
     * @param endpoint: the endpoint path of the url
     *
     * @return an endpoint to make the request as [String]
     */
    protected fun assembleUsersEndpointPath(
        endpoint: String = ""
    ): String {
        return "$USERS_KEY/$userId$endpoint"
    }
    
}