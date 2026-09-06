package com.lovely.bakingrecipes.data

import org.junit.Assert.assertEquals
import org.junit.Test

class PastryCategoriesTest {

    @Test
    fun singular_mapsKnownCategories() {
        assertEquals("Cake", PastryCategories.singular("Cakes"))
        assertEquals("Cookie", PastryCategories.singular("Cookies"))
        assertEquals("Pastry", PastryCategories.singular("Pastries"))
    }

    @Test
    fun singular_returnsInputWhenUnknown() {
        assertEquals("Other", PastryCategories.singular("Other"))
        assertEquals("Candy & Chocolate", PastryCategories.singular("Candy & Chocolate"))
    }
}
