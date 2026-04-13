package com.example.simplecalculator

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var tvResult: TextView
    private lateinit var tvExpression: TextView

    private var currentNumber = ""
    private var firstNumber = 0.0
    private var currentOperation = ""
    private var isNewNumber = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)
        tvExpression = findViewById(R.id.tvExpression)

        // Все кнопки включая btnClearAll
        val buttons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnDoubleZero, R.id.btnDecimal, R.id.btnClear,
            R.id.btnClearAll, R.id.btnPercent, R.id.btnAdd,
            R.id.btnSubtract, R.id.btnMultiply, R.id.btnDivide, R.id.btnEquals
        )

        for (id in buttons) {
            findViewById<Button>(id).setOnClickListener(this)
        }

        tvResult.text = "0"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn0 -> addNumber("0")
            R.id.btn1 -> addNumber("1")
            R.id.btn2 -> addNumber("2")
            R.id.btn3 -> addNumber("3")
            R.id.btn4 -> addNumber("4")
            R.id.btn5 -> addNumber("5")
            R.id.btn6 -> addNumber("6")
            R.id.btn7 -> addNumber("7")
            R.id.btn8 -> addNumber("8")
            R.id.btn9 -> addNumber("9")
            R.id.btnDoubleZero -> addNumber("00")
            R.id.btnDecimal -> addDecimal()
            R.id.btnClear -> clearCurrent()
            R.id.btnClearAll -> clearAll()      // AC - полная очистка
            R.id.btnPercent -> percent()
            R.id.btnAdd -> setOperation("+")
            R.id.btnSubtract -> setOperation("-")
            R.id.btnMultiply -> setOperation("×")
            R.id.btnDivide -> setOperation("÷")
            R.id.btnEquals -> calculate()
        }
    }

    private fun addNumber(number: String) {
        if (isNewNumber) {
            currentNumber = ""
            isNewNumber = false
        }
        if (currentNumber.length < 15) {
            currentNumber += number
            if (currentNumber.startsWith("0") && !currentNumber.startsWith("0.") && currentNumber.length > 1) {
                currentNumber = currentNumber.replaceFirst("^0+".toRegex(), "")
            }
            updateDisplay()
        }
    }

    private fun addDecimal() {
        if (isNewNumber) {
            currentNumber = "0"
            isNewNumber = false
        }
        if (!currentNumber.contains(".")) {
            currentNumber += "."
            updateDisplay()
        }
    }

    private fun clearCurrent() {
        currentNumber = ""
        isNewNumber = true
        tvResult.text = "0"
    }

    private fun clearAll() {
        currentNumber = ""
        firstNumber = 0.0
        currentOperation = ""
        isNewNumber = true
        tvResult.text = "0"
        tvExpression.text = ""
    }

    private fun percent() {
        if (currentNumber.isNotEmpty() && currentNumber != "-") {
            try {
                val number = currentNumber.toDouble()
                currentNumber = (number / 100).toString()
                if (currentNumber.endsWith(".0")) {
                    currentNumber = currentNumber.substring(0, currentNumber.length - 2)
                }
                updateDisplay()
            } catch (e: Exception) {
                currentNumber = "0"
                updateDisplay()
            }
        }
    }

    private fun setOperation(op: String) {
        if (currentNumber.isNotEmpty() && currentNumber != "-") {
            if (firstNumber != 0.0 && currentOperation.isNotEmpty() && !isNewNumber) {
                calculate()
            }
            firstNumber = currentNumber.toDouble()
            currentOperation = op
            tvExpression.text = "${formatNumber(firstNumber)} $op"
            isNewNumber = true
        }
    }

    private fun calculate() {
        if (currentNumber.isNotEmpty() && currentOperation.isNotEmpty() && currentNumber != "-") {
            try {
                val secondNumber = currentNumber.toDouble()
                val result: Double

                when (currentOperation) {
                    "+" -> result = firstNumber + secondNumber
                    "-" -> result = firstNumber - secondNumber
                    "×" -> result = firstNumber * secondNumber
                    "÷" -> {
                        if (secondNumber == 0.0) {
                            Toast.makeText(this, "На ноль делить нельзя!", Toast.LENGTH_SHORT).show()
                            clearAll()
                            return
                        }
                        result = firstNumber / secondNumber
                    }
                    else -> return
                }

                tvExpression.text = "${formatNumber(firstNumber)} $currentOperation ${formatNumber(secondNumber)} ="

                val resultText = formatNumber(result)
                tvResult.text = resultText
                currentNumber = resultText
                currentOperation = ""
                isNewNumber = true
                firstNumber = result

            } catch (e: Exception) {
                Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show()
                clearAll()
            }
        }
    }

    private fun formatNumber(number: Double): String {
        return if (number == number.toLong().toDouble()) {
            number.toLong().toString()
        } else {
            var formatted = "%.8f".format(number).trimEnd('0').trimEnd('.')
            if (formatted.length > 12) formatted = formatted.substring(0, 12)
            formatted
        }
    }

    private fun updateDisplay() {
        var display = currentNumber
        if (display.isEmpty()) display = "0"
        tvResult.text = display
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(0, 1, 0, "Reset")
        menu?.add(0, 2, 0, "Quit")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                clearAll()
                Toast.makeText(this, "Очищено", Toast.LENGTH_SHORT).show()
                return true
            }
            2 -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}