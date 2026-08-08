package com.example.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.Date
import kotlin.math.pow

class ConvertersViewModel : ViewModel() {

    // ==========================================
    // 1. UNIT CONVERTER STATE & LOGIC
    // ==========================================
    enum class UnitCategory { LENGTH, WEIGHT, AREA, VOLUME, TEMPERATURE, SPEED, TIME }
    
    private val _unitCategory = MutableStateFlow(UnitCategory.LENGTH)
    val unitCategory: StateFlow<UnitCategory> = _unitCategory.asStateFlow()

    private val _unitInput = MutableStateFlow("")
    val unitInput: StateFlow<String> = _unitInput.asStateFlow()

    private val _fromUnit = MutableStateFlow("m")
    val fromUnit: StateFlow<String> = _fromUnit.asStateFlow()

    private val _toUnit = MutableStateFlow("km")
    val toUnit: StateFlow<String> = _toUnit.asStateFlow()

    private val _unitOutput = MutableStateFlow("")
    val unitOutput: StateFlow<String> = _unitOutput.asStateFlow()

    fun setUnitCategory(category: UnitCategory) {
        _unitCategory.value = category
        // Reset defaults per category
        when (category) {
            UnitCategory.LENGTH -> { _fromUnit.value = "m"; _toUnit.value = "km" }
            UnitCategory.WEIGHT -> { _fromUnit.value = "kg"; _toUnit.value = "lb" }
            UnitCategory.AREA -> { _fromUnit.value = "sq_m"; _toUnit.value = "sq_km" }
            UnitCategory.VOLUME -> { _fromUnit.value = "l"; _toUnit.value = "ml" }
            UnitCategory.TEMPERATURE -> { _fromUnit.value = "C"; _toUnit.value = "F" }
            UnitCategory.SPEED -> { _fromUnit.value = "m_s"; _toUnit.value = "km_h" }
            UnitCategory.TIME -> { _fromUnit.value = "sec"; _toUnit.value = "min" }
        }
        calculateUnitConversion()
    }

    fun updateUnitInput(input: String) {
        _unitInput.value = input
        calculateUnitConversion()
    }

    fun setFromUnit(unit: String) {
        _fromUnit.value = unit
        calculateUnitConversion()
    }

    fun setToUnit(unit: String) {
        _toUnit.value = unit
        calculateUnitConversion()
    }

    private fun calculateUnitConversion() {
        val value = _unitInput.value.toDoubleOrNull()
        if (value == null) {
            _unitOutput.value = ""
            return
        }

        val from = _fromUnit.value
        val to = _toUnit.value
        val category = _unitCategory.value

        val result = when (category) {
            UnitCategory.LENGTH -> convertLength(value, from, to)
            UnitCategory.WEIGHT -> convertWeight(value, from, to)
            UnitCategory.AREA -> convertArea(value, from, to)
            UnitCategory.VOLUME -> convertVolume(value, from, to)
            UnitCategory.TEMPERATURE -> convertTemperature(value, from, to)
            UnitCategory.SPEED -> convertSpeed(value, from, to)
            UnitCategory.TIME -> convertTime(value, from, to)
        }

        _unitOutput.value = "%.6f".format(java.util.Locale.US, result).trimEnd('0').trimEnd('.')
    }

    private fun convertLength(value: Double, from: String, to: String): Double {
        // Base: meters (m)
        val inMeters = when (from) {
            "mm" -> value / 1000.0
            "cm" -> value / 100.0
            "m" -> value
            "km" -> value * 1000.0
            "in" -> value * 0.0254
            "ft" -> value * 0.3048
            "yd" -> value * 0.9144
            "mi" -> value * 1609.344
            else -> value
        }
        return when (to) {
            "mm" -> inMeters * 1000.0
            "cm" -> inMeters * 100.0
            "m" -> inMeters
            "km" -> inMeters / 1000.0
            "in" -> inMeters / 0.0254
            "ft" -> inMeters / 0.3048
            "yd" -> inMeters / 0.9144
            "mi" -> inMeters / 1609.344
            else -> inMeters
        }
    }

    private fun convertWeight(value: Double, from: String, to: String): Double {
        // Base: kilograms (kg)
        val inKg = when (from) {
            "mg" -> value / 1000000.0
            "g" -> value / 1000.0
            "kg" -> value
            "lb" -> value * 0.45359237
            "oz" -> value * 0.028349523
            "ton" -> value * 907.18474
            else -> value
        }
        return when (to) {
            "mg" -> inKg * 1000000.0
            "g" -> inKg * 1000.0
            "kg" -> inKg
            "lb" -> inKg / 0.45359237
            "oz" -> inKg / 0.028349523
            "ton" -> inKg / 907.18474
            else -> inKg
        }
    }

    private fun convertArea(value: Double, from: String, to: String): Double {
        // Base: square meters (sq_m)
        val inSqM = when (from) {
            "sq_cm" -> value / 10000.0
            "sq_m" -> value
            "sq_km" -> value * 1000000.0
            "sq_in" -> value * 0.00064516
            "sq_ft" -> value * 0.09290304
            "acre" -> value * 4046.8564
            "hectare" -> value * 10000.0
            else -> value
        }
        return when (to) {
            "sq_cm" -> inSqM * 10000.0
            "sq_m" -> inSqM
            "sq_km" -> inSqM / 1000000.0
            "sq_in" -> inSqM / 0.00064516
            "sq_ft" -> inSqM / 0.09290304
            "acre" -> inSqM / 4046.8564
            "hectare" -> inSqM / 10000.0
            else -> inSqM
        }
    }

    private fun convertVolume(value: Double, from: String, to: String): Double {
        // Base: liters (l)
        val inLiters = when (from) {
            "ml" -> value / 1000.0
            "l" -> value
            "gal" -> value * 3.78541
            "qt" -> value * 0.946353
            "pt" -> value * 0.473176
            "cup" -> value * 0.236588
            "fl_oz" -> value * 0.0295735
            else -> value
        }
        return when (to) {
            "ml" -> inLiters * 1000.0
            "l" -> inLiters
            "gal" -> inLiters / 3.78541
            "qt" -> inLiters / 0.946353
            "pt" -> inLiters / 0.473176
            "cup" -> inLiters / 0.236588
            "fl_oz" -> inLiters / 0.0295735
            else -> inLiters
        }
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        val celsius = when (from) {
            "C" -> value
            "F" -> (value - 32.0) * 5.0 / 9.0
            "K" -> value - 273.15
            else -> value
        }
        return when (to) {
            "C" -> celsius
            "F" -> celsius * 9.0 / 5.0 + 32.0
            "K" -> celsius + 273.15
            else -> celsius
        }
    }

    private fun convertSpeed(value: Double, from: String, to: String): Double {
        // Base: meters/second (m_s)
        val inMs = when (from) {
            "m_s" -> value
            "km_h" -> value / 3.6
            "mi_h" -> value * 0.44704
            "knot" -> value * 0.514444
            else -> value
        }
        return when (to) {
            "m_s" -> inMs
            "km_h" -> inMs * 3.6
            "mi_h" -> inMs / 0.44704
            "knot" -> inMs / 0.514444
            else -> inMs
        }
    }

    private fun convertTime(value: Double, from: String, to: String): Double {
        // Base: seconds (sec)
        val inSec = when (from) {
            "ms" -> value / 1000.0
            "sec" -> value
            "min" -> value * 60.0
            "hr" -> value * 3600.0
            "day" -> value * 86400.0
            "week" -> value * 604800.0
            else -> value
        }
        return when (to) {
            "ms" -> inSec * 1000.0
            "sec" -> inSec
            "min" -> inSec / 60.0
            "hr" -> inSec / 3600.0
            "day" -> inSec / 86400.0
            "week" -> inSec / 604800.0
            else -> inSec
        }
    }

    // ==========================================
    // 2. CURRENCY CONVERTER STATE & LOGIC
    // ==========================================
    private val _currencyInput = MutableStateFlow("")
    val currencyInput: StateFlow<String> = _currencyInput.asStateFlow()

    private val _currencyFrom = MutableStateFlow("USD")
    val currencyFrom: StateFlow<String> = _currencyFrom.asStateFlow()

    private val _currencyTo = MutableStateFlow("EUR")
    val currencyTo: StateFlow<String> = _currencyTo.asStateFlow()

    private val _currencyOutput = MutableStateFlow("")
    val currencyOutput: StateFlow<String> = _currencyOutput.asStateFlow()

    // Highly comprehensive offline base currency rates referenced to USD (1 USD = Rate)
    private val currencyRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "JPY" to 155.20,
        "AUD" to 1.50,
        "CAD" to 1.37,
        "INR" to 83.50,
        "CNY" to 7.25,
        "BDT" to 117.50,
        "AED" to 3.67,
        "SAR" to 3.75,
        "SGD" to 1.35
    )

    fun updateCurrencyInput(input: String) {
        _currencyInput.value = input
        calculateCurrency()
    }

    fun setCurrencyFrom(curr: String) {
        _currencyFrom.value = curr
        calculateCurrency()
    }

    fun setCurrencyTo(curr: String) {
        _currencyTo.value = curr
        calculateCurrency()
    }

    private fun calculateCurrency() {
        val value = _currencyInput.value.toDoubleOrNull()
        if (value == null) {
            _currencyOutput.value = ""
            return
        }

        val fromRate = currencyRates[_currencyFrom.value] ?: 1.0
        val toRate = currencyRates[_currencyTo.value] ?: 1.0

        // Convert to USD, then to target currency
        val valueInUsd = value / fromRate
        val result = valueInUsd * toRate

        _currencyOutput.value = "%.2f".format(java.util.Locale.US, result)
    }

    // ==========================================
    // 3. BMI CALCULATOR STATE & LOGIC
    // ==========================================
    private val _bmiHeight = MutableStateFlow("")
    val bmiHeight: StateFlow<String> = _bmiHeight.asStateFlow()

    private val _bmiWeight = MutableStateFlow("")
    val bmiWeight: StateFlow<String> = _bmiWeight.asStateFlow()

    private val _bmiIsMetric = MutableStateFlow(true)
    val bmiIsMetric: StateFlow<Boolean> = _bmiIsMetric.asStateFlow()

    private val _bmiResult = MutableStateFlow("")
    val bmiResult: StateFlow<String> = _bmiResult.asStateFlow()

    private val _bmiCategory = MutableStateFlow("")
    val bmiCategory: StateFlow<String> = _bmiCategory.asStateFlow()

    fun updateBmiHeight(height: String) {
        _bmiHeight.value = height
        calculateBmi()
    }

    fun updateBmiWeight(weight: String) {
        _bmiWeight.value = weight
        calculateBmi()
    }

    fun toggleBmiUnit(isMetric: Boolean) {
        _bmiIsMetric.value = isMetric
        _bmiHeight.value = ""
        _bmiWeight.value = ""
        _bmiResult.value = ""
        _bmiCategory.value = ""
    }

    private fun calculateBmi() {
        val hVal = _bmiHeight.value.toDoubleOrNull()
        val wVal = _bmiWeight.value.toDoubleOrNull()

        if (hVal == null || wVal == null || hVal <= 0 || wVal <= 0) {
            _bmiResult.value = ""
            _bmiCategory.value = ""
            return
        }

        val bmi = if (_bmiIsMetric.value) {
            // Height in cm, weight in kg -> bmi = kg / (m^2)
            val hInMeters = hVal / 100.0
            wVal / (hInMeters.pow(2.0))
        } else {
            // Height in inches, weight in lbs -> bmi = 703 * lbs / (inches^2)
            703.0 * wVal / (hVal.pow(2.0))
        }

        _bmiResult.value = "%.1f".format(java.util.Locale.US, bmi)
        _bmiCategory.value = when {
            bmi < 18.5 -> "Underweight"
            bmi in 18.5..24.9 -> "Normal"
            bmi in 25.0..29.9 -> "Overweight"
            else -> "Obese"
        }
    }

    // ==========================================
    // 4. AGE CALCULATOR STATE & LOGIC
    // ==========================================
    private val _birthYear = MutableStateFlow(2000)
    val birthYear: StateFlow<Int> = _birthYear.asStateFlow()

    private val _birthMonth = MutableStateFlow(0) // 0-indexed (Jan = 0)
    val birthMonth: StateFlow<Int> = _birthMonth.asStateFlow()

    private val _birthDay = MutableStateFlow(1)
    val birthDay: StateFlow<Int> = _birthDay.asStateFlow()

    private val _ageResultYears = MutableStateFlow(0)
    val ageResultYears: StateFlow<Int> = _ageResultYears.asStateFlow()

    private val _ageResultMonths = MutableStateFlow(0)
    val ageResultMonths: StateFlow<Int> = _ageResultMonths.asStateFlow()

    private val _ageResultDays = MutableStateFlow(0)
    val ageResultDays: StateFlow<Int> = _ageResultDays.asStateFlow()

    private val _nextBirthdayMonths = MutableStateFlow(0)
    val nextBirthdayMonths: StateFlow<Int> = _nextBirthdayMonths.asStateFlow()

    private val _nextBirthdayDays = MutableStateFlow(0)
    val nextBirthdayDays: StateFlow<Int> = _nextBirthdayDays.asStateFlow()

    fun updateBirthDate(year: Int, month: Int, day: Int) {
        _birthYear.value = year
        _birthMonth.value = month
        _birthDay.value = day
        calculateAge()
    }

    private fun calculateAge() {
        val today = Calendar.getInstance()
        val birth = Calendar.getInstance().apply {
            set(Calendar.YEAR, _birthYear.value)
            set(Calendar.MONTH, _birthMonth.value)
            set(Calendar.DAY_OF_MONTH, _birthDay.value)
        }

        if (birth.after(today)) {
            _ageResultYears.value = 0
            _ageResultMonths.value = 0
            _ageResultDays.value = 0
            _nextBirthdayDays.value = 0
            _nextBirthdayMonths.value = 0
            return
        }

        var years = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
        var months = today.get(Calendar.MONTH) - birth.get(Calendar.MONTH)
        var days = today.get(Calendar.DAY_OF_MONTH) - birth.get(Calendar.DAY_OF_MONTH)

        if (days < 0) {
            months--
            val prevMonth = (today.get(Calendar.MONTH) - 1 + 12) % 12
            val yearOfPrevMonth = if (prevMonth == 11) today.get(Calendar.YEAR) - 1 else today.get(Calendar.YEAR)
            val temp = Calendar.getInstance().apply {
                set(Calendar.YEAR, yearOfPrevMonth)
                set(Calendar.MONTH, prevMonth)
            }
            days += temp.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        if (months < 0) {
            years--
            months += 12
        }

        _ageResultYears.value = years
        _ageResultMonths.value = months
        _ageResultDays.value = days

        // Calculate next birthday countdown
        val nextBday = Calendar.getInstance().apply {
            set(Calendar.YEAR, today.get(Calendar.YEAR))
            set(Calendar.MONTH, birth.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, birth.get(Calendar.DAY_OF_MONTH))
        }

        if (nextBday.before(today) || nextBday.equals(today)) {
            nextBday.add(Calendar.YEAR, 1)
        }

        var nextMonths = nextBday.get(Calendar.MONTH) - today.get(Calendar.MONTH)
        var nextDays = nextBday.get(Calendar.DAY_OF_MONTH) - today.get(Calendar.DAY_OF_MONTH)

        if (nextDays < 0) {
            nextMonths--
            val prevMonth = (nextBday.get(Calendar.MONTH) - 1 + 12) % 12
            val temp = Calendar.getInstance().apply {
                set(Calendar.MONTH, prevMonth)
            }
            nextDays += temp.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (nextMonths < 0) {
            nextMonths += 12
        }

        _nextBirthdayMonths.value = nextMonths
        _nextBirthdayDays.value = nextDays
    }

    // ==========================================
    // 5. PERCENTAGE CALCULATOR STATE & LOGIC
    // ==========================================
    private val _pctValueX1 = MutableStateFlow("")
    val pctValueX1: StateFlow<String> = _pctValueX1.asStateFlow()

    private val _pctValueY1 = MutableStateFlow("")
    val pctValueY1: StateFlow<String> = _pctValueY1.asStateFlow()

    private val _pctResult1 = MutableStateFlow("")
    val pctResult1: StateFlow<String> = _pctResult1.asStateFlow()

    private val _pctValueX2 = MutableStateFlow("")
    val pctValueX2: StateFlow<String> = _pctValueX2.asStateFlow()

    private val _pctValueY2 = MutableStateFlow("")
    val pctValueY2: StateFlow<String> = _pctValueY2.asStateFlow()

    private val _pctResult2 = MutableStateFlow("")
    val pctResult2: StateFlow<String> = _pctResult2.asStateFlow()

    fun updatePct1(x: String, y: String) {
        _pctValueX1.value = x
        _pctValueY1.value = y
        val dx = x.toDoubleOrNull()
        val dy = y.toDoubleOrNull()
        if (dx != null && dy != null) {
            // What is X% of Y?
            val res = (dx / 100.0) * dy
            _pctResult1.value = "%.4f".format(java.util.Locale.US, res).trimEnd('0').trimEnd('.')
        } else {
            _pctResult1.value = ""
        }
    }

    fun updatePct2(x: String, y: String) {
        _pctValueX2.value = x
        _pctValueY2.value = y
        val dx = x.toDoubleOrNull()
        val dy = y.toDoubleOrNull()
        if (dx != null && dy != null && dy != 0.0) {
            // X is what % of Y?
            val res = (dx / dy) * 100.0
            _pctResult2.value = "%.4f%%".format(java.util.Locale.US, res)
        } else {
            _pctResult2.value = ""
        }
    }

    // ==========================================
    // 6. GST/VAT CALCULATOR STATE & LOGIC
    // ==========================================
    private val _gstAmount = MutableStateFlow("")
    val gstAmount: StateFlow<String> = _gstAmount.asStateFlow()

    private val _gstRate = MutableStateFlow("15")
    val gstRate: StateFlow<String> = _gstRate.asStateFlow()

    private val _gstIsAdd = MutableStateFlow(true)
    val gstIsAdd: StateFlow<Boolean> = _gstIsAdd.asStateFlow()

    private val _gstNet = MutableStateFlow("")
    val gstNet: StateFlow<String> = _gstNet.asStateFlow()

    private val _gstTax = MutableStateFlow("")
    val gstTax: StateFlow<String> = _gstTax.asStateFlow()

    private val _gstGross = MutableStateFlow("")
    val gstGross: StateFlow<String> = _gstGross.asStateFlow()

    fun updateGst(amount: String, rate: String, isAdd: Boolean) {
        _gstAmount.value = amount
        _gstRate.value = rate
        _gstIsAdd.value = isAdd

        val amt = amount.toDoubleOrNull()
        val rt = rate.toDoubleOrNull()

        if (amt == null || rt == null || amt < 0 || rt < 0) {
            _gstNet.value = ""
            _gstTax.value = ""
            _gstGross.value = ""
            return
        }

        if (isAdd) {
            // Amount is NET, add GST
            val tax = amt * (rt / 100.0)
            val gross = amt + tax
            _gstNet.value = "%.2f".format(java.util.Locale.US, amt)
            _gstTax.value = "%.2f".format(java.util.Locale.US, tax)
            _gstGross.value = "%.2f".format(java.util.Locale.US, gross)
        } else {
            // Amount is GROSS, extract GST
            val net = amt / (1.0 + rt / 100.0)
            val tax = amt - net
            _gstNet.value = "%.2f".format(java.util.Locale.US, net)
            _gstTax.value = "%.2f".format(java.util.Locale.US, tax)
            _gstGross.value = "%.2f".format(java.util.Locale.US, amt)
        }
    }

    // ==========================================
    // 7. DISCOUNT CALCULATOR STATE & LOGIC
    // ==========================================
    private val _discPrice = MutableStateFlow("")
    val discPrice: StateFlow<String> = _discPrice.asStateFlow()

    private val _discPercent = MutableStateFlow("")
    val discPercent: StateFlow<String> = _discPercent.asStateFlow()

    private val _discAddPercent = MutableStateFlow("")
    val discAddPercent: StateFlow<String> = _discAddPercent.asStateFlow()

    private val _discFinal = MutableStateFlow("")
    val discFinal: StateFlow<String> = _discFinal.asStateFlow()

    private val _discSaved = MutableStateFlow("")
    val discSaved: StateFlow<String> = _discSaved.asStateFlow()

    fun updateDiscount(price: String, pct: String, addPct: String) {
        _discPrice.value = price
        _discPercent.value = pct
        _discAddPercent.value = addPct

        val p = price.toDoubleOrNull()
        val d1 = pct.toDoubleOrNull() ?: 0.0
        val d2 = addPct.toDoubleOrNull() ?: 0.0

        if (p == null || p < 0) {
            _discFinal.value = ""
            _discSaved.value = ""
            return
        }

        // Apply first discount
        val priceAfterD1 = p * (1.0 - d1.coerceIn(0.0, 100.0) / 100.0)
        // Apply second discount on the already discounted price
        val finalPrice = priceAfterD1 * (1.0 - d2.coerceIn(0.0, 100.0) / 100.0)
        val saved = p - finalPrice

        _discFinal.value = "%.2f".format(java.util.Locale.US, finalPrice)
        _discSaved.value = "%.2f".format(java.util.Locale.US, saved)
    }

    // ==========================================
    // 8. LOAN EMI CALCULATOR STATE & LOGIC
    // ==========================================
    private val _emiPrincipal = MutableStateFlow("")
    val emiPrincipal: StateFlow<String> = _emiPrincipal.asStateFlow()

    private val _emiRate = MutableStateFlow("")
    val emiRate: StateFlow<String> = _emiRate.asStateFlow()

    private val _emiTenureYears = MutableStateFlow("")
    val emiTenureYears: StateFlow<String> = _emiTenureYears.asStateFlow()

    private val _emiTenureMonths = MutableStateFlow("")
    val emiTenureMonths: StateFlow<String> = _emiTenureMonths.asStateFlow()

    private val _emiMonthly = MutableStateFlow("")
    val emiMonthly: StateFlow<String> = _emiMonthly.asStateFlow()

    private val _emiTotalInterest = MutableStateFlow("")
    val emiTotalInterest: StateFlow<String> = _emiTotalInterest.asStateFlow()

    private val _emiTotalPayment = MutableStateFlow("")
    val emiTotalPayment: StateFlow<String> = _emiTotalPayment.asStateFlow()

    fun updateEmi(principal: String, rate: String, years: String, months: String) {
        _emiPrincipal.value = principal
        _emiRate.value = rate
        _emiTenureYears.value = years
        _emiTenureMonths.value = months

        val p = principal.toDoubleOrNull()
        val r = rate.toDoubleOrNull()
        val y = years.toIntOrNull() ?: 0
        val m = months.toIntOrNull() ?: 0

        if (p == null || r == null || p <= 0 || r < 0 || (y == 0 && m == 0)) {
            _emiMonthly.value = ""
            _emiTotalInterest.value = ""
            _emiTotalPayment.value = ""
            return
        }

        // Monthly interest rate
        val rMonthly = (r / 12.0) / 100.0
        val totalMonths = y * 12 + m

        if (totalMonths <= 0) return

        val emi = if (rMonthly == 0.0) {
            p / totalMonths
        } else {
            // EMI = [P x R x (1+R)^N]/[(1+R)^N-1]
            (p * rMonthly * (1.0 + rMonthly).pow(totalMonths.toDouble())) / 
                    ((1.0 + rMonthly).pow(totalMonths.toDouble()) - 1.0)
        }

        val totalPayment = emi * totalMonths
        val totalInterest = totalPayment - p

        _emiMonthly.value = "%.2f".format(java.util.Locale.US, emi)
        _emiTotalInterest.value = "%.2f".format(java.util.Locale.US, totalInterest)
        _emiTotalPayment.value = "%.2f".format(java.util.Locale.US, totalPayment)
    }

    // ==========================================
    // 9. TIP CALCULATOR STATE & LOGIC
    // ==========================================
    private val _tipBill = MutableStateFlow("")
    val tipBill: StateFlow<String> = _tipBill.asStateFlow()

    private val _tipPercent = MutableStateFlow("15")
    val tipPercent: StateFlow<String> = _tipPercent.asStateFlow()

    private val _tipPeople = MutableStateFlow("1")
    val tipPeople: StateFlow<String> = _tipPeople.asStateFlow()

    private val _tipAmountPerPerson = MutableStateFlow("")
    val tipAmountPerPerson: StateFlow<String> = _tipAmountPerPerson.asStateFlow()

    private val _tipTotalPerPerson = MutableStateFlow("")
    val tipTotalPerPerson: StateFlow<String> = _tipTotalPerPerson.asStateFlow()

    fun updateTip(bill: String, pct: String, people: String) {
        _tipBill.value = bill
        _tipPercent.value = pct
        _tipPeople.value = people

        val b = bill.toDoubleOrNull()
        val p = pct.toDoubleOrNull() ?: 0.0
        val num = people.toIntOrNull() ?: 1

        if (b == null || b < 0 || num <= 0) {
            _tipAmountPerPerson.value = ""
            _tipTotalPerPerson.value = ""
            return
        }

        val totalTip = b * (p / 100.0)
        val totalBill = b + totalTip
        
        val tipPerPerson = totalTip / num
        val totalPerPerson = totalBill / num

        _tipAmountPerPerson.value = "%.2f".format(java.util.Locale.US, tipPerPerson)
        _tipTotalPerPerson.value = "%.2f".format(java.util.Locale.US, totalPerPerson)
    }
}
