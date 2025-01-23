package com.tecknobit.neutroncore.enums

/**
 * `NeutronCurrency` list of the available fiat currencies supported
 *
 * @param isoCode The iso code, 3 letters, of the currency
 * @param isoName The iso code the iso name of the currency
 * @param symbol The symbol of the currency
 */
enum class NeutronCurrency(
    val isoCode: String,
    val isoName: String,
    val symbol: String
) {

    /**
     * `EURO` fiat currency
     */
    EURO("EUR", "Euro", "€"),

    /**
     * `DOLLAR` fiat currency
     */
    DOLLAR("USD", "US Dollar", "$"),

    /**
     * `POUND_STERLING` fiat currency
     */
    POUND_STERLING("GBP", "Pound sterling", "£"),

    /**
     * `JAPANESE_YEN` fiat currency
     */
    JAPANESE_YEN("JPY", "Japanese Yen", "¥"),

    /**
     * `CHINESE_YEN` fiat currency
     */
    CHINESE_YEN("CNY", "Chinese Yuan", "¥");

    companion object {

        /**
         * Method to get an instance of the [NeutronCurrency]
         *
         * @param currencyName The name of the currency to get
         * @return the currency instance as [NeutronCurrency]
         */
        fun getInstance(
            currencyName: String?
        ): NeutronCurrency {
            if (currencyName == null)
                return DOLLAR
            return when (currencyName) {
                "EURO" -> EURO
                "POUND_STERLING" -> POUND_STERLING
                "JAPANESE_YEN" -> JAPANESE_YEN
                "CHINESE_YEN" -> CHINESE_YEN
                else -> DOLLAR
            }
        }
    }

}