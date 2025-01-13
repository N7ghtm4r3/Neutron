package com.tecknobit.neutroncore.helpers

import com.tecknobit.equinoxcore.network.EquinoxBaseEndpointsSet

/**
 * The `NeutronEndpoints` class is a container with all the Neutron's endpoints
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see EquinoxBaseEndpointsSet
 */
object NeutronEndpoints : EquinoxBaseEndpointsSet() {

    /**
     * `CHANGE_CURRENCY_ENDPOINT` the endpoint to execute the change of the user currency
     */
    const val CHANGE_CURRENCY_ENDPOINT: String = "/changeCurrency"

    /**
     * `TICKETS_ENDPOINT` the endpoint to execute the all the ticket's operation
     */
    const val TICKETS_ENDPOINT: String = "/tickets"

    /**
     * `WALLET_ENDPOINT` the endpoint to execute the all the wallet's operation
     */
    const val WALLET_ENDPOINT: String = "/wallet"

    /**
     * `TRANSFER_IN_ENDPOINT` the endpoint to execute the migration to the current machine of an account data
     */
    const val TRANSFER_IN_ENDPOINT: String = "transferIn"

    /**
     * `TRANSFER_OUT_ENDPOINT` the endpoint to execute the migration from the current machine of an account data
     */
    const val TRANSFER_OUT_ENDPOINT: String = "transferOut"

}
