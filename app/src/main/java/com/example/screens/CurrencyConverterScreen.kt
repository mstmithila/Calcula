package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.viewmodel.ConvertersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(
    viewModel: ConvertersViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val input by viewModel.currencyInput.collectAsState()
    val fromCurr by viewModel.currencyFrom.collectAsState()
    val toCurr by viewModel.currencyTo.collectAsState()
    val output by viewModel.currencyOutput.collectAsState()

    var showFromMenu by remember { mutableStateOf(false) }
    var showToMenu by remember { mutableStateOf(false) }

    val currencies = remember {
        listOf("USD", "EUR", "GBP", "JPY", "AUD", "CAD", "INR", "CNY", "BDT", "AED", "SAR", "SGD")
    }

    val currencyLabels = remember {
        mapOf(
            "USD" to "US Dollar (USD)",
            "EUR" to "Euro (EUR)",
            "GBP" to "British Pound (GBP)",
            "JPY" to "Japanese Yen (JPY)",
            "AUD" to "Australian Dollar (AUD)",
            "CAD" to "Canadian Dollar (CAD)",
            "INR" to "Indian Rupee (INR)",
            "CNY" to "Chinese Yuan (CNY)",
            "BDT" to "Bangladeshi Taka (BDT)",
            "AED" to "UAE Dirham (AED)",
            "SAR" to "Saudi Riyal (SAR)",
            "SGD" to "Singapore Dollar (SGD)"
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Currency Converter", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Input Column
                    Column {
                        Text(
                            text = "From",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Dropdown trigger
                            Box {
                                OutlinedButton(
                                    onClick = { showFromMenu = true },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.width(130.dp)
                                ) {
                                    Text(
                                        text = fromCurr,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                                DropdownMenu(
                                    expanded = showFromMenu,
                                    onDismissRequest = { showFromMenu = false }
                                ) {
                                    currencies.forEach { curr ->
                                        DropdownMenuItem(
                                            text = { Text(currencyLabels[curr] ?: curr) },
                                            onClick = {
                                                viewModel.setCurrencyFrom(curr)
                                                showFromMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Text input field
                            OutlinedTextField(
                                value = input,
                                onValueChange = { viewModel.updateCurrencyInput(it) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text("0.00") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // Divider separator
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Output Column
                    Column {
                        Text(
                            text = "To",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Dropdown trigger
                            Box {
                                OutlinedButton(
                                    onClick = { showToMenu = true },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.width(130.dp)
                                ) {
                                    Text(
                                        text = toCurr,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                                DropdownMenu(
                                    expanded = showToMenu,
                                    onDismissRequest = { showToMenu = false }
                                ) {
                                    currencies.forEach { curr ->
                                        DropdownMenuItem(
                                            text = { Text(currencyLabels[curr] ?: curr) },
                                            onClick = {
                                                viewModel.setCurrencyTo(curr)
                                                showToMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Text output panel (readonly)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = output.ifEmpty { "0.00" },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Offline Note
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Offline conversion info",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Standard conversion rates are loaded offline automatically. Rates are estimated referenced against USD.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
