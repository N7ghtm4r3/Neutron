package com.tecknobit.neutroncore.dtos

import com.tecknobit.equinoxcore.annotations.DTO
import kotlinx.serialization.Serializable

/**
 * `WalletStatus` data transferable object used to share the current status of the wallet of the user
 *
 * @property totalEarnings The total earnings amount value
 * @property trend The current trend of the wallet based on the period
 */
@DTO
@Serializable
data class WalletStatus(
    val totalEarnings: Double,
    val trend: Double
) {

    companion object {

        /**
         * `TOTAL_EARNINGS_KEY` the key for the `total_earnings` field
         */
        const val TOTAL_EARNINGS_KEY = "total_earnings"

        /**
         * `TREND_KEY` the key for the `trend` field
         */
        const val TREND_KEY = "trend"

    }

}
