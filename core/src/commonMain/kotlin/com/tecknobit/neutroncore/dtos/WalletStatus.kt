package com.tecknobit.neutroncore.dtos

import com.tecknobit.equinoxcore.annotations.DTO

@DTO
data class WalletStatus(
    val totalEarnings: Double,
    val trend: Double
) {

    companion object {

        /**
         * `TOTAL_EARNINGS_KEY` the key for the **"total_earnings"** field
         */
        const val TOTAL_EARNINGS_KEY = "total_earnings"

        /**
         * `TREND_KEY` the key for the **"trend"** field
         */
        const val TREND_KEY = "trend"

    }

}
