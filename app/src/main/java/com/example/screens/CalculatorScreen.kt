package com.example.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.repository.AngleMode
import com.example.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val expression by viewModel.expression.collectAsState()
    val result by viewModel.result.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val memory by viewModel.memoryValue.collectAsState()

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    var showSciInPortrait by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Scientific Calculator", 
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    ) 
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.toggleAngleMode()
                        }
                    ) {
                        Text(
                            text = if (settings.angleMode == AngleMode.DEGREE) "DEG" else "RAD",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (memory != 0.0) {
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "M",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Displays Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isLandscape) 1.2f else 2f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                // Expression display
                val exprScrollState = rememberScrollState()
                LaunchedEffect(expression) {
                    exprScrollState.animateScrollTo(exprScrollState.maxValue)
                }
                Text(
                    text = expression.ifEmpty { "0" },
                    fontSize = if (expression.length > 15) 24.sp else 36.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(exprScrollState),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Result display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (result.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(result))
                                Toast.makeText(context, "Copied result: $result", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy result",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    val resultScrollState = rememberScrollState()
                    Text(
                        text = result,
                        fontSize = if (result.length > 10) 36.sp else 48.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(resultScrollState),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Keyboard / Buttons Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isLandscape) 2f else 4.5f)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(8.dp)
            ) {
                // Memory keys row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("MC", "MR", "M+", "M-", "MS").forEach { memKey ->
                        Text(
                            text = memKey,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    when (memKey) {
                                        "MC" -> viewModel.memoryClear()
                                        "MR" -> viewModel.memoryRecall()
                                        "M+" -> viewModel.memoryAdd()
                                        "M-" -> viewModel.memorySubtract()
                                        "MS" -> viewModel.memoryStore()
                                    }
                                }
                                .padding(vertical = 6.dp, horizontal = 12.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (isLandscape) {
                    // Landscape layout: side-by-side (Scientific left, basic right)
                    Row(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.weight(1f)) {
                            ScientificKeypad(viewModel)
                        }
                        Column(modifier = Modifier.weight(1.2f)) {
                            BasicKeypad(viewModel)
                        }
                    }
                } else {
                    // Portrait layout: collapsible scientific keys, and basic keys below
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showSciInPortrait) "Scientific Mode" else "Basic Mode",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        IconButton(onClick = { showSciInPortrait = !showSciInPortrait }) {
                            Icon(
                                imageVector = if (showSciInPortrait) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Toggle scientific keys",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = showSciInPortrait,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            ScientificKeypad(viewModel)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        BasicKeypad(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ScientificKeypad(viewModel: CalculatorViewModel) {
    val keys = listOf(
        listOf("sin", "cos", "tan", "ln"),
        listOf("asin", "acos", "atan", "log"),
        listOf("sinh", "cosh", "tanh", "sqrt"),
        listOf("π", "e", "abs", "cbrt"),
        listOf("^", "!", "%", "rand")
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { key ->
                    CalcButton(
                        text = key,
                        onClick = {
                            when (key) {
                                "π" -> viewModel.append("π")
                                "e" -> viewModel.append("e")
                                "rand" -> viewModel.append("rand")
                                "%" -> viewModel.append("%")
                                "^" -> viewModel.append("^")
                                "!" -> viewModel.append("!")
                                else -> viewModel.appendFunction(key)
                            }
                        },
                        backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun BasicKeypad(viewModel: CalculatorViewModel) {
    val rows = listOf(
        listOf("C", "(", ")", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "⌫", "=")
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { key ->
                    val isOperator = key in listOf("÷", "×", "-", "+", "=")
                    val isAction = key in listOf("C", "(", ")", "⌫")
                    val isEquals = key == "="
                    val bg = when {
                        isEquals -> MaterialTheme.colorScheme.primary
                        isOperator -> MaterialTheme.colorScheme.secondaryContainer
                        isAction -> MaterialTheme.colorScheme.surfaceVariant
                        else -> MaterialTheme.colorScheme.surface
                    }
                    val fg = when {
                        isEquals -> MaterialTheme.colorScheme.onPrimary
                        isOperator -> MaterialTheme.colorScheme.onSecondaryContainer
                        isAction -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    CalcButton(
                        text = key,
                        onClick = {
                            when (key) {
                                "C" -> viewModel.clear()
                                "⌫" -> viewModel.backspace()
                                "=" -> viewModel.evaluate()
                                else -> viewModel.append(key)
                            }
                        },
                        backgroundColor = bg,
                        contentColor = fg,
                        modifier = Modifier.weight(1f),
                        fontWeight = if (isOperator || isAction) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun CalcButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Box(
        modifier = modifier
            .aspectRatio(1.8f)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (text == "⌫") {
            Icon(
                imageVector = Icons.Default.Backspace,
                contentDescription = "Backspace",
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = text,
                color = contentColor,
                fontSize = if (text.length > 3) 14.sp else 20.sp,
                fontWeight = fontWeight,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
