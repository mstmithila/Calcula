package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun BmiCalculatorScreen(
    viewModel: ConvertersViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val height by viewModel.bmiHeight.collectAsState()
    val weight by viewModel.bmiWeight.collectAsState()
    val isMetric by viewModel.bmiIsMetric.collectAsState()
    val bmiResult by viewModel.bmiResult.collectAsState()
    val bmiCategory by viewModel.bmiCategory.collectAsState()

    val categoryColor = remember(bmiCategory) {
        when (bmiCategory) {
            "Underweight" -> Color(0xFF29B6F6) // light blue
            "Normal" -> Color(0xFF66BB6A) // green
            "Overweight" -> Color(0xFFFFA726) // orange
            "Obese" -> Color(0xFFEF5350) // red
            else -> Color.Gray
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("BMI Calculator", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Metric / Imperial units selector
            TabRow(
                selectedTabIndex = if (isMetric) 0 else 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = isMetric,
                    onClick = { viewModel.toggleBmiUnit(true) },
                    text = { Text("Metric (kg, cm)", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = !isMetric,
                    onClick = { viewModel.toggleBmiUnit(false) },
                    text = { Text("Imperial (lb, in)", fontWeight = FontWeight.Bold) }
                )
            }

            // Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Enter Body Metrics",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Height Input
                    OutlinedTextField(
                        value = height,
                        onValueChange = { viewModel.updateBmiHeight(it) },
                        label = { Text(if (isMetric) "Height (cm)" else "Height (inches)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Weight Input
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { viewModel.updateBmiWeight(it) },
                        label = { Text(if (isMetric) "Weight (kg)" else "Weight (lbs)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Result Display Card
            if (bmiResult.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = categoryColor.copy(alpha = 0.12f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "YOUR BMI INDEX SCORE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = categoryColor,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = bmiResult,
                            fontSize = 54.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = categoryColor,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                        Box(
                            modifier = Modifier
                                .background(categoryColor, RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = bmiCategory,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom color-coded metric visualizer meter
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray.copy(alpha = 0.3f))
                        ) {
                            listOf(
                                "Underweight" to Color(0xFF29B6F6),
                                "Normal" to Color(0xFF66BB6A),
                                "Overweight" to Color(0xFFFFA726),
                                "Obese" to Color(0xFFEF5350)
                            ).forEach { (cat, col) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(if (cat == bmiCategory) col else col.copy(alpha = 0.2f))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
