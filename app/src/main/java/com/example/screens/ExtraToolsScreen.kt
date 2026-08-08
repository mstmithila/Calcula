package com.example.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navigation.Screen

data class ToolItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val destination: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtraToolsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val tools = remember {
        listOf(
            ToolItem("Unit Converter", "Convert length, weight, area, volume, temperature, speed, etc.", Icons.Default.LinearScale, Screen.UnitConverter.route),
            ToolItem("Currency Converter", "Exchange rates for popular global currencies.", Icons.Default.CurrencyExchange, Screen.CurrencyConverter.route),
            ToolItem("BMI Calculator", "Track body weight metrics and health categories.", Icons.Default.FitnessCenter, Screen.BmiCalculator.route),
            ToolItem("Age Calculator", "Find exact age span and next birthday countdowns.", Icons.Default.Cake, Screen.AgeCalculator.route),
            ToolItem("Percentage", "Solve portions, differences, and percentage margins.", Icons.Default.Percent, Screen.PercentageCalculator.route),
            ToolItem("GST/VAT", "Add or remove goods and service taxes instantly.", Icons.Default.ReceiptLong, Screen.GstCalculator.route),
            ToolItem("Discount", "Determine savings, final pricing, and markdowns.", Icons.Default.LocalOffer, Screen.DiscountCalculator.route),
            ToolItem("EMI Calculator", "Analyze monthly installments and total payable interest.", Icons.Default.AccountBalance, Screen.EmiCalculator.route),
            ToolItem("Tip Calculator", "Split bills and shares easily among friends.", Icons.Default.Restaurant, Screen.TipCalculator.route)
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Extra Calculators & Tools", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(tools) { tool ->
                ToolCard(tool = tool) {
                    navController.navigate(tool.destination)
                }
            }
        }
    }
}

@Composable
fun ToolCard(
    tool: ToolItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.95f)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = tool.title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = tool.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = tool.description,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                lineHeight = 15.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
