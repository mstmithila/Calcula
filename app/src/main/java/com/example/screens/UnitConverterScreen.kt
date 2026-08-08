package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.viewmodel.ConvertersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    viewModel: ConvertersViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val category by viewModel.unitCategory.collectAsState()
    val input by viewModel.unitInput.collectAsState()
    val fromUnit by viewModel.fromUnit.collectAsState()
    val toUnit by viewModel.toUnit.collectAsState()
    val output by viewModel.unitOutput.collectAsState()

    var showFromMenu by remember { mutableStateOf(false) }
    var showToMenu by remember { mutableStateOf(false) }

    val categories = remember { ConvertersViewModel.UnitCategory.values() }

    val unitLabels = remember(category) {
        when (category) {
            ConvertersViewModel.UnitCategory.LENGTH -> mapOf("mm" to "Millimeter (mm)", "cm" to "Centimeter (cm)", "m" to "Meter (m)", "km" to "Kilometer (km)", "in" to "Inch (in)", "ft" to "Foot (ft)", "yd" to "Yard (yd)", "mi" to "Mile (mi)")
            ConvertersViewModel.UnitCategory.WEIGHT -> mapOf("mg" to "Milligram (mg)", "g" to "Gram (g)", "kg" to "Kilogram (kg)", "lb" to "Pound (lb)", "oz" to "Ounce (oz)", "ton" to "Ton (ton)")
            ConvertersViewModel.UnitCategory.AREA -> mapOf("sq_cm" to "Sq Centimeter (cm²)", "sq_m" to "Sq Meter (m²)", "sq_km" to "Sq Kilometer (km²)", "sq_in" to "Sq Inch (in²)", "sq_ft" to "Sq Foot (ft²)", "acre" to "Acre", "hectare" to "Hectare")
            ConvertersViewModel.UnitCategory.VOLUME -> mapOf("ml" to "Milliliter (ml)", "l" to "Liter (l)", "gal" to "Gallon (gal)", "qt" to "Quart (qt)", "pt" to "Point (pt)", "cup" to "Cup", "fl_oz" to "Fluid Ounce (fl oz)")
            ConvertersViewModel.UnitCategory.TEMPERATURE -> mapOf("C" to "Celsius (°C)", "F" to "Fahrenheit (°F)", "K" to "Kelvin (K)")
            ConvertersViewModel.UnitCategory.SPEED -> mapOf("m_s" to "Meters/Sec (m/s)", "km_h" to "Kilometers/Hour (km/h)", "mi_h" to "Miles/Hour (mph)", "knot" to "Knot")
            ConvertersViewModel.UnitCategory.TIME -> mapOf("ms" to "Millisecond (ms)", "sec" to "Second (s)", "min" to "Minute (min)", "hr" to "Hour (h)", "day" to "Day (d)", "week" to "Week (w)")
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Unit Converter", fontWeight = FontWeight.Bold) },
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
        ) {
            // Category scroll row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setUnitCategory(cat) },
                        label = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body content card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
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
                                        text = fromUnit,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                                DropdownMenu(
                                    expanded = showFromMenu,
                                    onDismissRequest = { showFromMenu = false }
                                ) {
                                    unitLabels.forEach { (key, label) ->
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = {
                                                viewModel.setFromUnit(key)
                                                showFromMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Text input field
                            OutlinedTextField(
                                value = input,
                                onValueChange = { viewModel.updateUnitInput(it) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                placeholder = { Text("0") },
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
                                        text = toUnit,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                }
                                DropdownMenu(
                                    expanded = showToMenu,
                                    onDismissRequest = { showToMenu = false }
                                ) {
                                    unitLabels.forEach { (key, label) ->
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = {
                                                viewModel.setToUnit(key)
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
                                    text = output.ifEmpty { "0" },
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
        }
    }
}
