package com.tecknobit.neutroncore.helpers

import com.tecknobit.equinox.Requester

class NeutronRequester(
    host: String,
    userId: String? = null,
    userToken: String? = null,
) : Requester(
    host = host,
    userId = userId,
    userToken = userToken,
    connectionErrorMessage = "",
    enableCertificatesValidation = true
) {
}