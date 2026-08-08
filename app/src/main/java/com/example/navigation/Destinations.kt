package com.example.navigation

sealed class Screen(val route: String, val title: String) {
    object Calculator : Screen("calculator", "Calculator")
    object History : Screen("history", "History")
    object ExtraTools : Screen("extra_tools", "Tools")
    object Settings : Screen("settings", "Settings")
    
    // Extra tools sub-screens
    object UnitConverter : Screen("unit_converter", "Unit Converter")
    object CurrencyConverter : Screen("currency_converter", "Currency Converter")
    object BmiCalculator : Screen("bmi_calculator", "BMI Calculator")
    object AgeCalculator : Screen("age_calculator", "Age Calculator")
    object PercentageCalculator : Screen("percentage_calculator", "Percentage")
    object GstCalculator : Screen("gst_calculator", "GST/VAT Calculator")
    object DiscountCalculator : Screen("discount_calculator", "Discount")
    object EmiCalculator : Screen("emi_calculator", "EMI Calculator")
    object TipCalculator : Screen("tip_calculator", "Tip Calculator")
}
