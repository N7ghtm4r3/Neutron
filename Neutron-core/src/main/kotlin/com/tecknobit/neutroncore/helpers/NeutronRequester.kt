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
import com.tecknobit.neutroncore.records.TransferPayload.USER_DETAILS_KEY
import com.tecknobit.neutroncore.records.User
import com.tecknobit.neutroncore.records.User.*
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue.REVENUE_DESCRIPTION_KEY
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue.REVENUE_LABELS_KEY
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue.*
import com.tecknobit.neutroncore.records.revenues.Revenue
import com.tecknobit.neutroncore.records.revenues.Revenue.REVENUES_KEY
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import com.tecknobit.neutroncore.records.revenues.TicketRevenue
import com.tecknobit.neutroncore.records.revenues.TicketRevenue.CLOSING_DATE_KEY
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpEntity
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.io.File

open class NeutronRequester(
    host: String,
    userId: String? = null,
    userToken: String? = null,
) : Requester(
    host = host + BASE_ENDPOINT,
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
            revenueDate = revenueDate,
            payload = payload
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
        return execPost(
            endpoint = assembleRevenuesEndpointPath(),
            payload = rPayload
        )
    }

    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}", method = GET)
    fun getProjectRevenue(
        revenue: Revenue
    ): JSONObject {
        return getProjectRevenue(
            revenueId = revenue.id
        )
    }

    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}", method = GET)
    fun getProjectRevenue(
        revenueId: String
    ): JSONObject {
        return execGet(
            endpoint = assembleRevenuesEndpointPath(
                revenueId = revenueId,
                isProjectPath = true
            )
        )
    }

    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}/tickets", method = POST)
    fun addTicketToProjectRevenue(
        projectRevenue: ProjectRevenue,
        ticketRevenue: TicketRevenue
    ): JSONObject {
        return addTicketToProjectRevenue(
            projectRevenueId = projectRevenue.id,
            ticketTitle = ticketRevenue.title,
            ticketValue = ticketRevenue.value,
            ticketDescription = ticketRevenue.description,
            openingDate = ticketRevenue.revenueTimestamp,
            closingDate = ticketRevenue.closingTimestamp
        )
    }

    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}/tickets", method = POST)
    fun addTicketToProjectRevenue(
        projectRevenueId: String,
        ticketTitle: String,
        ticketValue: Double,
        ticketDescription: String,
        openingDate: Long,
        closingDate: Long = -1L
    ): JSONObject {
        val payload = Params()
        payload.addParam(REVENUE_TITLE_KEY, ticketTitle)
        payload.addParam(REVENUE_VALUE_KEY, ticketValue)
        payload.addParam(REVENUE_DESCRIPTION_KEY, ticketDescription)
        payload.addParam(REVENUE_DATE_KEY, openingDate)
        payload.addParam(CLOSING_DATE_KEY, closingDate)
        return execPost(
            endpoint = assembleRevenuesEndpointPath(
                revenueId = projectRevenueId,
                isProjectPath = true,
                extraPath = TICKETS_ENDPOINT
            ),
            payload = payload
        )
    }

    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}/tickets/{ticket_id}", method = PATCH)
    fun closeProjectRevenueTicket(
        projectRevenue: ProjectRevenue,
        ticket: TicketRevenue
    ): JSONObject {
        return closeProjectRevenueTicket(
            projectRevenueId = projectRevenue.id,
            ticketId = ticket.id
        )
    }

    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}/tickets/{ticket_id}", method = PATCH)
    fun closeProjectRevenueTicket(
        projectRevenueId: String,
        ticketId: String
    ): JSONObject {
        return execPatch(
            endpoint = assembleRevenuesEndpointPath(
                revenueId = projectRevenueId,
                isProjectPath = true,
                extraPath = TICKETS_ENDPOINT,
                extraId = "/$ticketId"
            ),
            payload = Params()
        )
    }

    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}/tickets/{ticket_id}", method = DELETE)
    fun deleteProjectRevenueTicket(
        projectRevenue: ProjectRevenue,
        ticket: TicketRevenue
    ): JSONObject {
        return deleteProjectRevenueTicket(
            projectRevenueId = projectRevenue.id,
            ticketId = ticket.id
        )
    }

    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}/tickets/{ticket_id}", method = DELETE)
    fun deleteProjectRevenueTicket(
        projectRevenueId: String,
        ticketId: String
    ): JSONObject {
        return execDelete(
            endpoint = assembleRevenuesEndpointPath(
                revenueId = projectRevenueId,
                isProjectPath = true,
                extraPath = TICKETS_ENDPOINT,
                extraId = "/$ticketId"
            )
        )
    }

    @RequestPath(path = "/api/v1/users/{id}/revenues/{revenue_id}", method = DELETE)
    fun deleteRevenue(
        revenue: Revenue
    ): JSONObject {
        return deleteRevenue(
            revenueId = revenue.id
        )
    }

    @RequestPath(path = "/api/v1/users/{id}/revenues/{revenue_id}", method = DELETE)
    fun deleteRevenue(
        revenueId: String
    ): JSONObject {
        return execDelete(
            endpoint = assembleRevenuesEndpointPath(
                revenueId = revenueId
            )
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
        endpoint: String = "",
        isProjectPath: Boolean = false,
        revenueId: String? = null,
        extraPath: String = "",
        extraId: String = ""
    ): String {
        var baseEndpoint = "${assembleUsersEndpointPath(endpoint)}/$REVENUES_KEY"
        if(isProjectPath)
            baseEndpoint = "$baseEndpoint$PROJECTS_KEY$revenueId$extraPath$extraId"
        else if (revenueId != null)
            baseEndpoint = "$baseEndpoint/$revenueId"
        return baseEndpoint
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

    @RequestPath(path = "/api/v1/transferIn", method = POST)
    fun transferIn(
        serverSecret: String,
        user: User,
        revenues: List<Revenue>
    ): JSONObject {
        val payload = Params()
        payload.addParam(SERVER_SECRET_KEY, serverSecret)
        payload.addParam(USER_DETAILS_KEY, user.toTransferTarget())
        val jRevenues = JSONArray()
        revenues.forEach { revenue ->
            jRevenues.put(revenue.toTransferTarget())
        }
        payload.addParam(REVENUES_KEY, jRevenues)
        return execPost(
            endpoint = TRANSFER_IN_ENDPOINT,
            payload = payload
        )
    }

    @RequestPath(path = "/api/v1/transferOut/{id}", method = GET)
    fun transferOut(): JSONObject {
        return execGet(
            endpoint = "$TRANSFER_OUT_ENDPOINT/$userId"
        )
    }

}