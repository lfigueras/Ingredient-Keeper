package com.lovely.bakingrecipes.data

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatAmountTest {

    @Test
    fun wholeNumbersHaveNoDecimals() {
        assertEquals("250", formatAmount(250.0))
        assertEquals("1", formatAmount(1.0))
    }

    @Test
    fun fractionalNumbersKeepDecimals() {
        assertEquals("2.5", formatAmount(2.5))
    }
}
