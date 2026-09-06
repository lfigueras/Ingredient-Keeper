package com.lovely.bakingrecipes.data

object PastryCategories {
    val all = listOf(
        "Cakes",
        "Cupcakes",
        "Cookies",
        "Brownies & Bars",
        "Breads",
        "Pastries",
        "Pies",
        "Tarts",
        "Muffins",
        "Donuts",
        "Croissants",
        "Macarons",
        "Cheesecakes",
        "Pancakes & Waffles",
        "Candy & Chocolate",
        "Other"
    )

    private val singulars = mapOf(
        "Cakes" to "Cake",
        "Cupcakes" to "Cupcake",
        "Cookies" to "Cookie",
        "Brownies & Bars" to "Brownie & Bar",
        "Breads" to "Bread",
        "Pastries" to "Pastry",
        "Pies" to "Pie",
        "Tarts" to "Tart",
        "Muffins" to "Muffin",
        "Donuts" to "Donut",
        "Croissants" to "Croissant",
        "Macarons" to "Macaron",
        "Cheesecakes" to "Cheesecake",
        "Pancakes & Waffles" to "Pancake & Waffle"
    )

    fun singular(category: String): String = singulars[category] ?: category
}
