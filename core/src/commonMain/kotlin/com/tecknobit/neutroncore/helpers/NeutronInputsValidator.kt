package com.tecknobit.neutroncore.helpers

import com.tecknobit.equinoxcore.helpers.InputsValidator
import com.tecknobit.neutroncore.enums.NeutronCurrency

/**
 * The `NeutronInputsValidator` class is useful to validate the inputs
 *
 * @author N7ghtm4r3 - Tecknobit
 *
 * @see InputsValidator
 */
object NeutronInputsValidator : InputsValidator() {

    /**
     * `REVENUE_TITLE_MAX_LENGTH` the max valid length for the revenue title
     */
    const val REVENUE_TITLE_MAX_LENGTH: Int = 30

    /**
     * `REVENUE_DESCRIPTION_MAX_LENGTH` the max valid length for the revenue description
     */
    const val REVENUE_DESCRIPTION_MAX_LENGTH: Int = 250

    /**
     * `MAX_REVENUE_LABELS_NUMBER_LENGTH` the max valid number of labels for revenue
     */
    const val MAX_REVENUE_LABELS_NUMBER: Int = 5

    /**
     * Method to validate a currency
     *
     * @param currency Currency value to check the validity
     *
     * @return whether the currency is valid or not as `boolean`
     */
    fun isCurrencyValid(
        currency: String?
    ): Boolean {
        if (currency == null)
            return false
        try {
            NeutronCurrency.valueOf(currency)
            return true
        } catch (e: IllegalArgumentException) {
            return false
        }
    }

    /**
     * Method to validate a value of a revenue
     *
     * @param revenueValue Value to check the validity
     *
     * @return whether the value is valid or not as `boolean`
     */
    fun isRevenueValueValid(revenueValue: Double): Boolean {
        return revenueValue >= 0
    }

    /**
     * Method to validate a title of a revenue
     *
     * @param revenueTitle Title to check the validity
     *
     * @return whether the title is valid or not as `boolean`
     */
    fun isRevenueTitleValid(revenueTitle: String): Boolean {
        return isInputValid(revenueTitle) && revenueTitle.length <= REVENUE_TITLE_MAX_LENGTH
    }

    /**
     * Method to validate a description of a revenue
     *
     * @param revenueDescription Description to check the validity
     *
     * @return whether the description is valid or not as `boolean`
     */
    fun isRevenueDescriptionValid(revenueDescription: String): Boolean {
        return isInputValid(revenueDescription) && revenueDescription.length <= REVENUE_DESCRIPTION_MAX_LENGTH
    }

}
