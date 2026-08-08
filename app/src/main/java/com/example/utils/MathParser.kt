package com.example.utils

import kotlin.math.*

class MathParser(private val isDegreeMode: Boolean = true) {

    fun evaluate(expression: String): Double {
        if (expression.trim().isEmpty()) return 0.0
        val sanitized = sanitizeExpression(expression)
        return parse(sanitized)
    }

    private fun sanitizeExpression(expr: String): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", "pi")
            .replace("mod", "%")
            .replace("e", "e") // e is already lowercase
    }

    private fun parse(str: String): Double {
        var pos = -1
        var ch = '\u0000'

        fun nextChar() {
            ch = if (++pos < str.length) str[pos] else '\u0000'
        }

        fun eat(charToEat: Char): Boolean {
            while (ch == ' ') nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        // Forward declarations
        var parseExpression: (() -> Double)? = null
        var parseTerm: (() -> Double)? = null
        var parseFactor: (() -> Double)? = null

        parseExpression = {
            var x = parseTerm!!.invoke()
            while (true) {
                if (eat('+')) x += parseTerm!!.invoke() // addition
                else if (eat('-')) x -= parseTerm!!.invoke() // subtraction
                else break
            }
            x
        }

        parseTerm = {
            var x = parseFactor!!.invoke()
            while (true) {
                if (eat('*')) x *= parseFactor!!.invoke() // multiplication
                else if (eat('/')) {
                    val divisor = parseFactor!!.invoke()
                    if (divisor == 0.0) throw ArithmeticException("Division by zero")
                    x /= divisor // division
                } else break
            }
            x
        }

        parseFactor = {
            if (eat('+')) parseFactor!!.invoke() // unary plus
            else if (eat('-')) -parseFactor!!.invoke() // unary minus
            else {
                var x: Double
                val startPos = pos
                if (eat('(')) { // parentheses
                    x = parseExpression.invoke()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // numbers
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    val numStr = str.substring(startPos, pos)
                    x = numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid number: $numStr")
                } else if (ch in 'a'..'z') { // functions and constants
                    while (ch in 'a'..'z') nextChar()
                    val id = str.substring(startPos, pos)
                    if (id == "pi") {
                        x = Math.PI
                    } else if (id == "e") {
                        x = Math.E
                    } else if (id == "rand") {
                        x = Math.random()
                    } else {
                        // It's a function
                        if (!eat('(')) throw IllegalArgumentException("Missing parenthesis after function: $id")
                        val arg = parseExpression.invoke()
                        eat(')')
                        x = when (id) {
                            "sqrt" -> {
                                if (arg < 0) throw ArithmeticException("Square root of negative number")
                                sqrt(arg)
                            }
                            "cbrt" -> Math.cbrt(arg)
                            "sin" -> if (isDegreeMode) sin(Math.toRadians(arg)) else sin(arg)
                            "cos" -> if (isDegreeMode) cos(Math.toRadians(arg)) else cos(arg)
                            "tan" -> {
                                val radians = if (isDegreeMode) Math.toRadians(arg) else arg
                                // Tan is undefined for 90 + 180k degrees
                                val checkCos = cos(radians)
                                if (abs(checkCos) < 1e-15) throw ArithmeticException("Tangent undefined")
                                tan(radians)
                            }
                            "asin" -> {
                                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Arcsin domain error")
                                if (isDegreeMode) Math.toDegrees(asin(arg)) else asin(arg)
                            }
                            "acos" -> {
                                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Arccos domain error")
                                if (isDegreeMode) Math.toDegrees(acos(arg)) else acos(arg)
                            }
                            "atan" -> if (isDegreeMode) Math.toDegrees(atan(arg)) else atan(arg)
                            "sinh" -> sinh(arg)
                            "cosh" -> cosh(arg)
                            "tanh" -> tanh(arg)
                            "log" -> {
                                if (arg <= 0) throw ArithmeticException("Log domain error")
                                log10(arg)
                            }
                            "ln" -> {
                                if (arg <= 0) throw ArithmeticException("Ln domain error")
                                ln(arg)
                            }
                            "abs" -> abs(arg)
                            "exp" -> exp(arg)
                            else -> throw IllegalArgumentException("Unknown function: $id")
                        }
                    }
                } else {
                    throw IllegalArgumentException("Unexpected character: $ch")
                }

                // Postfix operators like power, factorial, percentage
                while (true) {
                    if (eat('^')) {
                        x = x.pow(parseFactor!!.invoke()) // exponentiation
                    } else if (eat('!')) {
                        x = factorial(x)
                    } else if (eat('%')) {
                        x /= 100.0
                    } else {
                        break
                    }
                }
                x
            }
        }

        nextChar()
        val x = parseExpression.invoke()
        if (pos < str.length) throw IllegalArgumentException("Unexpected trailing character: ${str[pos]}")
        return x
    }

    private fun factorial(n: Double): Double {
        if (n < 0.0) throw ArithmeticException("Factorial of negative number undefined")
        if (n > 170.0) throw ArithmeticException("Overflow")
        if (n != floor(n)) {
            return gamma(n + 1.0)
        }
        val num = n.toInt()
        var result = 1.0
        for (i in 2..num) {
            result *= i
        }
        return result
    }

    private fun gamma(x: Double): Double {
        val g = 7
        val p = doubleArrayOf(
            0.99999999999980993, 676.5203681218851, -1259.1392167224028,
            771.32342877765313, -176.61502916214059, 12.507343278686905,
            -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7
        )
        if (x < 0.5) {
            return Math.PI / (sin(Math.PI * x) * gamma(1.0 - x))
        }
        val z = x - 1.0
        var a = p[0]
        for (i in 1 until p.size) {
            a += p[i] / (z + i)
        }
        val t = z + g + 0.5
        return sqrt(2.0 * Math.PI) * t.pow(z + 0.5) * exp(-t) * a
    }
}
