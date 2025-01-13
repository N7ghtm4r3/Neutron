package com.tecknobit.neutroncore.enums

import kotlinx.datetime.Clock

enum class RevenuePeriod(
    private val days: Long
) {

    LAST_WEEK(7 * 86400 * 1000L),

    LAST_MONTH(30 * 86400 * 1000L),

    LAST_THREE_MONTHS(90 * 86400 * 1000L),

    LAST_SIX_MONTHS(180 * 86400 * 1000L),

    LAST_YEAR(365 * 86400 * 1000L),

    ALL(0);

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