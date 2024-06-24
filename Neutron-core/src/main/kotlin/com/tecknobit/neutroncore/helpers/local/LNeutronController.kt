package com.tecknobit.neutroncore.helpers.local

import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.equinox.Requester
import org.json.JSONObject

interface LNeutronController {

    companion object {

        const val LOCAL_DATABASE_NAME: String = "Neutron.db"

        val DEFAULT_SUCCESS_RESPONSE: JsonHelper = getResponse(
            StandardResponseCode.SUCCESSFUL,
            "Operation executed successfully"
        )

        val DEFAULT_FAILURE_RESPONSE: JsonHelper = getResponse(
            StandardResponseCode.FAILED,
            "Operation executed failed"
        )

        fun getSuccessfulResponse(message: JSONObject): JsonHelper {
            return getResponse(StandardResponseCode.SUCCESSFUL, message.toString())
        }

        fun getFailedResponse(message: JSONObject): JsonHelper {
            return getResponse(StandardResponseCode.FAILED, message.toString())
        }

        private fun getResponse(responseCode: StandardResponseCode, message: String): JsonHelper {
            return JsonHelper(
                JSONObject()
                    .put(Requester.RESPONSE_STATUS_KEY, responseCode)
                    .put(Requester.RESPONSE_MESSAGE_KEY, message)
            )
        }

    }

}
