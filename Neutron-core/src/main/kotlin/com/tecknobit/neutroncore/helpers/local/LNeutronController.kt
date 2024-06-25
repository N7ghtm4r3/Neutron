package com.tecknobit.neutroncore.helpers.local

import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.FAILED
import com.tecknobit.apimanager.apis.sockets.SocketManager.StandardResponseCode.SUCCESSFUL
import com.tecknobit.apimanager.formatters.JsonHelper
import com.tecknobit.equinox.Requester.Companion.RESPONSE_MESSAGE_KEY
import com.tecknobit.equinox.Requester.Companion.RESPONSE_STATUS_KEY
import org.json.JSONObject

interface LNeutronController {

    companion object {

        const val LOCAL_DATABASE_NAME: String = "Neutron.db"

        fun getSuccessfulResponse(message: JSONObject): JsonHelper {
            return getResponse(SUCCESSFUL, message)
        }

        fun getFailedResponse(message: JSONObject): JsonHelper {
            return getResponse(FAILED, message)
        }

        private fun getResponse(responseCode: StandardResponseCode, message: JSONObject): JsonHelper {
            return JsonHelper(
                JSONObject()
                    .put(RESPONSE_STATUS_KEY, responseCode)
                    .put(RESPONSE_MESSAGE_KEY, message)
            )
        }

        fun getSuccessfulResponse(message: String): JsonHelper {
            return getResponse(SUCCESSFUL, message)
        }

        fun getFailedResponse(message: String): JsonHelper {
            return getResponse(FAILED, message)
        }

        private fun getResponse(responseCode: StandardResponseCode, message: String): JsonHelper {
            return JsonHelper(
                JSONObject()
                    .put(RESPONSE_STATUS_KEY, responseCode)
                    .put(RESPONSE_MESSAGE_KEY, message)
            )
        }

    }

}
