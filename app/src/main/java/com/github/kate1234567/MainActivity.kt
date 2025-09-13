package com.github.kate1234567

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.kate1234567.ui.theme.CalculatorTheme
import androidx.compose.runtime.saveable.rememberSaveable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var display by rememberSaveable { mutableStateOf("0") }
    var operand1 by rememberSaveable { mutableStateOf<Double?>(null) }
    var pendingOp by rememberSaveable { mutableStateOf<String?>(null) }
    var userTyping by rememberSaveable { mutableStateOf(false) }

    val buttons = listOf(
        listOf("C", "DEL", "/", "*"),
        listOf("7", "8", "9", "-"),
        listOf("4", "5", "6", "+"),
        listOf("1", "2", "3", "="),
        listOf("0", ".", "±", "%")
    )

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.LightGray, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = display,
                modifier = Modifier.padding(16.dp),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }

        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { label ->
                    val buttonColor = when {
                        label in listOf("C", "DEL") -> Color(0xFFFF5C5C)
                        label in listOf("+", "-", "*", "/", "=") -> Color(0xFFFFA500)
                        else -> Color(0xFF90CAF9)
                    }

                    Button(
                        onClick = {
                            when (label) {
                                "C" -> {
                                    display = "0"
                                    operand1 = null
                                    pendingOp = null
                                    userTyping = false
                                }
                                "DEL" -> {
                                    display = if (display.length <= 1) "0" else display.dropLast(1)
                                    userTyping = display != "0"
                                }
                                "+", "-", "*", "/" -> {
                                    operand1 = display.toDoubleOrNull()
                                    pendingOp = label
                                    userTyping = false
                                }
                                "=" -> {
                                    val value = display.toDoubleOrNull()
                                    if (operand1 != null && pendingOp != null && value != null) {
                                        val result = performOperation(operand1!!, value, pendingOp!!)
                                        display = if (result.isNaN()) "Ошибка" else result.trimDouble()
                                        operand1 = null
                                        pendingOp = null
                                        userTyping = false
                                    }
                                }
                                "." -> {
                                    if (!display.contains('.')) display += "."
                                    userTyping = true
                                }
                                "±" -> {
                                    if (display != "0") display = if (display.startsWith("-")) display.drop(1) else "-$display"
                                }
                                "%" -> {
                                    val value = display.toDoubleOrNull()
                                    if (value != null) display = (value / 100).trimDouble()
                                }
                                else -> {
                                    display = if (!userTyping || display == "0") label else display + label
                                    userTyping = true
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                    ) {
                        Text(text = label, fontSize = 24.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

fun performOperation(a: Double, b: Double, op: String): Double {
    return when (op) {
        "+" -> a + b
        "-" -> a - b
        "*" -> a * b
        "/" -> if (b == 0.0) Double.NaN else a / b
        else -> b
    }
}

fun Double.trimDouble(): String {
    return if (this == this.toLong().toDouble()) String.format("%d", this.toLong()) else this.toString()
}
