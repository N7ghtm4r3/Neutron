package com.tecknobit.neutroncore.helpers

import com.tecknobit.apimanager.annotations.RequestPath
import com.tecknobit.apimanager.annotations.Structure
import com.tecknobit.apimanager.annotations.Wrapper
import com.tecknobit.apimanager.apis.APIRequest.Params
import com.tecknobit.apimanager.apis.APIRequest.RequestMethod.*
import com.tecknobit.apimanager.apis.ServerProtector.SERVER_SECRET_KEY
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.GENERIC_RESPONSE
import com.tecknobit.equinox.Requester
import com.tecknobit.neutroncore.helpers.Endpoints.*
import com.tecknobit.neutroncore.helpers.InputValidator.DEFAULT_LANGUAGE
import com.tecknobit.neutroncore.helpers.InputValidator.isLanguageValid
import com.tecknobit.neutroncore.records.User.*
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue.REVENUE_DESCRIPTION_KEY
import com.tecknobit.neutroncore.records.revenues.GeneralRevenue.REVENUE_LABELS_KEY
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue
import com.tecknobit.neutroncore.records.revenues.ProjectRevenue.*
import com.tecknobit.neutroncore.records.revenues.Revenue
import com.tecknobit.neutroncore.records.revenues.Revenue.REVENUES_KEY
import com.tecknobit.neutroncore.records.revenues.RevenueLabel
import com.tecknobit.neutroncore.records.revenues.RevenueLabel.REVENUE_LABEL_COLOR_KEY
import com.tecknobit.neutroncore.records.revenues.RevenueLabel.REVENUE_LABEL_TEXT_KEY
import com.tecknobit.neutroncore.records.revenues.TicketRevenue
import com.tecknobit.neutroncore.records.revenues.TicketRevenue.CLOSING_DATE_KEY
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * The **NeutronRequester** class is useful to communicate with the Neutron's backend
 *
 * @param host The host where is running the Neutron's backend
 * @param userId The user identifier
 * @param userToken The user token
 *
 * @author N7ghtm4r3 - Tecknobit
 * @see Requester
 */
@Structure
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
     * @param serverSecret The secret of the personal Neutron's backend
     * @param name The name of the user
     * @param surname The surname of the user
     * @param email The email of the user
     * @param password The password of the user
     * @param language The language of the user
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
     * @param email The email of the user
     * @param password The password of the user
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
     * @param profilePic The profile pic chosen by the user to set as the new profile pic
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/changeProfilePic", method = POST)
    fun changeProfilePic(profilePic: File): JSONObject {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(
                PROFILE_PIC_KEY,
                profilePic.name,
                profilePic.readBytes().toRequestBody("*/*".toMediaType())
            )
            .build()
        return execMultipartRequest(
            endpoint = assembleUsersEndpointPath(CHANGE_PROFILE_PIC_ENDPOINT),
            body = body
        )
    }

    /**
     * Function to exec a multipart body  request
     *
     * @param endpoint The endpoint path of the url
     * @param body The body payload of the request
     *
     * @return the result of the request as [JSONObject]
     */
    private fun execMultipartRequest(
        endpoint: String,
        body: MultipartBody
    ): JSONObject {
        val mHeaders = mutableMapOf<String, String>()
        headers.headersKeys.forEach { headerKey ->
            mHeaders[headerKey] = headers.getHeader(headerKey)
        }
        val request: Request = Request.Builder()
            .headers(mHeaders.toHeaders())
            .url("$host$endpoint")
            .post(body)
            .build()
        val client = validateSelfSignedCertificate(OkHttpClient())
        var response: JSONObject? = null
        runBlocking {
            try {
                async {
                    response = try {
                        client.newCall(request).execute().body?.string()?.let { JSONObject(it) }
                    } catch (e: IOException) {
                        JSONObject(connectionErrorMessage())
                    }
                }.await()
            } catch (e: Exception) {
                response = JSONObject(connectionErrorMessage())
            }
        }
        return response!!
    }

    /**
     * Function to set the [RESPONSE_STATUS_KEY] to send when an error during the connection occurred
     *
     * No-any params required
     *
     * @return the error message as [String]
     */
    private fun connectionErrorMessage(): String {
        return JSONObject()
            .put(RESPONSE_STATUS_KEY, GENERIC_RESPONSE.name)
            .put(RESPONSE_MESSAGE_KEY, "Server is temporarily unavailable")
            .toString()
    }

    /**
     * Method to validate a self-signed SLL certificate and bypass the checks of its validity<br></br>
     * No-any params required
     *
     * @apiNote this method disable all checks on the SLL certificate validity, so is recommended to use for test only or
     * in a private distribution on own infrastructure
     */
    private fun validateSelfSignedCertificate(
        okHttpClient: OkHttpClient
    ): OkHttpClient {
        if (mustValidateCertificates) {
            val trustAllCerts = arrayOf<TrustManager>(
            object : X509TrustManager {
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }


                override fun checkClientTrusted(
                    certs: Array<X509Certificate>,
                    authType: String
                ) {
                }

                override fun checkServerTrusted(
                    certs: Array<X509Certificate>,
                    authType: String
                ) {
                }
            })
            val builder = okHttpClient.newBuilder()
            try {
                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustAllCerts, SecureRandom())
                builder.sslSocketFactory(
                    sslContext.socketFactory,
                    trustAllCerts[0] as X509TrustManager
                )
                builder.hostnameVerifier { _: String?, _: SSLSession? -> true }
                return builder.build()
            } catch (ignored: java.lang.Exception) {
            }
        }
        return OkHttpClient()
    }

    /**
     * Function to execute the request to change the email of the user
     *
     * @param newEmail The new email of the user
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
     * @param newPassword The new password of the user
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
     * @param newLanguage The new language of the user
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
     * @param newCurrency The new currency of the user
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

    /**
     * Function to execute the request to delete the account of the user
     *
     * No-any params required
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}", method = DELETE)
    fun deleteAccount(): JSONObject {
        println(assembleUsersEndpointPath())
        return execDelete(
            endpoint = assembleUsersEndpointPath()
        )
    }

    /**
     * Function to execute the request to get the revenues of the user
     *
     * No-any params required
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/revenues", method = GET)
    fun listRevenues(): JSONObject {
        return execGet(
            endpoint = assembleRevenuesEndpointPath()
        )
    }

    /**
     * Function to execute the request to create a new project revenue
     *
     * @param title The title of the project
     * @param value The initial revenue value
     * @param revenueDate The date when the project has been created
     *
     * @return the result of the request as [JSONObject]
     */
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

    /**
     * Function to execute the request to create a new general revenue
     *
     * @param title The title of the general revenue
     * @param description The description of the general revenue
     * @param value The amount revenue value
     * @param revenueDate The date when the general revenue has been created
     * @param labels The labels to attach to the general revenue
     *
     * @return the result of the request as [JSONObject]
     */
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
        val jLabels = JSONArray()
        labels.forEach { label ->
            jLabels.put(JSONObject()
                .put(REVENUE_LABEL_TEXT_KEY, label.text)
                .put(REVENUE_LABEL_COLOR_KEY, label.color)
            )
        }
        payload.addParam(REVENUE_LABELS_KEY, jLabels)
        return createRevenue(
            title = title,
            value = value,
            revenueDate = revenueDate,
            payload = payload
        )
    }

    /**
     * Function to execute the request to create a new revenue
     *
     * @param payload The payload to send
     * @param title The title of the revenue
     * @param value The amount revenue value
     * @param revenueDate The date when the revenue has been created
     *
     * @return the result of the request as [JSONObject]
     */
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

    /**
     * Function to execute the request to get a project revenue
     *
     * @param revenue The project revenue to get
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/revenues/projects/{revenue_id}", method = GET)
    fun getProjectRevenue(
        revenue: Revenue
    ): JSONObject {
        return getProjectRevenue(
            revenueId = revenue.id
        )
    }

    /**
     * Function to execute the request to get a project revenue
     *
     * @param revenueId The identifier of the project revenue to get
     *
     * @return the result of the request as [JSONObject]
     */
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

    /**
     * Function to execute the request to add a new ticket to a project revenue
     *
     * @param projectRevenue The project revenue where add the ticket
     * @param ticketRevenue The ticket to add to the project
     *
     * @return the result of the request as [JSONObject]
     */
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

    /**
     * Function to execute the request to add a new ticket to a project revenue
     *
     * @param projectRevenueId The identifier of the project revenue where add the ticket
     * @param ticketTitle The title of the ticket
     * @param ticketValue The amount value of the ticket
     * @param ticketDescription The description of the ticket
     * @param openingDate When the ticket has been opened
     * @param closingDate When the ticket has been closed
     *
     * @return the result of the request as [JSONObject]
     */
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

    /**
     * Function to execute the request to close a ticket
     *
     * @param projectRevenue The project revenue where close a ticket
     * @param ticket The ticket to close
     *
     * @return the result of the request as [JSONObject]
     */
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

    /**
     * Function to execute the request to close a ticket
     *
     * @param projectRevenueId The identifier of the project revenue where close a ticket
     * @param ticketId The identifier of the ticket to close
     *
     * @return the result of the request as [JSONObject]
     */
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

    /**
     * Function to execute the request to delete a ticket
     *
     * @param projectRevenue The project revenue where delete a ticket
     * @param ticket The ticket to delete
     *
     * @return the result of the request as [JSONObject]
     */
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

    /**
     * Function to execute the request to delete a ticket
     *
     * @param projectRevenueId The identifier of the project revenue where delete a ticket
     * @param ticketId The identifier of the ticket to delete
     *
     * @return the result of the request as [JSONObject]
     */
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

    /**
     * Function to execute the request to delete a revenue
     *
     * @param revenue The revenue to delete
     *
     * @return the result of the request as [JSONObject]
     */
    @RequestPath(path = "/api/v1/users/{id}/revenues/{revenue_id}", method = DELETE)
    fun deleteRevenue(
        revenue: Revenue
    ): JSONObject {
        return deleteRevenue(
            revenueId = revenue.id
        )
    }

    /**
     * Function to execute the request to delete a revenue
     *
     * @param revenueId The revenue identifier to delete
     *
     * @return the result of the request as [JSONObject]
     */
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
     * @param endpoint The endpoint path of the url
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
     * @param endpoint The endpoint path of the url
     *
     * @return an endpoint to make the request as [String]
     */
    protected fun assembleUsersEndpointPath(
        endpoint: String = ""
    ): String {
        return "$USERS_KEY/$userId$endpoint"
    }

}