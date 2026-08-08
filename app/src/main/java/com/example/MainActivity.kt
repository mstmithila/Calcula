package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.database.CalculatorDatabase
import com.example.navigation.AppNavigation
import com.example.repository.AppThemeMode
import com.example.repository.HistoryRepository
import com.example.repository.SettingsRepository
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CalculatorViewModel
import com.example.viewmodel.ConvertersViewModel
import com.example.viewmodel.ViewModelFactory
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Initialize Database, DAOs, and Repositories
        val database = CalculatorDatabase.getDatabase(this)
        val historyRepository = HistoryRepository(database.historyDao())
        val settingsRepository = SettingsRepository(this)

        // 2. Instantiate ViewModels via Factory (Simple Constructor Injection)
        val factory = ViewModelFactory(
            application = this.application,
            historyRepository = historyRepository,
            settingsRepository = settingsRepository
        )
        
        val calculatorViewModel = ViewModelProvider(this, factory)[CalculatorViewModel::class.java]
        val convertersViewModel = ViewModelProvider(this, factory)[ConvertersViewModel::class.java]

        // 3. Enable edge-to-edge layout
        enableEdgeToEdge()

        setContent {
            // Observe settings reactively to apply the user's preferred theme
            val appSettings by settingsRepository.settings.collectAsState()
            
            val isDarkTheme = when (appSettings.themeMode) {
                AppThemeMode.SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            var showSplash by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                delay(2000) // Beautiful 2-second splash screen experience
                showSplash = false
            }

            MyApplicationTheme(
                themeName = appSettings.colorTheme,
                darkTheme = isDarkTheme,
                dynamicColor = false // Force custom themed palettes for beautiful consistency
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimatedContent(
                        targetState = showSplash,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                        },
                        label = "SplashTransition"
                    ) { splashActive ->
                        if (splashActive) {
                            SplashScreenContent()
                        } else {
                            AppNavigation(
                                calculatorViewModel = calculatorViewModel,
                                convertersViewModel = convertersViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreenContent() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val backgroundColor = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Block
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(primaryColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = "App Logo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Text Typography
            Text(
                text = "SCIENTIFIC",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                color = secondaryColor
            )
            
            Text(
                text = "Calculator",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Premium Edition",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Premium loader
            CircularProgressIndicator(
                color = primaryColor,
                strokeWidth = 3.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
