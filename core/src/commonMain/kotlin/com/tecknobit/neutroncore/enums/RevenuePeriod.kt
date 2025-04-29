package com.tecknobit.neutroncore.enums

import kotlinx.datetime.Clock

/**
 * `RevenuePeriod` list of the available temporal period gap filter
 *
 * @param days The milliseconds gap value formatted as days
 */
enum class RevenuePeriod(
    private val days: Long
) {

    /**
     * `LAST_WEEK` retrieve the revenues inserted within last week
     */
    LAST_WEEK(7 * 86400 * 1000L),

    /**
     * `LAST_MONTH` retrieve the revenues inserted within last month
     */
    LAST_MONTH(30 * 86400 * 1000L),

    /**
     * `LAST_THREE_MONTHS` retrieve the revenues inserted within last three months
     */
    LAST_THREE_MONTHS(90 * 86400 * 1000L),

    /**
     * `LAST_SIX_MONTHS` retrieve the revenues inserted within last six months
     */
    LAST_SIX_MONTHS(180 * 86400 * 1000L),

    /**
     * `LAST_YEAR` retrieve the revenues inserted within last years
     */
    LAST_YEAR(365 * 86400 * 1000L),

    /**
     * `ALL` retrieve all the revenues
     */
    ALL(0);

    /**
     * Method to get the base date from retrieve the revenues
     *
     * @param offset The offset to apply to the temporal gap
     * @return the base date from retrieve the revenues as [Long]
     */
    fun RevenuePeriod.calculateFromDate(
        offset: Int = 1
    ) : Long {
        require(offset > 0) { "Offset cannot be negative or equal to zero"}
        if(this == ALL)
            return 0
        val now = Clock.System.now().toEpochMilliseconds()
        return now - (this.days * offset)
    }

}