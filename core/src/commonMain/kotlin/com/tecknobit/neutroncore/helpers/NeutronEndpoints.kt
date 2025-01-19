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
     * `DYNAMIC_ACCOUNT_DATA_ENDPOINT` the endpoint where the user can retrieve the dynamic data of his/her account,
     * such email, profile picture, etc...
     */
    @Deprecated(message = "USE THE EQUINOX BUILT-IN")
    const val DYNAMIC_ACCOUNT_DATA_ENDPOINT: String = "/dynamicAccountData"

    /**
     * `CHANGE_CURRENCY_ENDPOINT` the endpoint to execute the change of the user currency
     */
    const val CHANGE_CURRENCY_ENDPOINT: String = "/changeCurrency"

    /**
     * `PROJECT_BALANCE_ENDPOINT` the endpoint to where retrieve the balance of a project
     */
    const val PROJECT_BALANCE_ENDPOINT: String = "/balance"

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
